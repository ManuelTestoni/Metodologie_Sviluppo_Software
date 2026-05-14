# Guida docente

## Default consigliato
Per un lab in presenza:

- LangGraph come framework principale
- Ollama locale come default
- Ollama Cloud come opzione hosted se serve piu velocita o stabilita

La forma dell'API resta Ollama in entrambi i casi, quindi gli studenti non devono cambiare stack mentale.

## Modelli suggeriti
- Locale: `llama3.1:8b`
- Cloud: `gemma3:latest` o un modello Ollama Cloud disponibile
- Macchine deboli: modello Ollama più piccolo, accettando risposte meno stabili

## Strategia didattica
- Notebook 00: far ottenere subito una risposta dal modello, poi far cambiare tono, pubblico e vincoli.
- Notebook 00b: far creare un tool piccolo e self-contained, provarlo direttamente e mostrare il passaggio schema -> tool call -> risultato.
- Notebook 01: distinguere chiaramente risposta inventata e risposta grounded dai tool dentro un loop LangGraph.
- Notebook 02: mostrare un agente con ricerca web, fonti e conversazione interattiva.
- Notebook 03: mostrare che multi-agent spesso significa ruoli piccoli, routing e trace leggibile.
- Notebook 04: rendere la memoria concreta: senza history non esiste, con `thread_id` viene separata.
- Notebook 05: separare RAG da memoria e tool operativi usando chunk, retrieval, SQLite e citazioni.
- Tenere gli studenti attivi: far modificare prompt, domande, regole del router e thread id.

## Dove gli studenti si bloccano
1. Provider o modello sbagliato in `.env`.
2. Ollama locale non avviato.
3. Modello locale troppo debole per tool calling affidabile.
4. Dimenticare `thread_id` quando testano la memoria.
5. Confondere memoria, tool lookup e RAG.
6. Pensare che temperatura 0 significhi determinismo assoluto.

## Interventi utili in aula
- Far confrontare una risposta senza tool con una risposta grounded.
- Far cambiare descrizione o argomenti del tool nel notebook 00b e osservare se il modello continua a chiamarlo.
- Far rompere volutamente il router e usare il trace per capire il bug.
- Far cambiare `thread_id` e spiegare perché la memoria sparisce.
- Far ispezionare i chunk recuperati prima della risposta RAG: se il contesto e sbagliato, la generazione non puo salvarla.
- Far aggiungere una domanda rischiosa, per esempio inviare email o prenotare un'aula, e discutere approvazione umana.

## Idee per estensioni
- assistente Erasmus per corsi ed eventi
- advisor per scegliere un progetto finale
- agente per organizzare una demo night
- assistente per prenotazione aule con approvazione obbligatoria
