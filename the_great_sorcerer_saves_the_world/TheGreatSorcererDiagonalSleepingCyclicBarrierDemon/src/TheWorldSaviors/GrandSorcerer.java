package TheWorldSaviors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.net.*; 
import java.io.*; 

/**
 * This class represents the Grand Sorcerer and it is the server in the TCP/IP communication.
 * @author Anghel Florina-Catalina
 *
 */
public class GrandSorcerer{
	/**
	 * This is the list of potions received by The Great Sorcerer from the witches using the TCP/IP "magic portal"
	 */
	protected static List<String> potions = new ArrayList<String>();
	
	/**
	 * This is the class main. It is used to connect to the socket used to communicate with the witches and to receive
	 * and send potions when they fight an undead. The server will wait for a client to connect and then he will wait 
	 * for a request from a client. If he receives the request, he will try to determine if he got a call for help or if
	 * he received a potion. If he received a potion, he will add it into the list of potions. If he receives a call for
	 * help, he will check if he has enough potions in order to help the caller.
	 * @param args command line arguments
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		// Create the server socket and connect to the port 4999
		ServerSocket ss = new ServerSocket(4999);
		// Enable the SO_REUSEADDRsocket option.
		ss.setReuseAddress(true);
		// Wait until the client(here, the SorcererCircle) connects to the port
		Socket s = ss.accept();
		
		System.out.println("The spell was said and the portal is functional! Hooray! :)");
		while(true) {
			InputStreamReader in = new InputStreamReader(s.getInputStream());
			BufferedReader br = new BufferedReader(in);
			PrintWriter pr = new PrintWriter(s.getOutputStream());
			while(br.ready()) {
				// while there is a message in the portal, it will be read

				String response = br.readLine();
				if(!response.equals("A coven is attaked by an undead. We need some potions to defend it.")) {
					// If a witch sends a potion, then get it and add it into the potions list
					potions.add(response);
					System.out.println("The Grand Sorcerer received the potion: " + response);
				}else {
					/* If the witch asks for help, then the Great Sorcerer will check if he has enough potions 
					 * to help the witch
					*/
					System.out.println(response);
					Random random = new Random();
					// generate the number of potions that he will try to send to the witch
					Integer potionsRequested = random.nextInt(3) + 2;
					System.out.println("The Grand Sorcerer checks if he has " + potionsRequested +" potions.");
					if(potions.size() >= potionsRequested) {
						Integer number = 0;
						while(number < potionsRequested) {
							// If he has enough potions, he will help the witch
							Integer potion = random.nextInt(potions.size());
							System.out.println("Before: " + potions);
							pr.println(potions.get(potion));
							System.out.println("The Grand Sorcerer sends the potion: " + potions.get(potion));
							potions.remove(potions.get(potion));
							pr.flush();
							System.out.println("After: " + potions);
							number++;
						}
					}else {
						// He does not have enough potions
						pr.println("I don't have enough potions to give you. Good luck!");
						System.out.println("I don't have enough potions to give you. Good luck!");
						pr.flush();
					}
				}
			}
		}
		
	}
}
