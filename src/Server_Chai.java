//Victoria Chai
//Server allows client 3 chances to enter a password, blocks clients that have been rejected
import java.io.*;
import java.net.*;
import java.util.*;

public class Server_Chai {
	
	private final String PASSWORD = "ateez";
	private Map<String,Integer> clients = new HashMap<String,Integer>();

	public Server_Chai() {

		try {
			ServerSocket server = new ServerSocket(4242);
			System.out.println(server.getLocalPort());
			System.out.println(InetAddress.getLocalHost().getHostAddress());

			// server socket accepts connections forever
			while (true) {

				Socket sock = server.accept();
				
				//gets IP address of client
				String ip = sock.getRemoteSocketAddress().toString();
				String address = ip.substring(2, ip.indexOf(":"));

				//spawns new thread to interact with client
				ClientReceiver receiver = new ClientReceiver(sock, address);
				Thread thread = new Thread(receiver);

				thread.start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class ClientReceiver implements Runnable {

		private Socket sock;
		private String address;
		private PrintWriter out;

		public ClientReceiver(Socket s, String a) {
			sock = s;
			address = a;
		}

		public void run() {
			
			try {
				out = new PrintWriter(sock.getOutputStream());

				//checks if client has been rejected before
				if (!clients.isEmpty() && clients.get(address) == 0) {
					out.println("NO");
					out.flush();
				}
				else {
					out.println("ACCEPTED");
					out.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			int numTries = 3;
			
			//if client has connected before, retrieves their attempts left
			if(clients.containsKey(address))
				numTries = clients.get(address);
			
			try {
				
				Scanner in = new Scanner(sock.getInputStream());
				while (numTries > 0) {

					numTries--;
					String attempt = in.nextLine();

					//checks for correct password
					//if clients runs out of tries, rejects client
					if (attempt.equals(PASSWORD)) {
						out.println("ACCEPTED");
						out.flush();
						in.close();
						sock.close();
						return;
					} else if (numTries == 0) {
						out.println("NO");
						out.flush();
					} else {
						out.println("Password was incorrect. You have " + numTries + " attempts left.");
						out.flush();
					}
					
					//updates client's attempts left
					clients.put(address, numTries);				
				}

				in.close();
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Server_Chai test = new Server_Chai();
	}
	
}
