package onionclient;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protocol.PEASBody;
import protocol.PEASHeader;
import protocol.PEASObject;
import protocol.PEASRequest;
import util.Encryption;


public class HandshakeHandler extends SimpleChannelInboundHandler<PEASObject> {
	
	private OnionClient client;

	public HandshakeHandler(OnionClient client) {
		this.client = client;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		try {
			// for response
			PEASHeader header = new PEASHeader();
		
			header.setCommand("HANDSHAKE");
			header.setIssuer(client.getNodes().get(client.getCurrentWorkingNode()).getHostname() + ":" + client.getNodes().get(client.getCurrentWorkingNode()).getPort());
			header.setForward(client.createForwarderChain(client.getCurrentWorkingNode()));
			
			byte[] content = client.createHandshakeContent(client.getCurrentWorkingNode());
			System.out.println("1: " + Encryption.bytesToHex(content));
			header.setBodyLength(content.length);
			PEASBody body = new PEASBody(content);
	
			ChannelFuture f = ctx.writeAndFlush(new PEASRequest(header, body));
			
			f.addListener(new ChannelFutureListener() {
	           @Override
	           public void operationComplete(ChannelFuture future) {
	               if (future.isSuccess()) {
	               	   System.out.println("handshake successful");
	               } else {
	                   System.out.println("handshake failed");
	                   future.channel().close();
	               }
	           }
	        });
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IOException e) {
			ctx.close();
			e.printStackTrace();
		}
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1) throws Exception {
		arg1.printStackTrace();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PEASObject obj) throws Exception {
		if (obj.getHeader().getCommand().equals("RESPONSE")) {
			byte[] cipherResponseBytes = obj.getBody().getBody().array();
			// decrypt if it was forwarded
			for (int i = 0; i < client.getCurrentWorkingNode(); i++) {
				cipherResponseBytes = client.getNodes().get(i).getAESdecipher().doFinal(cipherResponseBytes);
			}
			
			// get handshake for current node
			client.computeKeyAgreement(client.getCurrentWorkingNode(), cipherResponseBytes);
			
			// to next working node
			client.setCurrentWorkingNode(client.getCurrentWorkingNode() + 1);
			
			// send handshake request for next node
			if (client.getCurrentWorkingNode() < client.getNodes().size()) {
				// for response
				PEASHeader header = new PEASHeader();
			
				header.setCommand("HANDSHAKE");
				header.setIssuer(client.getNodes().get(0).getHostname() + ":" + client.getNodes().get(0).getPort());
				header.setForward(client.createForwarderChain(client.getCurrentWorkingNode()));
				
				byte[] content = client.createHandshakeContent(client.getCurrentWorkingNode());

				header.setBodyLength(content.length);
				PEASBody body = new PEASBody(content);
		
				ChannelFuture f = ctx.writeAndFlush(new PEASRequest(header, body));
				
				f.addListener(new ChannelFutureListener() {
		           @Override
		           public void operationComplete(ChannelFuture future) {
		               if (future.isSuccess()) {
		               	   System.out.println("handshake successful");
		               } else {
		                   System.out.println("handshake failed");
		                   future.channel().close();
		               }
		           }
		        });
			} else {
				ctx.close();
				// set to last node
				client.setCurrentWorkingNode(client.getNodes().size() - 1);
			}
		} 
			
	}


}


