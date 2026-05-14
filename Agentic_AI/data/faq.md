# FAQ

**D: Quale modello locale conviene usare per iniziare?**  
R: Parti da `llama3.1:8b` su Ollama se il computer lo regge. Su macchine più deboli prova un modello più piccolo.

**D: Serve Ollama Cloud se ho già Ollama locale?**  
R: No. Ollama locale è il default del lab; Ollama Cloud serve quando vuoi inferenza hosted mantenendo la stessa forma di API.

**D: LangGraph e uguale agli agenti LangChain?**  
R: No. Gli agenti LangChain sono più ad alto livello; LangGraph rende espliciti stato, nodi, archi, cicli e persistenza.

**D: Cosa conta come azione rischiosa nel lab?**  
R: Qualsiasi tool che modifichi un sistema esterno senza approvazione, per esempio inviare email, prenotare aule o modificare voti.
