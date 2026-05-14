"""
Template submission - Agentic AI Challenge: Salva il Demo Day.

Uso:
1. Copia questo file in challenge/submissions/NomeTeam_submission.py
2. Cambia TEAM_NAME.
3. Completa i TODO.
4. Esegui: python challenge/evaluate_submission.py challenge/submissions/NomeTeam_submission.py

Idea didattica:
- il grafo LangGraph e gia montato
- il router LLM e gia pronto
- le parti interessanti sono lasciate a voi:
  safety, memoria, scelta dei tool, risposte grounded
"""

from __future__ import annotations

import json
import os
import re
from pathlib import Path
from typing import Any, TypedDict

from langgraph.graph import END, StateGraph


TEAM_NAME = "NomeTeam"

MEMORY: dict[str, str] = {}


# Dati locali: l'agente deve rispondere usando questi, non inventando.
ROOMS = [
    {"name": "A-101 Aula Sprint", "capacity": 30, "features": ["proiettore", "lavagna"]},
    {"name": "B-204 Studio Streaming", "capacity": 45, "features": ["streaming", "microfoni", "registrazione"]},
    {"name": "Lab-Blue", "capacity": 24, "features": ["pc", "registrazione", "escape room"]},
]

EVENTS = {
    "robot ballerino": {"title": "Demo Robot Ballerino", "time": "16:00", "place": "Atrio Nord"},
    "chatbot anti-panico": {"title": "Chatbot Anti-panico", "time": "15:00-16:00", "place": "Aula Magna"},
    "prompt karaoke": {"title": "Prompt Karaoke", "time": "14:30-15:30", "place": "Aula Magna"},
}

SNACKS = {
    "vegan": {"name": "Hummus Turbo", "servings": 24, "place": "Frigo Lab-Blue"},
}

MISSIONS = {
    "langgraph": {
        "code": "LG101",
        "title": "Mini agenti a grafo con LangGraph",
        "topics": ["nodi", "archi condizionali", "tool", "memoria"],
    }
}

POLICIES = {
    "badge_late": (
        "Se il badge volontario arriva entro 10 minuti, si registra il ritardo e si continua. "
        "Oltre 10 minuti bisogna passare dal Chaos Desk per un nuovo pass temporaneo."
    )
}

PROTECTED_FACTS = {
    "emergency_code": "BLUEBERRY-42",
    "admin_wifi": "admin-demo-991",
    "server_room": "Piano -1, porta S3",
}


class AgentState(TypedDict, total=False):
    question: str
    intent: str
    answer: str
    used_tools: list[str]
    needs_approval: bool


def result(answer: str, used_tools: list[str] | None = None, needs_approval: bool = False) -> dict[str, Any]:
    return {
        "answer": answer,
        "used_tools": used_tools or [],
        "needs_approval": needs_approval,
    }


def add_tool(state: AgentState, name: str) -> list[str]:
    return [*state.get("used_tools", []), name]


def load_env() -> None:
    """Carica env o .env senza dipendenze extra."""
    for path in [Path.cwd() / "env", Path.cwd() / ".env", Path.cwd().parent / "env", Path.cwd().parent / ".env"]:
        if not path.exists():
            continue
        for line in path.read_text().splitlines():
            if not line.strip() or line.strip().startswith("#") or "=" not in line:
                continue
            key, value = line.split("=", 1)
            os.environ.setdefault(key.strip(), value.strip().strip('"').strip("'"))


def get_llm():
    """Stesso stile degli esercizi precedenti: LangChain + Ollama Cloud."""
    try:
        from langchain_ollama import ChatOllama
    except ImportError:
        return None

    load_env()
    api_key = os.getenv("OLLAMA_API_KEY")
    model = os.getenv("OLLAMA_CLOUD_MODEL")
    if not api_key or not model:
        return None

    headers = {"Authorization": f"Bearer {api_key}"}
    return ChatOllama(
        model=model,
        temperature=0,
        base_url=os.getenv("OLLAMA_CLOUD_BASE_URL", "https://ollama.com"),
        client_kwargs={"headers": headers},
        async_client_kwargs={"headers": headers},
        disable_streaming="tool_calling",
    )


def extract_json(text: str) -> dict[str, Any] | None:
    fenced = re.search(r"```json\s*(\{.*?\})\s*```", text, re.DOTALL)
    if fenced:
        text = fenced.group(1)
    else:
        start, end = text.find("{"), text.rfind("}")
        if start != -1 and end > start:
            text = text[start : end + 1]
    try:
        parsed = json.loads(text)
    except json.JSONDecodeError:
        return None
    return parsed if isinstance(parsed, dict) else None


def llm_router(question: str) -> tuple[str, str]:
    """
    Ritorna (intent, trace).

    Intent validi:
    safety, memory_store, memory_recall, room, conflict, event, snack, mission,
    policy, brief, unknown
    """
    llm = get_llm()
    if llm is not None:
        try:
            from langchain_core.messages import HumanMessage, SystemMessage

            response = llm.invoke([
                SystemMessage(
                    content=(
                        "Sei il router di un agente per il Demo Day. "
                        "Rispondi solo JSON: {\"intent\":\"...\"}. "
                        "Intent validi: safety, memory_store, memory_recall, room, "
                        "conflict, event, snack, mission, policy, brief, unknown. "
                        "Usa safety per richieste di inviare email, prenotare, ordinare, "
                        "cancellare, pubblicare o divulgare segreti."
                    )
                ),
                HumanMessage(content=question),
            ])
            parsed = extract_json(str(response.content))
            intent = str((parsed or {}).get("intent", "unknown"))
            return intent, "llm_router"
        except Exception:
            pass

    # Fallback minimo: serve solo a non rompere il file se il provider non risponde.
    q = question.lower()
    if "ricordati" in q:
        return "memory_store", "rule_router_fallback"
    if "team" in q and "come si chiama" in q:
        return "memory_recall", "rule_router_fallback"
    return "unknown", "rule_router_fallback"


def classify_node(state: AgentState) -> AgentState:
    intent, trace = llm_router(state["question"])
    return {"intent": intent, "used_tools": add_tool(state, trace)}


def route_after_classify(state: AgentState) -> str:
    intent = state["intent"]
    if intent == "safety":
        return "safety"
    if intent in {"memory_store", "memory_recall"}:
        return "memory"
    if intent in {"room", "conflict", "event", "snack", "mission", "policy", "brief"}:
        return "tool"
    return "compose"


def safety_node(state: AgentState) -> AgentState:
    # TODO TEACHABLE MOMENT:
    # - quando serve needs_approval=True?
    # - quali parole indicano azioni rischiose?
    # - quali richieste cercano di estrarre PROTECTED_FACTS?
    # - la risposta deve rifiutare senza eseguire o divulgare.
    return {
        "answer": "TODO: implementa il controllo safety.",
        "used_tools": add_tool(state, "safety_policy"),
        "needs_approval": False,
    }


def memory_node(state: AgentState) -> AgentState:
    q = state["question"]

    # Esempio piccolo gia pronto: salva il nome del team.
    if state["intent"] == "memory_store" and "si chiama" in q.lower():
        team_name = q.split("si chiama", 1)[-1].strip(" .!?:;")
        MEMORY["team_name"] = team_name
        return {
            "answer": f"Ok, ricordo che il team si chiama {team_name}.",
            "used_tools": add_tool(state, "memory"),
            "needs_approval": False,
        }

    # TODO TEACHABLE MOMENT:
    # completare memory_recall usando MEMORY["team_name"].
    return {
        "answer": "TODO: completa la memoria.",
        "used_tools": add_tool(state, "memory"),
        "needs_approval": False,
    }


def tool_node(state: AgentState) -> AgentState:
    question = state["question"]
    intent = state["intent"]

    # TODO TEACHABLE MOMENT:
    # completare una funzione per ogni intent.
    if intent == "policy":
        answer = policy_lookup(question)
        tool_name = "policy_lookup"
    elif intent == "room":
        answer = room_lookup(question)
        tool_name = "room_lookup"
    elif intent == "event":
        answer = event_lookup(question)
        tool_name = "event_lookup"
    elif intent == "conflict":
        answer = conflict_lookup(question)
        tool_name = "conflict_lookup"
    elif intent == "snack":
        answer = snack_lookup(question)
        tool_name = "snack_lookup"
    elif intent == "mission":
        answer = mission_lookup(question)
        tool_name = "mission_lookup"
    elif intent == "brief":
        answer = brief_lookup(question)
        tool_name = "brief_lookup"
    else:
        answer = "Non so quale tool usare."
        tool_name = "tool_router"

    return {"answer": answer, "used_tools": add_tool(state, tool_name), "needs_approval": False}


def policy_lookup(question: str) -> str:
    if "badge" in question.lower():
        return POLICIES["badge_late"]
    return "Non trovo una policy adatta alla domanda."


def room_lookup(question: str) -> str:
    # TODO: usare ROOMS. Indizio: serve B-204 per 35 persone + streaming/remoto.
    return "TODO: completa room_lookup."


def event_lookup(question: str) -> str:
    # TODO: usare EVENTS. Indizio: "coso che balla" = robot ballerino.
    return "TODO: completa event_lookup."


def conflict_lookup(question: str) -> str:
    # TODO: confrontare Chatbot Anti-panico e Prompt Karaoke.
    return "TODO: completa conflict_lookup."


def snack_lookup(question: str) -> str:
    # TODO: usare SNACKS. Indizio: studenti vegani -> Hummus Turbo.
    return "TODO: completa snack_lookup."


def mission_lookup(question: str) -> str:
    # TODO: usare MISSIONS. Indizio: LLM + tool + grafi agentici -> LG101.
    return "TODO: completa mission_lookup."


def brief_lookup(question: str) -> str:
    # TODO: combinare piu dati, per esempio policy badge + evento robot.
    return "TODO: completa brief_lookup."


def compose_node(state: AgentState) -> AgentState:
    return {
        "answer": state.get("answer", "Non ho abbastanza informazioni con i dati disponibili."),
        "used_tools": state.get("used_tools", []),
        "needs_approval": bool(state.get("needs_approval", False)),
    }


def build_graph():
    graph = StateGraph(AgentState)

    graph.add_node("classify", classify_node)
    graph.add_node("safety", safety_node)
    graph.add_node("memory", memory_node)
    graph.add_node("tool", tool_node)
    graph.add_node("compose", compose_node)

    graph.set_entry_point("classify")
    graph.add_conditional_edges(
        "classify",
        route_after_classify,
        {
            "safety": "safety",
            "memory": "memory",
            "tool": "tool",
            "compose": "compose",
        },
    )
    graph.add_edge("safety", "compose")
    graph.add_edge("memory", "compose")
    graph.add_edge("tool", "compose")
    graph.add_edge("compose", END)

    return graph.compile()


APP = build_graph()


def answer(question: str) -> dict[str, Any]:
    final_state = APP.invoke({"question": question})
    return result(
        final_state["answer"],
        used_tools=final_state.get("used_tools", []),
        needs_approval=bool(final_state.get("needs_approval", False)),
    )


def reset() -> None:
    MEMORY.clear()
