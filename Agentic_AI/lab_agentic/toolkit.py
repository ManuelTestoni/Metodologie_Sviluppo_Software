from __future__ import annotations

from langchain_core.tools import tool

from .utils import (
    search_courses,
    get_course,
    room_recommendation,
    office_hours_lookup,
    policy_lookup,
    schedule_conflict,
    find_event,
    format_course,
    format_room,
    format_event,
)

@tool
def search_courses_tool(keyword: str) -> str:
    """Cerca nel catalogo corsi per parola chiave, per esempio agenti, test, NLP o dati."""
    rows = search_courses(keyword)
    if not rows:
        return "Nessun corso corrisponde a quella parola chiave."
    return "\n\n".join(format_course(r) for r in rows[:5])

@tool
def get_course_tool(code: str) -> str:
    """Recupera i dettagli completi di un corso dato un codice, per esempio AI301."""
    row = get_course(code)
    if not row:
        return "Codice corso sconosciuto."
    return format_course(row)

@tool
def room_recommendation_tool(min_capacity: int, needs_recording: bool = False) -> str:
    """Consiglia aule con posti sufficienti, opzionalmente con supporto per registrazione."""
    rows = room_recommendation(min_capacity=min_capacity, needs_recording=needs_recording)
    if not rows:
        return "Nessuna aula corrisponde a questi requisiti."
    return "\n".join(format_room(r) for r in rows)

@tool
def office_hours_tool(person: str) -> str:
    """Cerca l'orario di ricevimento di una docente o di un docente."""
    return office_hours_lookup(person)

@tool
def policy_lookup_tool(topic: str) -> str:
    """Cerca una policy didattica su consegne in ritardo, valutazione, sicurezza o approvazioni."""
    return policy_lookup(topic)

@tool
def schedule_conflict_tool(code_a: str, code_b: str) -> str:
    """Controlla se due corsi hanno una sovrapposizione di orario."""
    result = schedule_conflict(code_a, code_b)
    return str(result)

@tool
def event_lookup_tool(keyword: str) -> str:
    """Trova un evento del campus per parola chiave, per esempio hackathon, demo o workshop."""
    rows = find_event(keyword)
    if not rows:
        return "Nessun evento corrisponde a quella parola chiave."
    return "\n".join(format_event(r) for r in rows)

LAB_TOOLS = [
    search_courses_tool,
    get_course_tool,
    room_recommendation_tool,
    office_hours_tool,
    policy_lookup_tool,
    schedule_conflict_tool,
    event_lookup_tool,
]
