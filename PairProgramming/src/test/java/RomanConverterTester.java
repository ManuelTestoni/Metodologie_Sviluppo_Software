@author Manuel Testoni
@author Simone


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RomanConverterTester {
    
    //Test base
    @Test
    public void testInteroToRoman() {
        RomanConverter conv = new RomanConverter("10");
        assertEquals("X", conv.interoToRoman());
    }

    //Test con le migliaia
    @Test
    public void testThousands() {
        RomanConverter conv = new RomanConverter("3254");
        assertEquals("MMMCCLIV", conv.interoToRoman());
    }

    //Test con i nove e i quattro per capire se sono gestiti bene
    @Test
    public void test9Hundreds() {
        RomanConverter conv = new RomanConverter("2944");
        assertEquals("MMCMXLIV", conv.interoToRoman());
    }

    //Test su un numero estremamente grande per testare se il programma
    //si comporta come dovrebbe.
    @Test
    public void testHighNumber() {
        RomanConverter conv = new RomanConverter("100247");
        assertEquals("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMCCCCXLVII", conv.interoToRoman());
    }


}
