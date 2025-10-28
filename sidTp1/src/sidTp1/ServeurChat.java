package sidTp1;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class ServeurChat  extends Thread{
	private static int nbClient = 0;
	ArrayList<Conversation> listeClients = new ArrayList<>();
	
    public void run() {
    	try {
			ServerSocket Server = new ServerSocket(1236);
			while(true) {
				Socket Client = Server.accept();
	    		nbClient ++;
	    		Conversation conv = new Conversation( Client ,nbClient);
	    		conv.start();
	    		listeClients.add(conv);
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	
    }
    
    public class Conversation extends Thread{
    	int nbClient;
    	Socket Client;
		int numCible;
		BufferedReader br = null;
		PrintWriter pw = null;
    	public Conversation(Socket Client ,int nbClient) {
    		this.nbClient = nbClient;
    		this.Client =Client;
    	}
        public void run() {
        	try {
        		//envoyer un message au client
        		
        		OutputStream os = Client.getOutputStream();
        		 pw = new PrintWriter(os,true);
        		pw.println("Bienvenue : " +nbClient);
        		
        		//recuperer les info a partir de client
        		
        		InputStream is = Client.getInputStream();
        		InputStreamReader isr = new InputStreamReader(is);
        		 br = new BufferedReader(isr);
        		String line ;
        		while ((line = br.readLine()) != null) {
        		    line = line.trim();
        		    
        		    if (line.isEmpty()) {
        		        continue;
        		    } else if (line.contains(":")) {
        		        String[] messageD = line.split(":", 2);
        		        if (messageD[0].matches("\\d+")) {
        		            int numCible = Integer.parseInt(messageD[0]);
        		            String message = messageD[1];
        		            for (int i = 0; i < listeClients.size(); i++) {
        		                Conversation c = listeClients.get(i);
        		                if (numCible == c.nbClient) {
        		                    broadcastMessage(message, Client, numCible);
        		                }
        		            }
        		        }
        		    } else {
        		        broadcastMessage(line, Client, -1);  // broadcast à tous sauf l’émetteur
        		    }
        		
        			
       		}
        		
        	} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}finally {
    	        // Nettoyer toutes les ressources
    			  try {
    			        if (br != null) br.close();
    			    } catch (IOException e) {
    			        e.printStackTrace();
    			    }    			 
    			  if (pw != null) pw.close();
    			  try {
    			        if (Client != null) Client.close();
    			    } catch (IOException e) {
    			        e.printStackTrace();
    			    }
    	        
    	        // Retirer ce thread de la liste
    	        listeClients.remove(this);
    	        
    	        // Logger la déconnexion
    	        System.out.println("Client #" + nbClient + " déconnecté");
    	    }
        }
       
        	
        	public void broadcastMessage (String message, Socket emetteur,int numCible) {
        		for (Conversation clientConnecte : listeClients) {
        		    try {
        		        PrintWriter pwClient = new PrintWriter(clientConnecte.Client.getOutputStream(), true);

        		        if (numCible == -1) {
        		            if (!clientConnecte.Client.equals(emetteur)) {
        		                pwClient.println("Client #" + nbClient + " : " + message);
        		            }
        		        } else {
        		            if (clientConnecte.nbClient == numCible) {
        		                pwClient.println("Message privé de Client #" + nbClient + " : " + message);
        		            }
        		        }

        		    } catch (IOException e) {
        		        System.out.println("Erreur : impossible d'envoyer le message au client #" + clientConnecte.nbClient);
        		        // Retirer le client déconnecté
        		        listeClients.remove(clientConnecte);
        		    }
        		  }
        	}
        		
        	public static void main(String[] args) {
        	    ServeurChat serveur = new ServeurChat(); // créer le serveur
        	    serveur.start();                       
        	    System.out.println("done port 1236 is working");
        	}
        
    	
    }
}