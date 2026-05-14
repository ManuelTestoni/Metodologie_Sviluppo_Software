from __future__ import annotations

import csv
import json
import os
import re
from pathlib import Path
from typing import Any, Dict, List, Optional

from dotenv import load_dotenv

DATA_DIR = Path(__file__).resolve().parent.parent / "data"
ENV_FILE = Path(__file__).resolve().parent.parent / ".env"

def load_env() -> None:
    if ENV_FILE.exists():
        load_dotenv(ENV_FILE, override=False)
    else:
        load_dotenv(override=False)

def load_json(filename: str) -> Any:
    return json.loads((DATA_DIR / filename).read_text())

def load_markdown(filename: str) -> str:
    return (DATA_DIR / filename).read_text()

def load_rooms() -> List[Dict[str, Any]]:
    with open(DATA_DIR / "rooms.csv", newline="") as f:
        return list(csv.DictReader(f))

def search_courses(keyword: str) -> List[Dict[str, Any]]:
    keyword = keyword.lower().strip()
    rows = []
    for row in load_json("course_catalog.json"):
        hay = " ".join([
            row["code"],
            row["title"],
            row["instructor"],
            " ".join(row["keywords"]),
            row["description"],
        ]).lower()
        if keyword in hay:
            rows.append(row)
    return rows

def get_course(code: str) -> Optional[Dict[str, Any]]:
    code = code.upper().strip()
    for row in load_json("course_catalog.json"):
        if row["code"].upper() == code:
            return row
    return None

def room_recommendation(min_capacity: int, needs_recording: bool = False) -> List[Dict[str, Any]]:
    out = []
    for row in load_rooms():
        if int(row["capacity"]) < int(min_capacity):
            continue
        equipment = row["equipment"].lower()
        if needs_recording and "recording" not in equipment and "registrazione" not in equipment:
            continue
        out.append(row)
    return sorted(out, key=lambda x: int(x["capacity"]))

def office_hours_lookup(person: str) -> str:
    text = load_markdown("policies.md")
    mapping = {
        "sofia bianchi": "Dr.ssa Sofia Bianchi: mercoledi 15:00-17:00 in A112",
        "luca ferri": "Prof. Luca Ferri: giovedi 11:00-12:00 online",
        "elena rossi": "Dr.ssa Elena Rossi: martedi 16:00-17:00 in C210",
        "marta conti": "Prof.ssa Marta Conti: lunedi 14:00-15:00 in A115",
    }
    person_l = person.lower()
    for key, value in mapping.items():
        if key in person_l:
            return value
    return "Non ho trovato un orario di ricevimento per questa persona nei dati del lab."

def policy_lookup(topic: str) -> str:
    topic_l = topic.lower()
    if any(token in topic_l for token in ["late", "submission", "ritardo", "consegna", "scadenza"]):
        return "Le consegne dei lab sono dovute entro 7 giorni dalla sessione. Le consegne in ritardo perdono il 10% al giorno fino a 3 giorni. Dopo 3 giorni valgono 0, salvo proroga approvata."
    if any(token in topic_l for token in ["safe", "approval", "approvazione", "sicurezza", "email", "prenot", "cancell", "iscriv", "azione esterna", "write action", "change grade", "cambia voto", "cambiare voti"]):
        return "Nessuna azione che modifica sistemi esterni va eseguita senza approvazione umana. Esempi: inviare email, cambiare voti, cancellare file, iscrivere studenti o prenotare aule."
    if any(token in topic_l for token in ["grade", "rubric", "voto", "valutazione", "rubrica"]):
        return "Rubrica: correttezza 40%, qualità del design 25%, spiegabilità e documentazione 20%, test e riproducibilità 15%."
    return "Non ho trovato una policy precisa per questo argomento."

def _parse_meeting_time(slot: str) -> tuple[str, int, int] | None:
    match = re.fullmatch(r"([A-Za-z]{3})\s+(\d{2}):(\d{2})-(\d{2}):(\d{2})", slot.strip())
    if not match:
        return None
    day, start_h, start_m, end_h, end_m = match.groups()
    start = int(start_h) * 60 + int(start_m)
    end = int(end_h) * 60 + int(end_m)
    return day, start, end

def _format_minutes(minutes: int) -> str:
    return f"{minutes // 60:02d}:{minutes % 60:02d}"

def schedule_conflict(code_a: str, code_b: str) -> Dict[str, Any]:
    a = get_course(code_a)
    b = get_course(code_b)
    if not a or not b:
        return {"conflict": None, "reason": "codice corso sconosciuto"}
    overlap = []
    for slot_a in a["meeting_times"]:
        parsed_a = _parse_meeting_time(slot_a)
        if not parsed_a:
            continue
        day_a, start_a, end_a = parsed_a
        for slot_b in b["meeting_times"]:
            parsed_b = _parse_meeting_time(slot_b)
            if not parsed_b:
                continue
            day_b, start_b, end_b = parsed_b
            if day_a != day_b:
                continue
            start = max(start_a, start_b)
            end = min(end_a, end_b)
            if start < end:
                overlap.append(f"{day_a} {_format_minutes(start)}-{_format_minutes(end)}")
    overlap = sorted(set(overlap))
    return {
        "conflict": bool(overlap),
        "overlap_slots": overlap,
        "reason": "sovrapposizione trovata" if overlap else "nessuna sovrapposizione di orario",
    }

def find_event(keyword: str) -> List[Dict[str, Any]]:
    keyword_l = keyword.lower()
    out = []
    for event in load_json("events.json"):
        hay = " ".join([event["name"], event["location"], " ".join(event["tags"])]).lower()
        if keyword_l in hay:
            out.append(event)
    return out

def needs_human_approval(user_query: str) -> bool:
    q = user_query.lower()
    risky = [
        "email", "send", "delete", "enrol", "enroll", "book", "reserve",
        "change grade", "update", "invia", "cancella", "iscrivi",
        "prenota", "riserva", "cambia voto", "aggiorna",
    ]
    return any(token in q for token in risky)

def safe_json_extract(text: str, fallback: Any = None) -> Any:
    text = text.strip()
    fenced = re.search(r"```json\s*(\{.*?\}|\[.*?\])\s*```", text, re.DOTALL)
    if fenced:
        text = fenced.group(1)
    start_candidates = [i for i in [text.find("{"), text.find("[")] if i != -1]
    if start_candidates:
        start = min(start_candidates)
        end_curly = text.rfind("}")
        end_square = text.rfind("]")
        end = max(end_curly, end_square)
        if end > start:
            snippet = text[start:end+1]
            try:
                return json.loads(snippet)
            except Exception:
                pass
    try:
        return json.loads(text)
    except Exception:
        return fallback

def keyword_router(question: str) -> str:
    q = question.lower()
    if any(t in q for t in ["evento", "event", "demo", "workshop", "escape", "clinic", "portfolio", "pizza"]):
        return "events"
    if any(t in q for t in ["policy", "late", "grade", "approval", "safe", "ritardo", "voto", "rubrica", "approvazione", "sicurezza"]):
        return "policy"
    if any(t in q for t in ["room", "capacity", "recording", "hall", "aula", "posti", "capienza", "registrazione", "sala"]):
        return "room"
    if any(t in q for t in ["office hours", "conflict", "schedule", "when", "overlap", "ricevimento", "orario", "sovrapp", "quando"]):
        return "schedule"
    if re.search(r"[A-Z]{2,}\d{3}", question.upper()) or any(t in q for t in ["course", "catalog", "agentic ai", "nlp", "corso", "catalogo", "agenti", "langgraph"]):
        return "catalog"
    return "writer"

def default_provider() -> str:
    load_env()
    if os.getenv("OLLAMA_API_KEY"):
        return "ollama_cloud"
    return "ollama"

def get_chat_model(provider: str | None = None, model: str | None = None, temperature: float = 0):
    load_env()
    provider = (provider or default_provider()).lower()
    if provider in {"ollama", "ollama_cloud", "cloud_ollama"}:
        from langchain_ollama import ChatOllama
        use_cloud = provider in {"ollama_cloud", "cloud_ollama"}
        model = model or os.getenv("OLLAMA_CLOUD_MODEL" if use_cloud else "OLLAMA_MODEL")
        model = model or ("gemma3:latest" if use_cloud else "llama3.1:8b")
        base_url = os.getenv("OLLAMA_CLOUD_BASE_URL" if use_cloud else "OLLAMA_BASE_URL")
        api_key = os.getenv("OLLAMA_API_KEY")
        kwargs = {
            "model": model,
            "temperature": temperature,
            # LangChain's own ChatOllama docs warn that Ollama tool calling should
            # bypass streaming. This keeps normal chat behavior unchanged while
            # switching tool-bound calls to non-streaming invoke mode.
            "disable_streaming": "tool_calling",
        }
        if base_url:
            kwargs["base_url"] = base_url
        elif use_cloud:
            kwargs["base_url"] = "https://ollama.com"
        if use_cloud:
            if not api_key:
                raise ValueError("OLLAMA_API_KEY è richiesta quando provider='ollama_cloud'. Inseriscila in .env.")
            auth_headers = {"Authorization": f"Bearer {api_key}"}
            kwargs["client_kwargs"] = {"headers": auth_headers}
            kwargs["async_client_kwargs"] = {"headers": auth_headers}
        return ChatOllama(**kwargs)
    raise ValueError(f"Unsupported provider: {provider}")

def format_course(row: Dict[str, Any]) -> str:
    return (
        f'{row["code"]} — {row["title"]}\n'
        f'Docente: {row["instructor"]}\n'
        f'Orario: {", ".join(row["meeting_times"])}\n'
        f'Aula: {row["room"]}\n'
        f'Parole chiave: {", ".join(row["keywords"])}'
    )

def format_room(row: Dict[str, Any]) -> str:
    return f'{row["room"]}: capienza {row["capacity"]}; dotazione {row["equipment"]}'

def format_event(row: Dict[str, Any]) -> str:
    return f'{row["name"]}: {row["date"]} alle {row["time"]} in {row["location"]}'

def print_checklist() -> None:
    load_env()
    print(f"Provider di default: {default_provider()}")
    print("Checklist provider")
    print("------------------------")
    print(f"OLLAMA_MODEL: {os.getenv('OLLAMA_MODEL', '(not set, default llama3.1:8b)')}")
    print(f"OLLAMA_CLOUD_MODEL: {os.getenv('OLLAMA_CLOUD_MODEL', '(not set, default gemma3:latest)')}")
    print(f"OLLAMA_CLOUD_BASE_URL: {os.getenv('OLLAMA_CLOUD_BASE_URL', '(not set, default https://ollama.com)')}")
    print(f"Chiave Ollama Cloud presente: {'si' if os.getenv('OLLAMA_API_KEY') else 'no'}")
