package benchmark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.bouncycastle.crypto.InvalidCipherTextException;

import util.Config;
import util.Observer;
import client.Client;

public class PEASDoQueries extends Thread {
	
	private Client client;
	private String receiverAddress; 
	private int receiverPort;
	private String issuerAddress;
	private int issuerPort;
	private String query;
	private int id;
	private final Executor executor = Executors.newSingleThreadExecutor();
	
	public PEASDoQueries(int id, Client client, String receiverAddress, int receiverPort, String issuerAddress, int issuerPort, String query) {
		this.id = id;
		this.client = client;
		this.receiverAddress = receiverAddress;
		this.receiverPort = receiverPort;
		this.issuerAddress = issuerAddress; 
		this.issuerPort = issuerPort;
		this.query = query;
	}

    public void run() {
    	long startTime = System.nanoTime();
        int count = 0;
    	while (count < 10000000) {
    		try {
    			if (!client.isSending()) {
    				count++;
    				client.doQuery(receiverAddress, receiverPort, issuerAddress, issuerPort, query);
    				
    				// Sleep between requests
    				long slstarttime = System.nanoTime();
    				if (Integer.parseInt(Config.getInstance().getValue("SLEEP")) > 0) {
    					Thread.sleep(Integer.parseInt(Config.getInstance().getValue("SLEEP")));
    					
    					long wtime = System.nanoTime() - slstarttime;
    					startTime += wtime;
    				}
    				
    				
    				// Measure Throughput
    				if (Config.getInstance().getValue("MEASURE_THROUGHPUT").equals("on")) {
    					if (count % 20 == 1) {
    						long endTime = System.nanoTime();
    	                    executor.execute(new ThroughtputWriter(count, startTime, endTime));
    					}
    				}
    			}
    		} catch (InvalidKeyException | NoSuchAlgorithmException
				| InvalidAlgorithmParameterException
				| InvalidCipherTextException | InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			client.setSending(false);
    		} finally {

            }
    		
       }
    }
    
    private class ThroughtputWriter implements Runnable {

        private int count;
        private long startTime;
        private long endTime;
        private PrintWriter log;

        public ThroughtputWriter(int count, long startTime, long endTime) {
            this.count = count;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public void run() {
            double time = (endTime - startTime) / 1000000000;
            double latency = (endTime-startTime) / ((double) count) / 1000000;
            String line = (int) time + "\t" + count + "\t" + String.format("%.3f", count/time) + "\t" + String.format("%.3f", latency);

			try {
				String jarPath = new File(Observer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath();
			
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(jarPath + "/" + "throughput.log", true)));
				writer.println(line);
				writer.close();
			} catch (IOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        }
    }
}
