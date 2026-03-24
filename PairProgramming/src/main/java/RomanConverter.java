public class RomanConverter {
    
    //classe che prende in input una stringa e la traduce in un numero romano.
    private String interoToRoman;

    RomanConverter(String interoToRoman) {
        this.interoToRoman = interoToRoman;
    }

    public String interoToRoman() {

        //Creiamo una stringav vuota e ci salviamo la lunghezza del numero.
        String convertedNumber = "";
        int length = 0;

        //Questo controllo ci serve per capire se il numero va sopra le migliaia, in quanto
        // sopra 999 ogni migliaio corrisponde ad una M
        if(interoToRoman.length() > 2 ) {
            //prendiamo il numero prima delle ultime 3 cifre
            String substring = interoToRoman.substring(0, interoToRoman.length() - 3);
            //Creiamo una sotto stringa composta da tante M quante migliaia ci sono.
            String parseRoman = "M".repeat(Integer.parseInt(substring));

            length = interoToRoman.length() - 2;
        }
        for(int i = length; i < interoToRoman.length(); i++) {

            //Prendiamo il valore del singolo carattere della stringa nel "reparto" centinaia
            int number = Character.getNumericValue(interoToRoman.charAt(i));
            number = number * (int) Math.pow(10, interoToRoman.length()-i);
            //e lo traduciamo in un nunmero romano corrispondente.
            switch (number) {
                case 900:
                    convertedNumber = convertedNumber + "CM";
                    break;
                case 800:
                    convertedNumber = convertedNumber + "DCCC";
                    break;
                case 700:
                    convertedNumber = convertedNumber + "DCC";
                    break;
                case 600:
                    convertedNumber = convertedNumber + "DC";
                    break;
                case 500:
                    convertedNumber = convertedNumber + "D";
                    break;
                case 400:
                    convertedNumber = convertedNumber + "CD";
                    break;
                case 300:
                    convertedNumber = convertedNumber + "CCC";
                    break;
                case 200:
                    convertedNumber = convertedNumber + "CC";
                    break;
                case 100:
                    convertedNumber = convertedNumber + "C";
                    break;
                case 90:
                    convertedNumber = convertedNumber + "XC";
                    break;
                case 80:
                    convertedNumber = convertedNumber + "LXXX";
                    break;
                case 70:
                    convertedNumber = convertedNumber + "LXX";
                    break;
                case 60:
                    convertedNumber = convertedNumber + "LX";
                    break;
                case 50:
                    convertedNumber = convertedNumber + "L";
                    break;
                case 40:
                    convertedNumber = convertedNumber + "XL";
                    break;
                case 30:
                    convertedNumber = convertedNumber + "XXX";
                    break;
                case 20:
                    convertedNumber = convertedNumber + "XX";
                    break;
                case 10:
                    convertedNumber = convertedNumber + "X";
                    break;
                case 9:
                    convertedNumber = convertedNumber + "IX";
                    break;
                case 8:
                    convertedNumber = convertedNumber + "VIII";
                    break;
                case 7:
                    convertedNumber = convertedNumber + "VII";
                    break;
                case 6:
                    convertedNumber = convertedNumber + "VI";
                    break;
                case 5:
                    convertedNumber = convertedNumber + "V";
                    break;
                case 4:
                    convertedNumber = convertedNumber + "IV";
                    break;
                case 3:
                    convertedNumber = convertedNumber + "III";
                    break;
                case 2:
                    convertedNumber = convertedNumber + "II";
                    break;
                case 1:
                    convertedNumber = convertedNumber + "I";
                    break;
                default:
                    break;
            }
        }

        return convertedNumber;

    }

}
