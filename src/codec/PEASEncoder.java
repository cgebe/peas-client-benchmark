package codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import protocol.PEASObject;

public class PEASEncoder extends MessageToMessageEncoder<PEASObject>{

	@Override
	protected void encode(ChannelHandlerContext ctx, PEASObject obj, List<Object> out) throws Exception {
		out.add(obj.toString());
	}
}
