package threaded.test;

import threaded.DisconnectedException;
import threaded.impl.Broker;
import threaded.impl.Channel;
import threaded.impl.Task;

public class EchoTest {
	
	public static final int BUFFER_SIZE = 128;
	
	public Task clientA;
	public Task clientB;
	public Task clientC;
	public Task echoServer;
	
	public Runnable getClientTask() {
	    return () -> {
	
	    	Task currentClient = (Task) Thread.currentThread();
	
			Broker currentBroker = currentClient.getBroker();
	
			byte[] sendData = new byte[BUFFER_SIZE];
	 		byte[] receivedData = new byte[BUFFER_SIZE];
	
			Channel channel = currentBroker.connect("serverOne", 90);
			
			for(int i = 0; i < BUFFER_SIZE; i++)
				sendData[i] = (byte) (i + 1);
	
			// Sending all the data
			for(int totalWrittenBytes = 0; totalWrittenBytes < BUFFER_SIZE;){
				int writtenBytes = 0;
				try {
					writtenBytes = channel.write(sendData, 0, BUFFER_SIZE);
				} catch (DisconnectedException e) {
					e.printStackTrace();
				}
				if (writtenBytes < 0) {
					throw new Error("Write error");
				}
				totalWrittenBytes += writtenBytes;
			}
	
			// Getting all the data
			for(int totalReadBytes = 0; totalReadBytes < BUFFER_SIZE;){
				int readBytes = 0;
				try {
					readBytes = channel.read(receivedData, 0, BUFFER_SIZE);
				} catch (DisconnectedException e) {
					e.printStackTrace();
				}
				if (readBytes < 0) {
					throw new Error("Read error");
				}
				totalReadBytes += readBytes;
			}
	
			channel.disconnect();
	
			// Tests
			for(int i = 0; i < BUFFER_SIZE; i++){
				assert(sendData[i] == receivedData[i]) : "Mismatch at index " + i;
			}	
	
			assert(channel != null) : "Channel not initialized";
			assert(channel.disconnected()) : "Channel not disconnected";
			
		};
	}
	
	public Runnable getServerTask() {
	    return () -> {
	
			byte[] receivedData = new byte[BUFFER_SIZE];
	
			Thread currentThread = Thread.currentThread();
			
			Task serverTask = (Task) currentThread;
	
			Broker currentBroker = serverTask.getBroker();
			
			for(int j = 0; j < 3; j++) {
				Channel serverChannel = currentBroker.accept(90);
	
				for(int i = 0; i < BUFFER_SIZE;){
					int bytesRead = 0;
					try {
						bytesRead = serverChannel.read(receivedData, 0, BUFFER_SIZE);
					} catch (DisconnectedException e) {
						e.printStackTrace();
					}
					if (bytesRead < 0) {
						throw new Error("Read error");
					}
					i += bytesRead;
				}
	
				for(int i = 0; i < BUFFER_SIZE;){
					int bytesWritten = 0;
					try {
						bytesWritten = serverChannel.write(receivedData, 0, BUFFER_SIZE);
					} catch (DisconnectedException e) {
						e.printStackTrace();
					}
					if (bytesWritten < 0) {
						throw new Error("Write error");
					}
					i += bytesWritten;
				}
				
				serverChannel.disconnect();
	
				assert(serverChannel != null) : "Server Channel not initialized";
				assert(serverChannel.disconnected()) : "Server Channel not disconnected";
				
			}
			
		};
	}
	
	private void initialize() {
		Broker brokerA = new Broker("serverOne");
		Broker brokerB = new Broker("serverTwo");
	
		this.clientA = new Task(brokerB, this.getClientTask());
		this.clientB = new Task(brokerB, this.getClientTask());
		this.clientC = new Task(brokerB, this.getClientTask());
		
		this.echoServer = new Task(brokerA, this.getServerTask());
	}
	
	public static void main(String[] args) {
	
		EchoTest echoTest = new EchoTest();
	
		echoTest.initialize();
	
		echoTest.echoServer.start();
		echoTest.clientA.start();
		echoTest.clientB.start();
		echoTest.clientC.start();
	
		try {
			echoTest.echoServer.join();
			echoTest.clientA.join();
			echoTest.clientB.join();
			echoTest.clientC.join();
	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("TEST COMPLETED SUCCESSFULLY");
	
	}
}
