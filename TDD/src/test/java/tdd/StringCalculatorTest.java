package tdd;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class StringCalculatorTest {

    @Test
    public void emptyStringReturnsZero() throws NegativeNumberException {
        StringCalculator calc = new StringCalculator();
        
        String numeri = "//[;] ";
        assertEquals(0, calc.Add(numeri));
    }

    @Test
    public void oneStringOnly() throws NegativeNumberException {
        StringCalculator calc = new StringCalculator();
        String numeri = "//[;]\n [;]1";
        assertEquals(1, calc.Add(numeri));
    }
    @Test
    public void bothStrings() throws NegativeNumberException {
        StringCalculator calc = new StringCalculator();
        String numeri = "//[;]\n [;]1\n2[;]3";
        assertEquals(6, calc.Add(numeri));
    }

    @Test
    public void commaAndNewLine() throws NegativeNumberException {
        StringCalculator calc = new StringCalculator();
        String numeri = "//[;]\n [;]11[;]\n22[;]11";
        assertEquals(44, calc.Add(numeri));
    }

    @Test
    public void newLineAndNewLine() throws NegativeNumberException {
        StringCalculator calc = new StringCalculator();
        String numeri = "//[;]\n 1[;]2\n\n";
        assertEquals(-1, calc.Add(numeri));
    }

    @Test
    public void newLineAndSeparator() throws NegativeNumberException {
        StringCalculator calc = new StringCalculator();
        String numeri = "//[;]\n 1[;]2\n[;]3";
        assertEquals(-1, calc.Add(numeri));
    }

    @Test
    public void differentSeparator() throws NegativeNumberException {
        StringCalculator calc = new StringCalculator();
        String numeri = "//[:]\n100[:]200[:]300[:]400";
        assertEquals(1000, calc.Add(numeri));
    }

    @Test
    public void negativeNumber() throws NegativeNumberException{
        StringCalculator calc = new StringCalculator();
        String numeri = "//[;]\n 1[;]-1[;]-5[;]7";
        try {
            calc.Add(numeri);
            fail("Expected NegativeNumberException to be thrown");
        } catch (NegativeNumberException e) {
            // Test passed - exception was thrown as expected
        }
    }

    @Test
    public void maxNumber() throws NegativeNumberException{
        StringCalculator calc = new StringCalculator();
        String numeri = "//[;]\n1100[;]4[;] [;]500;";
        assertEquals(504,calc.Add(numeri));

    }

    @Test 
    public void differentLengthSeparator() throws NegativeNumberException {
        StringCalculator calc = new StringCalculator();
        String numeri = "//[***]\n12[**]90";
        assertEquals(-1, calc.Add(numeri));
    }

    @Test
    public void differentNewSeparator() throws NegativeNumberException {
        StringCalculator calc = new StringCalculator();
        String numeri = "//[**,]\n12[***]4";
        assertEquals(-1, calc.Add(numeri));
    }

    @Test   
    public void strangeSeparator() throws NegativeNumberException {
        StringCalculator calc = new StringCalculator();
        String numeri = "//[?????]\n120[?????]240";
        assertEquals(360, calc.Add(numeri));
    }

    @Test
    public void differentSeparatorSequences() throws NegativeNumberException {
        StringCalculator calc = new StringCalculator();
        String numeri = "//[??]\n120 [**]240";
        assertEquals(360, calc.Add(numeri));
    }
}