package onionclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protocol.PEASObject;


public class HandshakeHandler extends SimpleChannelInboundHandler<PEASObject> {
	
	private OnionClient client;

	public HandshakeHandler(OnionClient client) {
		this.client = client;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1) throws Exception {
		arg1.printStackTrace();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PEASObject obj) throws Exception {
		if (obj.getHeader().getCommand().equals("RESPONSE")) {
			client.computeKeyAgreement(obj.getBody().getBody().array());
		} 
			
	}


}


