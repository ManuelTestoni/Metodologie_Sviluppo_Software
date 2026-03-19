import java.io.*;
import java.net.*;


public class RefactoringServerSocket {

	public static void main(String[] args) {
		boolean debug = false;
		int port = 2017;
		try {
			for (String arg : args) {
				switch (arg) {
					case "debug": debug = true; break;
					default: port = Integer.parseInt(arg);
				}
			}
			ServerSocket ss = new ServerSocket(port);
			while (true) {
				Socket s = ss.accept();
				if (debug)
					System.out.println("Ricevuta richiesta da "+s.getRemoteSocketAddress());
				PrintStream ps = new PrintStream(s.getOutputStream());
				ps.println("sk1 sv1");
				ps.println("sk2 sv2");
				ps.println("sk3 sv3");
				if (debug)
					System.out.println("Chiudo la connessione");
				ps.close();
				s.close();
			}
			//ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
