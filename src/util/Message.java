package util;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

public class Message {
	
	private String id;
	private String command;
	
	private String query;
	private String request;
	private String response;
	private String protocol;
	
	private String akey;
	private String skey;
	
	private String rHost;
	private String rPort;
	
	private String iHost;
	private String iPort;

	public Message() {
		this.command = null;
		this.id = null;
		this.query = null;
		this.protocol = null;
		this.request = null;
		this.skey = null;
		this.akey = null;
	}
	
	// what to do? getting asymmetric key, doing query etc.
	public String getCommand() {
		return this.command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}

	// set by the proxy to determine the identity of the searcher
	public String getQueryId() {
		return this.id;
	}
	
	public void setQueryId(String id) {
		this.id = id;
	}
	
	// query of the searcher, modular for the issuer to build group profile
	public String getQuery() {
		return this.query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	// request protocol, in which protocol issuer has to send the request, mostly http
	public String getProtocol() {
		return this.protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public String getRequest() {
		return this.request;
	}
	
	public void setRequest(String request) {
		this.request = request;
	}
	
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	// for key exchange
	public String getSymmetricKey() {
		return this.skey;
	}
	
	public void setSymmetricKey(String skey) {
		this.skey = skey;
	}
	
	public String getAsymmetricKey() {
		return this.akey;
	}
	
	public void setAsymmetricKey(String akey) {
		this.akey = akey;
	}
	
	public String getRecieverHost() {
		return rHost;
	}

	public void setRecieverHost(String rHost) {
		this.rHost = rHost;
	}

	public String getRecieverPort() {
		return rPort;
	}

	public void setRecieverPort(String rPort) {
		this.rPort = rPort;
	}

	public String getIssuerHost() {
		return iHost;
	}

	public void setIssuerHost(String iHost) {
		this.iHost = iHost;
	}

	public String getIssuerPort() {
		return iPort;
	}

	public void setIssuerPort(String iPort) {
		this.iPort = iPort;
	}
	
	public String toJSONString() {
		Map<String, String> msg = new HashMap<String, String>();
		/*
		msg.put("command", this.command);
		msg.put("id", this.id);
		msg.put("query", this.query);
		msg.put("protocol", this.protocol);
		msg.put("request", this.request);
		msg.put("skey", this.skey);
		msg.put("akey", this.akey);
		*/
		if (this.command != null) {
			msg.put("command", this.command);
		}
		if (this.id != null) {
			msg.put("id", this.id);
		}
		
		if (this.query != null) {
			msg.put("query", this.query);
		}
		
		if (this.protocol != null) {
			msg.put("protocol", this.protocol);
		}
		
		if (this.request != null) {
			msg.put("request", this.request);
		}
		
		if (this.response != null) {
			msg.put("response", this.response);
		}
		
		if (this.skey != null) {
			msg.put("s_key", this.skey);
		}
		
		if (this.akey != null) {
			msg.put("a_key", this.akey);
		}
		
		if (this.rHost != null) {
			msg.put("r_host", this.rHost);
		}
		
		if (this.rPort != null) {
			msg.put("r_port", this.rPort);
		}
		
		if (this.iHost != null) {
			msg.put("i_host", this.iHost);
		}
		
		if (this.iPort != null) {
			msg.put("i_port", this.iPort);
		}
		
		return JSONValue.toJSONString(msg);
	}




}
