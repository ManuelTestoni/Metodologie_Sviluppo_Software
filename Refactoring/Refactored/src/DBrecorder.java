import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;


public class DBrecorder {
	public static void fromSourceToDB(String source, String type){

		// Usiamo strutture dati dinamiche quali array list con generics.
		
		ArrayList <String> keys[] = new ArrayList<>();
		ArrayList <String> values[] =  new ArrayList<>();
		int cont = 0;
		
		/* reading phase */
		
		if (type.equals("file")) {
			FileReader fin = null;
			try { fin = new FileReader(source);}
			catch (IOException e) {
				// Chiamiamo il metodo evitando duplicazione.
				openingErrorDetection(e, source);
			}
			
			try {
				BufferedReader br = new BufferedReader(fin);
				// Chiamiamo il metodo evitando duplicazione.
				performRead(br, keys, values);
			}
			catch (IOException e){
				// Chiamiamo il metodo evitando duplicazione.
				openingErrorDetection(e, source);
			}
		}
		else if (type.equals("net")) {
			Socket s = null;
			try {
				// Sempre andare a capo e non fare codice su una sola riga.
				s = new Socket(source.substring(0, source.indexOf(':')), 
				Integer.parseInt(source.substring(source.indexOf(':')+1)));
			} 
			catch (Exception e) {
				// Chiamiamo il metodo evitando duplicazione.
				openingSocketErrorDetection(e, source);
			}

			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				// Chiamiamo il metodo evitando duplicazione.
				performRead(br, keys, values);
				s.close();
			}
			catch (Exception e){
				// Chiamiamo il metodo evitando duplicazione.
				openingSocketErrorDetection(e, source);
			}
			
		}
		else {
			System.err.println("Unknown source");
			return;
		}
		
		/* recording phase */

		//Valori hardocded del database, solitamente in produzione non si fa così.
		String urlDB="jdbc:sqlite:db1.db";
		String user="mas";
		String pwd="mas";
		Statement stmt;
		ResultSet rs;
		String stringSql;
		int res;
		
		try { 
			/* connect to the DBMS */
			Class.forName("org.sqlite.JDBC");
			Connection con = DriverManager.getConnection(urlDB, user, pwd);
			/* create the DB table if not exist */
			stringSql = "CREATE TABLE IF NOT EXISTS tab1 (key text, value text);";
			System.out.println(stringSql);
			stmt = con.createStatement();
			res = stmt.executeUpdate(stringSql);
			System.out.println("Returned: " + res);
			con.close();

			/* add the couples to the DB */
			con = DriverManager.getConnection(urlDB, user, pwd);
			
			for (int i=0; i< cont; i++) {
				/* SQL command */
				stringSql = "INSERT INTO tab1 VALUES (\""+keys[i]+"\", \""+values[i]+"\");";
				System.out.println(stringSql);
				stmt = con.createStatement();
				res = stmt.executeUpdate(stringSql);
				System.out.println("Returned: " + res);

			}
			con.close();
					
		} catch (Exception e) {
			openingSocketErrorDetection(e, source);
		}		

	}

	public static void performRead( BufferedReader br, ArrayList<String> keys[],ArrayList<String> values[]) {
		String line;
		int cont = 0;
		while((line = br.readLine()) != null){
					keys[cont] = line.substring(0, line.indexOf(' '));
					values[cont] = line.substring(line.indexOf(' ')+1);
					cont++;
				}
	}

	public static void openingErrorDetection(IOException e, String source) {
		System.err.println("Error reading "+source);
		System.err.println(e);
		return;
	}

	public static void openingSocketErrorDetection(Exception e, String source) {
		System.err.println("Error in socket "+source);
		e.printStackTrace();	
		return;
	}
}
