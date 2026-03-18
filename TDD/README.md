# TDD - Esercitazione con JUnit

Questa cartella e stata inizializzata per svolgere test secondo il modello **Test Driven Development (TDD)** descritto nelle slide **Es02-TDD**.

## Obiettivo

Scrivere il codice partendo dai test, seguendo cicli brevi e incrementali:

1. **Red**: scrivi un test che fallisce.
2. **Green**: implementa il minimo codice per far passare il test.
3. **Refactor**: migliora il design mantenendo tutti i test verdi.

## Regole pratiche TDD (workflow del corso)

1. Un nuovo comportamento inizia sempre da un test.
2. Un test deve essere piccolo, leggibile e focalizzato su un solo aspetto.
3. In fase Green scrivi solo il codice strettamente necessario.
4. Il refactoring avviene solo con tutti i test verdi.
5. Ripeti il ciclo con passi molto piccoli.

## Struttura del progetto

- `src/main/java`: codice di produzione.
- `src/test/java`: test JUnit 5.
- `pom.xml`: configurazione Maven + JUnit.

## Prerequisiti

- Java 21 (allineato alle altre cartelle del workspace)
- Maven 3.9+

## Comandi utili

Esegui dalla cartella `TDD`.

```bash
mvn test
```

Esegui un singolo test:

```bash
mvn -Dtest=NomeClasseTest test
```

## Come lavorare nell'esercizio

1. Crea un nuovo test in `src/test/java` che descrive il prossimo requisito.
2. Esegui `mvn test` e verifica il fallimento (Red).
3. Implementa il minimo in `src/main/java` (Green).
4. Rifattorizza mantenendo i test verdi (Refactor).
5. Procedi con il requisito successivo.

## Nota

La struttura e pronta ma volutamente vuota: il primo passo dell'esercizio e creare il tuo primo test (fase Red).
