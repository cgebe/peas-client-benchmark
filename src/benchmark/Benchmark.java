package benchmark;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.crypto.InvalidCipherTextException;

import client.Client;
import onionclient.OnionClient;
import util.Config;
import util.Observer;

public class Benchmark {
	
	public static void main(String[] args) throws Exception {
		if (Config.getInstance().getValue("TYPE").equals("default")) {
	    	String[] receivers = Config.getInstance().getValue("RECEIVERS").split(",");
	    	String[] issuers = Config.getInstance().getValue("ISSUERS").split(",");
	    
	    	// create clients
	    	List<Client> clients = new ArrayList<Client>();
	    	for (int i = 0; i < Integer.parseInt(Config.getInstance().getValue("CLIENT_COUNT")); i++) {
	    		System.out.println("client added");
	    		System.out.println(Integer.parseInt(Config.getInstance().getValue("CLIENT_COUNT")));
	    		clients.add(new Client());
	    	}
	    	
	    	// first receiver and issuer by now
    		String[] receiver = receivers[0].split(":");
    		String[] issuer = issuers[0].split(":");
    		
    		// send queries
    		for (int i = 0; i < Integer.parseInt(Config.getInstance().getValue("QUERY_COUNT")); i++) {
		    	for (int j = 0; j < clients.size(); j++) {
		    		System.out.println("client " + j + " started");
		    		PEASDoQueries thread = new PEASDoQueries(j, clients.get(j), receiver[0], Integer.parseInt(receiver[1]), issuer[0], Integer.parseInt(issuer[1]), Config.getInstance().getValue("QUERY"));
		    		thread.start();
		    		/*
			    	clients.get(j).doQuery(receiver[0], 
			    						   Integer.parseInt(receiver[1]), 
			    						   issuer[0], 
			    						   Integer.parseInt(issuer[1]), 
			    						   Config.getInstance().getValue("QUERY"));
			    						   */
		    	}
    		}
	    	
	    } else if (Config.getInstance().getValue("TYPE").equals("onion")) {
			String[] nodes = Config.getInstance().getValue("NODES").split(",");
			List<Map<String, String>> servers = new ArrayList<Map<String, String>>();
			
			// create clients
	    	List<OnionClient> clients = new ArrayList<OnionClient>();
	    	for (int i = 0; i < Integer.parseInt(Config.getInstance().getValue("CLIENT_COUNT")); i++) {
	    		clients.add(new OnionClient());
	    	}
			
	    	// read node addresses
			for (int i = 0; i < nodes.length; i++) {
				String[] host = nodes[i].split(":");

				Map<String, String> node = new HashMap<String, String>();
				node.put("hostname", host[0]);
				node.put("port", host[1]);
				
				servers.add(node);
			}
			
			// send queries
    		for (int i = 0; i < Integer.parseInt(Config.getInstance().getValue("QUERY_COUNT")); i++) {
		    	for (int j = 0; j < clients.size(); j++) {
		    		
		    		OnionDoQueries thread = new OnionDoQueries(j, clients.get(j), servers, Config.getInstance().getValue("QUERY"));
		    		thread.start();
			    	//clients.get(j).doQuery(servers, Config.getInstance().getValue("QUERY"));
		    	}
    		}


	    } 
	}
	
}

	


