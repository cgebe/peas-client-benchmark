package onionclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protocol.PEASObject;


public class HandshakeHandler extends SimpleChannelInboundHandler<PEASObject> {
	
	private OnionNode node;

	public HandshakeHandler(OnionNode node) {
		this.node = node;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1) throws Exception {
		arg1.printStackTrace();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PEASObject obj) throws Exception {
		if (obj.getHeader().getCommand().equals("RESPONSE")) {
			node.setPayload(obj.getBody().getBody().array());
		} 
			
	}


}


