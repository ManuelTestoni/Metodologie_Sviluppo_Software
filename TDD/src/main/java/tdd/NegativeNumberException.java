package tdd;
import java.util.*;

public class NegativeNumberException extends Exception {
    public NegativeNumberException(ArrayList<Integer> negativi) {
        for(int i = 0; i < negativi.size(); i++) {
            System.out.println("Questi sono i numeri negativi trovati: -"+ negativi.get(i));
        }
    }
}

