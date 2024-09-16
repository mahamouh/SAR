package task1;

import task1Interface.*;

public class Main {
	public static void main(String[] args) {
		
        Broker brokerServer = new Broker("server");
        
        Task server = new Task(brokerServer, () -> {
        	try {
        		
        	Channel channelServer = brokerServer.accept(88);
        	
        	byte[] buffer = new byte[256];
            int bytesRead = channelServer.read(buffer, 0, buffer.length);
            channelServer.write(buffer, 0, bytesRead);
            
            
        	}catch (Exception e) {
                System.out.println("Erreur dans le serveur: " + e.getMessage());
            }
        	
        });
        
        
        Broker brokerClient = new Broker("client");
        
        Task client = new Task(brokerClient, () -> {
        	try {
        	Channel channelClient = brokerClient.connect("localhost", 88);
        	
        	assert channelClient.disconnected() == false : "Le client est censé être connecter";
        	
        	byte[] sendData = new byte [256];
        	for (int i =0; i<sendData.length; i++) {
        		sendData[i] = (byte) (i +1);
        	}
        	
        	int writeData = channelClient.write(sendData, 0, sendData.length);
        	
        	assert writeData == sendData.length : "Le nombre d'octects envoyés et d'octects écrits ne sont pas les mêmes";
        	
        	int readData = channelClient.read(sendData, 0, writeData);
        	
        	assert readData == writeData : "Le nombre d'octects lus et d'octects écrits ne sont pas les mêmes";
        	
        	channelClient.disconnect();
        	assert channelClient.disconnected() == true : "Le client est censé être déconnecter";
        	
        	}catch (Exception e) {
                System.out.println("Erreur dans le client: " + e.getMessage());
            }
        
        	
        });
       
        
        try {
        	server.start();
			server.join();
			
			client.start();
			client.join();
			
			assert server.getBroker() == brokerServer : "Il y'a un problème dans la méthode getBroker";
			assert client.getBroker() == brokerClient : "Il y'a un problème dans la méthode getBroker";
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
       
        
    }
	
}
