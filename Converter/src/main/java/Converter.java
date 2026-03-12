
public class Converter {
	
    public static int stringToInt(String s) throws IncorrectStringExeption {
        
        for (int i = 0; i < s.length(); i ++) {
            if(s.charAt(i) != '0' && s.charAt(i) != '1' && s.charAt(i) != '2' && s.charAt(i) != '3' && s.charAt(i) != '4' &&
            s.charAt(i) != '5' && s.charAt(i) != '6' && s.charAt(i) != '7' && s.charAt(i) != '8' && s.charAt(i) != '9' && s.charAt(i) != '-') {
                throw new IncorrectStringExeption();
            }
        }
        int stringa = Integer.parseInt(s);
        if(stringa < -32768 || stringa > 32767) {
            throw new IncorrectStringExeption();
        } 
        return stringa;
    }

	
}

