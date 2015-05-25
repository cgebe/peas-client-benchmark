package client;

import protocol.PEASMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PEASPrinter extends SimpleChannelInboundHandler<PEASMessage> {
	
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	cause.printStackTrace();
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PEASMessage obj) throws Exception {
		System.out.println(obj.toString());
		ctx.fireChannelRead(obj);
	}
    


}