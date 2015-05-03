package protocol;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

public class PEASResponse extends PEASObject {

	public PEASResponse(PEASHeader header, PEASBody body) {
		this.header = header;
		this.body = body;
	}
	
	@Override
	public String toString() {
		StringBuilder response = new StringBuilder();
		
		response.append(header.getCommand());
		response.append(" ");
		response.append(header.getStatus());
		response.append(System.lineSeparator());
		response.append(body.getBody());
		response.append(System.lineSeparator());
		
		return response.toString();
	}
	
	@Override
	public String toJSONString() {
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("command", header.getCommand());
		map.put("status", header.getStatus());
		map.put("body", body.getBody());

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
