package protocol;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

public class PEASHeader {
	
	private String command;
	private String query;
	private String issuer;
	private String protocol;
	private String status;
	private int bodyLength;
	
	public PEASHeader() {
		command = null;
		query = null;
		issuer = null;
		protocol = null;
		status = null;
	}
	
	public String toString() {
		StringBuilder request = new StringBuilder();
		
		request.append(this.getCommand());
		request.append(" ");
		request.append(this.getIssuer());
		
		if (this.getStatus() != null) {
			request.append(System.lineSeparator());
			request.append("Status: ");
			request.append(this.getStatus());
		} 
		
		if (this.getProtocol() != null) {
			request.append(System.lineSeparator());
			request.append("Protocol: ");
			request.append(this.getProtocol());
		} 
		
		if (this.getBodyLength() > 0) {
			request.append(System.lineSeparator());
			request.append("Content-Length: ");
			request.append(this.getBodyLength());
		} 
		
		if (this.getQuery() != null) {
			request.append(System.lineSeparator());
			request.append("Query: ");
			request.append(this.getQuery());
		} 
		request.append(System.lineSeparator());
		request.append(System.lineSeparator());
		
		return request.toString();
	}
	

	public String toJSONString() {
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("command", this.getCommand());
		map.put("issuer", this.getIssuer());
		
		if (this.getStatus() != null) {
			map.put("status", this.getStatus());
		} 
		
		if (this.getProtocol() != null) {
			map.put("protocol", this.getProtocol());
		} 
		
		if (this.getQuery() != null) {
			map.put("query", this.getQuery());
		}
		
		if (this.getBodyLength() > 0) {
			map.put("bodylength", String.valueOf(this.getBodyLength()));
		}
		
		return JSONValue.toJSONString(map);
	}
	
	

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	
	public String getIssuerAddress() {
		return issuer.split(":")[0];
	}
	
	public int getIssuerPort() {
		return Integer.parseInt(issuer.split(":")[1]);
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getBodyLength() {
		return bodyLength;
	}

	public void setBodyLength(int bodyLength) {
		this.bodyLength = bodyLength;
	}

}
