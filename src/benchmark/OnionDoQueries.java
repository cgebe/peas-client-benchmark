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
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import onionclient.OnionClient;
import util.Config;
import util.Observer;

public class OnionDoQueries extends Thread {
	
	private OnionClient client;
	private List<Map<String, String>> nodes;
	private String query;
	private int id;
	private final Executor executor = Executors.newSingleThreadExecutor();
	
	public OnionDoQueries(int id, OnionClient client, List<Map<String, String>> nodes, String query) {
		this.id = id;
		this.client = client;
		this.nodes = nodes;
		this.query = query;
	}

    public void run() {
    	long startTime = System.nanoTime();
    	long threadTimer = System.nanoTime();
        int count = 0;
        int queriesPerSecond = 0;
    	while (count < 10000000) {
    		if ((System.nanoTime() - threadTimer) / 1e6 > 1000) {
    			try {
	    			String jarPath = new File(PEASDoQueries.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath();
					PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(jarPath + "/" + "queriesPerSecond" + id + ".log", true)));
					writer.println(queriesPerSecond);
					writer.close();
	    		} catch (IOException | URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
    			queriesPerSecond = 0;
    			threadTimer = System.nanoTime();
    		}
    		try {
    			if (!client.isSending()) {
    				count++;
    				queriesPerSecond++;
    				client.doQuery(nodes, query);
    				
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
			} catch (Exception e) {
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
            double latency = (endTime - startTime) / ((double) count) / 1000000;
            String line = (int) time + "\t" + count + "\t" + String.format("%.3f", count/time) + "\t" + String.format("%.3f", latency);
            
            try {
				String jarPath = new File(Observer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath();
			
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(jarPath + "/" + "throughput" + id + ".log", true)));
				writer.println(line);
				writer.close();
			} catch (IOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        }
    }
}