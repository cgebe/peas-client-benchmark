package codec;


import java.util.List;
import util.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class JSONEncoder extends MessageToMessageEncoder<Message>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		System.out.println(msg.getRequest());
		out.add(msg.toJSONString());
	}

}
