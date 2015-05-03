package codec;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import util.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class JSONDecoder extends MessageToMessageDecoder<String> {
	
	private JSONParser parser = new JSONParser();

	@Override
	protected void decode(ChannelHandlerContext ctx, String json, List<Object> out) throws Exception {
		JSONObject obj = (JSONObject) parser.parse(json);
		Message msg = new Message();
		msg.setCommand((String) obj.get("command"));
		msg.setQueryId((String) obj.get("id"));
		msg.setQuery((String) obj.get("query"));
		msg.setProtocol((String) obj.get("protocol"));
		msg.setRequest((String) obj.get("request"));
		msg.setSymmetricKey((String) obj.get("skey"));
		msg.setAsymmetricKey((String) obj.get("akey"));
		out.add(msg);
	}

}
