package codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

import protocol.PEASParser;

public class PEASDecoder extends MessageToMessageDecoder<String> {
	
	@Override
	protected void decode(ChannelHandlerContext ctx, String json, List<Object> out) throws Exception {
		out.add(PEASParser.parse(json));
	}
}
