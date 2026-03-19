import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class DBrecorder {
	public static void fromSourceToDB(String source, String type){
		ArrayList<String> keys = new ArrayList<>();
		ArrayList<String> values = new ArrayList<>();
		
		/* reading phase */
		
		if (type.equals("file")) {
			FileReader fin = null;
			try { fin = new FileReader(source);}
			catch (IOException e) {
				System.err.println("Error opening "+source);
				System.err.println(e);
				return;
			}
			
			try {
				BufferedReader br = new BufferedReader(fin);
				performRead(br, keys, values);
			}
			catch (IOException e){
				System.err.println("Error reading "+source);
				System.err.println(e);
				return;
			}
			
			
			
		}
		else if (type.equals("net")) {
			Socket s = null;
			try {
				s = new Socket(source.substring(0, source.indexOf(':')), Integer.parseInt(source.substring(source.indexOf(':')+1)));
			} 
			catch (Exception e) {
				System.err.println("Error in socket "+source);
				e.printStackTrace();
				return;
			}

			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				performRead(br, keys, values);
				s.close();
			}
			catch (Exception e){
				System.err.println("Error in socket "+source);
				e.printStackTrace();	
				return;
			}
			
		}
		else {
			System.err.println("Unknown source");
			return;
		}
		
		/* recording phase */
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
			
			for (int i=0; i< keys.size(); i++) {
				/* SQL command */
				stringSql = "INSERT INTO tab1 VALUES (\""+keys.get(i)+"\", \""+values.get(i)+"\");";
				System.out.println(stringSql);
				stmt = con.createStatement();
				res = stmt.executeUpdate(stringSql);
				System.out.println("Returned: " + res);

			}
			con.close();
					
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		}		

	}

	public static void performRead(BufferedReader br, ArrayList<String> keys, ArrayList<String> values) throws IOException {
		String line;
		while((line = br.readLine()) != null){
			keys.add(line.substring(0, line.indexOf(' ')));
			values.add(line.substring(line.indexOf(' ')+1));
		}
	}

}
