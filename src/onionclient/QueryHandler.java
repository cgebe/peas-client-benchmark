package onionclient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protocol.PEASBody;
import protocol.PEASHeader;
import protocol.PEASMessage;
import util.Config;
import util.Observer;


public class QueryHandler extends SimpleChannelInboundHandler<PEASMessage> {
	
	private OnionClient client;
	private PEASMessage query;
	private boolean querySent = false;

	public QueryHandler(OnionClient client, PEASMessage req) {
		this.client = client;
		this.query = req;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		try {
			// for response
			PEASHeader header = new PEASHeader();
		
			header.setCommand("HANDSHAKE");
			header.setIssuer("ONION");
			header.setForward(client.createForwarderChain(client.getCurrentWorkingNode()));
			
			byte[] content = client.createHandshakeContent(client.getCurrentWorkingNode());

			header.setContentLength(content.length);
			PEASBody body = new PEASBody(content);
	
			ChannelFuture f = ctx.writeAndFlush(new PEASMessage(header, body));
			
			f.addListener(new ChannelFutureListener() {
	           @Override
	           public void operationComplete(ChannelFuture future) {
	               if (future.isSuccess()) {
	               	   System.out.println("Handshake successful");
	               } else {
	                   System.out.println("Handshake failed");
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
		this.client.setSending(false);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PEASMessage obj) throws Exception {
		if (obj.getHeader().getCommand().equals("RESPONSE")) {
			if (!querySent) {
				byte[] cipherResponseBytes = obj.getBody().getContent().array();
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
					// making handshakes with all nodes
					
					// for response
					PEASHeader header = new PEASHeader();
				
					header.setCommand("HANDSHAKE");
					header.setIssuer("ONION");
					header.setForward(client.createForwarderChain(client.getCurrentWorkingNode()));
					
					byte[] content = client.createHandshakeContent(client.getCurrentWorkingNode());
	
					header.setContentLength(content.length);
					PEASBody body = new PEASBody(content);
			
					ChannelFuture f = ctx.writeAndFlush(new PEASMessage(header, body));
					
					f.addListener(new ChannelFutureListener() {
			           @Override
			           public void operationComplete(ChannelFuture future) {
			               if (future.isSuccess()) {
			               	   //System.out.println("Handshake successful");
			               } else {
			                   //System.out.println("Handshake failed");
			               }
			           }
			        });
				} else {
					// set to last node
					client.setCurrentWorkingNode(client.getNodes().size() - 1);
					
	        		query.getHeader().setForward(client.createForwarderChain(client.getNodes().size() - 1));
	        		query.getHeader().setQuery(Base64.encodeBase64String(client.encryptByteBuf(Unpooled.wrappedBuffer(query.getHeader().getQuery().getBytes())).array()));
	        		query.getBody().setContent(client.encryptByteBuf(query.getBody().getContent()));
					query.getHeader().setContentLength(query.getBody().getContent().capacity());
					
					ChannelFuture f = ctx.writeAndFlush(query);
					
					f.addListener(new ChannelFutureListener() {
					   @Override
					   public void operationComplete(ChannelFuture future) {
					       if (future.isSuccess()) {
					    	   querySent = true;
					       	   //System.out.println("query successful");
					       } else {
					           //System.out.println("query failed");
					       }
					   }
					});
					
	
				}
			} else {
				ctx.close();
				if (Config.getInstance().getValue("MEASURE_QUERY_TIME").equals("on")) {
					client.getQueryTime().setEnd(System.nanoTime());
					String jarPath = new File(Observer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath();
					try {
						PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(jarPath + "/" + "queries.log", true)));
						writer.println(client.getQueryTime().getTimeInMs());
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} 
			
	}
	
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	super.channelInactive(ctx);
    	if (client.isSending()) {
    		this.client.setSending(false);
    	}
    }


}


