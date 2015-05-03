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
		StringBuilder request = new StringBuilder();
		
		request.append(header.toString());
		request.append(body.toString());
		
		return request.toString();
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
			map.put("body", body.getBody().toString());
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
	
	public void setBody(PEASBody body) {
		this.body = body;
	}



}
