package protocol;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

public class PEASRequest extends PEASObject {
	
	public PEASRequest(PEASHeader header, PEASBody body) {
		this.header = header;
		this.body = body;
	}

	@Override
	public String toString() {
		StringBuilder response = new StringBuilder();
		
		response.append(header.getCommand());
		response.append(" ");
		response.append(header.getIssuer());
		
		if (header.getQuery() != null) {
			response.append(System.lineSeparator());
			response.append(header.getQuery());
		} 
		
		if (header.getProtocol() != null) {
			response.append(System.lineSeparator());
			response.append(header.getProtocol());
			response.append(" ");
			response.append(header.getBodyLength());
		} 
		
		if (body.getBody() != null) {
			response.append(System.lineSeparator());
			response.append(body.getBody());
		}
		response.append(System.lineSeparator());
		
		return response.toString();
	}
	
	@Override
	public String toJSONString() {
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("command", header.getCommand());
		map.put("issuer", header.getIssuer());
		
		if (header.getProtocol() != null) {
			map.put("protocol", header.getProtocol());
		} 
		
		if (header.getQuery() != null) {
			map.put("query", header.getQuery());
		}
		
		if (body.getBody() != null && header.getBodyLength() > 0) {
			map.put("body", body.getBody());
			map.put("bodylength", String.valueOf(header.getBodyLength()));
		}
		
		return JSONValue.toJSONString(map);
	}

	public PEASHeader getHeader() {
		return header;
	}
	
	public void setHeader(PEASHeader header) {
		this.header = header;
	}
	
	public PEASBody getBody() {
		return body;
	}
	
	public void setHeader(PEASBody body) {
		this.body = body;
	}


}
