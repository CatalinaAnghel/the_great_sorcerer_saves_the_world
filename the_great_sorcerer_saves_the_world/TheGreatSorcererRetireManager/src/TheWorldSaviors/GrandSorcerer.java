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
		ServerSocket ss = new ServerSocket(4999);
		ss.setReuseAddress(true);
		Socket s = ss.accept();
		
		System.out.println("The spell was said and the portal is functional! Hooray! :)");
		while(true) {
			InputStreamReader in = new InputStreamReader(s.getInputStream());
			BufferedReader br = new BufferedReader(in);
			PrintWriter pr = new PrintWriter(s.getOutputStream());
			while(br.ready()) {
				String response = br.readLine();
				if(!response.equals("A coven is attaked by an undead. We need some potions to defend it.")) {
					potions.add(response);
					System.out.println("The Grand Sorcerer received the potion: " + response);
				}else {
					System.out.println(response);
					Random random = new Random();
					Integer potionsRequested = random.nextInt(3) + 2;
					System.out.println("The Grand Sorcerer checks if he has " + potionsRequested +" potions.");
					if(potions.size() >= potionsRequested) {
						Integer number = 0;
						while(number < potionsRequested) {
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
						pr.println("I don't have enough potions to give you. Good luck!");
						System.out.println("I don't have enough potions to give you. Good luck!");
						pr.flush();
					}
				}
			}
		}
		
	}
}
