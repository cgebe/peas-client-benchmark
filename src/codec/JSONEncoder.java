package codec;

import java.util.List;

import protocol.PEASObject;
import util.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class JSONEncoder extends MessageToMessageEncoder<PEASObject> {

	@Override
	protected void encode(ChannelHandlerContext ctx, PEASObject obj, List<Object> out) throws Exception {
		out.add(obj.toJSONString());
	}

}
