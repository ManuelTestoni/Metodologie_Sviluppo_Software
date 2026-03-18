package tdd;
import java.util.ArrayList;
public class StringCalculator {
    
    public int Add(String numeri) throws NegativeNumberException{

        ArrayList<Integer> negativi = new ArrayList<Integer>();
        //ci permette di controllare se abbiamo trovato un numero negativo nel nostro codice.
        boolean negaTrovati = false;
        int sum = 0;
        //saltiamo direttamente il doppio slash che è inutile e il separatore.
        // ora andiamo all'indice 3 perché dobbiamo evitare anche la parentesi quadra.
        char sep = numeri.charAt(3);

        //questa var ci serve per controllare che ogni sequenza di separatori nella stringa sia lunga 
        //uguale
        int lunghSep = 1;
        int lunghCheck = 1;

        for(int i = 3; i < numeri.length(); i ++) {
            if(numeri.charAt(i) == sep) {
                if(numeri.charAt(i+1) != ']') {
                    lunghSep++;
                }
            }else if(numeri.charAt(i) == ']') {
                break;
            }
        }

        //usiamo un buffer per leggere finché non raggiunge il carattere separatore.
        ArrayList<Integer> buff = new ArrayList<Integer>();
        for(int i = 3; i < numeri.length(); i ++) {
            if(numeri.charAt(i) == '[' || numeri.charAt(i) == ']') {
                continue;
            }
            boolean isSepChar = !Character.isDigit(numeri.charAt(i)) && numeri.charAt(i) != '\n' && numeri.charAt(i) != ' ' && numeri.charAt(i) != '-';
            if(isSepChar) {
                if(!buff.isEmpty()) {
                    int numero = 0;
                    for(int j = 0; j < buff.size(); j++) {
                        numero = numero * 10 + buff.get(j);
                    }
                    if(numero > 1000) {
                        numero = 0;
                    }
                    sum += numero;
                    buff.clear();
                    lunghCheck = 1;
                }
                char currentSep = numeri.charAt(i);
                if(i+1 < numeri.length() && numeri.charAt(i+1) == currentSep) {
                    lunghCheck++;
                    //controlliamo che non vi siano separatori diversi di fila
                }else if(i+1 < numeri.length() && numeri.charAt(i+1) != currentSep && lunghSep > 1 && numeri.charAt(i+1) != ']') {
                    return -1;
                }
                continue;
            }else if(numeri.charAt(i) == '\n') {
                if(i + 1 < numeri.length() && (numeri.charAt(i+1) == '[' || numeri.charAt(i+1) == '\n')) {
                    return -1;
                    // Controlliamo che non avvenga la sequenza \n \n o \n sep, mentre
                    // sep \n è accettabile de formato della stringa.
                }else {
                    if(!buff.isEmpty()) {
                        int numero = 0;
                        for(int j = 0; j < buff.size(); j++) {
                            numero = numero * 10 + buff.get(j);
                        }
                        sum += numero;
                        buff.clear();
                    }
                    continue;
                }
            }else if(numeri.charAt(i) == ' ' || numeri.charAt(i) == sep) {
                continue;
            }      
            if(numeri.charAt(i) == '-') {
                negativi.add(Character.getNumericValue(numeri.charAt(i+1)));
                negaTrovati = true;
                continue;
            }
            if(lunghSep != lunghCheck) {
                return -1;
            }
            buff.add(Character.getNumericValue(numeri.charAt(i)));

            
        }

        if(!buff.isEmpty()) {
            int numero = 0;
            for(int j = 0; j < buff.size(); j++) {
                numero = numero * 10 + buff.get(j);
            }
            sum += numero;
            buff.clear();
        }
        
        if(negaTrovati == true) {
            throw new NegativeNumberException(negativi);
        }

        return sum;
    }

}
