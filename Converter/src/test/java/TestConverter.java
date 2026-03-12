import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.beans.Transient;
import org.junit.jupiter.api.Test;

public class TestConverter {
    
    /**
     * Test: Converter must work accordingly to specified behavior.
     */
    @Test
    public void testConversion() throws IncorrectStringExeption {
        // Let's run multiple test to capture the right and wrong behavior of our calculator.
        assertEquals(340,Converter.stringToInt("340"), "The returned value shoudl match the expected one");
        assertEquals(-32768, Converter.stringToInt("-32768"));
        try {
            assertEquals(-1, Converter.stringToInt("#32768"));
		    fail("Expected IncorrectStringException to be thrown");
	    } catch (IncorrectStringExeption e) {
            // Test passed - exception was thrown as expected
        }
        try {
            assertEquals(-1, Converter.stringToInt("-32769"));
		    fail("Expected IncorrectStringException to be thrown");
	    } catch (IncorrectStringExeption e) {
            // Test passed - exception was thrown as expected
        }

        try {
            assertEquals(-1, Converter.stringToInt("32768"));
		    fail("Expected IncorrectStringException to be thrown");
	    } catch (IncorrectStringExeption e) {
            // Test passed - exception was thrown as expected
        }

    }
}
