package codec;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import protocol.PEASBody;
import protocol.PEASHeader;
import protocol.PEASObject;
import protocol.PEASRequest;
import protocol.PEASResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class JSONDecoder extends MessageToMessageDecoder<String> {
	
	private JSONParser parser = new JSONParser();

	@Override
	protected void decode(ChannelHandlerContext ctx, String json, List<Object> out) throws Exception {
		JSONObject obj = (JSONObject) parser.parse(json);
		out.add(PEASObjectFromJSONObject(obj));
	}

	private PEASObject PEASObjectFromJSONObject(JSONObject obj) {
		String c = (String) obj.get("command");
		if (c.equals("KEY")) {
			PEASHeader header = new PEASHeader();
			header.setCommand(c);
			header.setIssuer((String) obj.get("issuer"));
			
			return new PEASRequest(header, new PEASBody(0));
		} else if (c.equals("QUERY")) {
			PEASHeader header = new PEASHeader();
			header.setCommand(c);
			header.setIssuer((String) obj.get("issuer"));
			header.setProtocol((String) obj.get("protocol"));
			header.setBodyLength((int) obj.get("bodylength"));
			header.setQuery((String) obj.get("query"));

			return new PEASRequest(header, new PEASBody(0));
		} else if (c.equals("RESPONSE")) {
			PEASHeader header = new PEASHeader();
			header.setCommand(c);
			header.setStatus((String) obj.get("status"));
			header.setProtocol((String) obj.get("protocol"));
			header.setBodyLength((int) obj.get("bodylength"));
			
			
			return new PEASResponse(header, new PEASBody(0));
		} else {
			return new PEASResponse(new PEASHeader(), new PEASBody(0));
		}
	}
}
