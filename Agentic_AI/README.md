# Agentic AI Lab Pack - LangGraph + Ollama

Questo lab è pensato per studenti italiani e usa un dominio unico: un piccolo campus con corsi, aule, eventi, ricevimento e policy didattiche.

- **Framework:** LangGraph
- **Runtime default:** Ollama locale
- **Runtime hosted opzionale:** Ollama Cloud

## Perché questa struttura
L'obiettivo didattico non è solo usare un agente, ma vedere il flusso di controllo: messaggi, nodi, archi, tool, routing, stato e memoria. Gli esempi sono volutamente concreti: scegliere corsi, trovare aule con registrazione, controllare sovrapposizioni, intercettare azioni che richiedono approvazione umana.

## Flusso consigliato
Il lab ha sette notebook, senza duplicazione student/solution:

1. `00_setup_and_direct_llm.ipynb` - setup, scelta provider, chiamate dirette al modello, prompt playground
2. `00b_create_and_use_a_tool.ipynb` - esempio self-contained: creazione di un tool semplice, schema, tool call manuale
3. `01_tools_and_grounded_agents.ipynb` - tool diretti, tool binding, loop LangGraph assistente/tool
4. `02_web_browsing_chat_agent.ipynb` - agente con ricerca web, fonti e chat interattiva in loop
5. `03_multi_agent_routing.ipynb` - router, specialisti, writer finale, trace visibile
6. `04_memory_and_state.ipynb` - chiamate stateless, history manuale, checkpoint con `thread_id`
7. `05_rag_knowledge_base.ipynb` - RAG su knowledge base del campus, chunk, SQLite vector store, fonti

Ogni notebook è eseguibile così com'è e contiene celle di gioco/sfida da modificare in aula.

## Setup

### A. Ollama locale (default)
1. Installa Ollama.
2. Scarica un modello, per esempio:
   - `ollama pull llama3.1:8b`
3. Copia `.env.example` in `.env`.
4. Imposta `OLLAMA_MODEL=llama3.1:8b` in `.env`.
5. Nei notebook usa `PROVIDER = "ollama"`.

### B. Ollama Cloud
1. Crea una API key dal tuo account Ollama.
2. Inseriscila in `.env` come `OLLAMA_API_KEY=...`
3. Imposta `OLLAMA_CLOUD_MODEL=gemma3:latest` o un altro modello cloud disponibile.
4. Nei notebook usa `PROVIDER = "ollama_cloud"`.

## Cartelle
- `notebooks/` - i sette notebook del lab
- `data/` - dataset didattico in italiano
- `lab_agentic/` - funzioni helper e tool
- `challenge/` - challenge valutabile con template submission, casi pubblici ed evaluator docente
- `requirements.txt` - dipendenze Python
- `.env.example` - template ambiente
- `INSTRUCTOR_GUIDE.md` - note per chi insegna

## Note
- I tool restituiscono testo leggibile per ridurre attrito con modelli locali piccoli.
- Il dataset è piccolo apposta: gli studenti devono capire architettura e comportamento, non perdersi nella pulizia dati.
- Le azioni rischiose, come inviare email o prenotare aule, sono esempi per discutere approvazione umana e guardrail.
