package sidTp1;

import java.io.*;
import java.net.*;
import java.util.*;
public class ServeurJEU extends Thread {
	private int numberRandom;
	private static int nbClient = 0;
	private  volatile boolean finJeux = false; 
	volatile String gagnant;
	private static ArrayList<Conversation> listeConversations = new ArrayList<>();
    
  public void run() {
	  try {
		ServerSocket Server = new ServerSocket(1235);
		numberRandom = new Random().nextInt(1001);
		
		while (!finJeux) {
			Socket Client = Server.accept();
			nbClient++;
			Conversation conv =new Conversation(Client, nbClient);
			listeConversations.add(conv);
			conv.start();
		}
		  Server.close(); 


	  } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
  
  }
  
  public synchronized void PremierFini(String ipgagnant) {
	  if (finJeux) return;

	    finJeux = true;
	    gagnant = ipgagnant;

	    for (int i = 0;i < listeConversations.size();i++) {
			   Conversation c =  listeConversations.get(i);	        
			   c.EnvoyerFinJeu(gagnant);
	    }

  }
  
  public class Conversation extends Thread {
	  Socket Client;
	  int nbClient;
	  private PrintWriter pw;
	  public Conversation(Socket Client ,int nbClient) {
		  this.Client = Client;
		  this.nbClient = nbClient; 
	  }
	  public void run() {
	
	  //envoyer des réponses 
	  try {
		  OutputStream os = Client.getOutputStream();
		  pw = new PrintWriter(os,true);
		  pw.println("bienvenue !!!!");
		  pw.println("Entrer un nombre : ");
		  
	  // lire les messages du client
	  InputStream is = Client.getInputStream();
	  InputStreamReader isr = new InputStreamReader(is);
	  BufferedReader br = new BufferedReader(isr);
	  
	  while(!finJeux) {
	  String s = br.readLine();
	  int nbr = Integer.parseInt(s);
	  
	  if (nbr > numberRandom) {
		  pw.println("Trop grand");
		  
	  }else if (nbr < numberRandom) {
		  pw.println("Trop petit");
		  
	  }else {
		  String ipgagnant = Client.getInetAddress().getHostAddress() + ":" + Client.getPort();
		   PremierFini( ipgagnant);
		   break;
	  }
	   
	    }
	  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
	  
	  }
	  
	  public void EnvoyerFinJeu(String ipgagnant) {
		  
		  String ipClient = Client.getInetAddress().getHostAddress() + ":" + Client.getPort();		  
		  
		  if (ipClient.equals(ipgagnant)) {
	            pw.println("SUPER !! Vous avez gagne !");
		  }
		  else {
	            pw.println("Le jeu est termine. Le gagnant est : " + ipgagnant);

		  }
	  }
		  
	  }
  
  public static void main(String[] args) {
	    ServeurJEU serveur = new ServeurJEU(); // créer le serveur
	    serveur.start();                       // démarrer le thread du serveur
	    System.out.println("done");
	}

  }

