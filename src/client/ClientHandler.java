package client;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import protocol.PEASBody;
import protocol.PEASException;
import protocol.PEASHeader;
import protocol.PEASObject;
import protocol.PEASParser;
import protocol.PEASRequest;
import util.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import org.apache.commons.codec.binary.Base64;
/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class ClientHandler extends SimpleChannelInboundHandler<PEASObject> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws PEASException, InterruptedException {
    	Thread reader = new Reader(ctx);
    	reader.run();
    }
    
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PEASObject obj) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println(obj.toString());
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    class Reader extends Thread {
    	
    	private ChannelHandlerContext ctx;
    	
    	public Reader(ChannelHandlerContext ctx) {
    		this.ctx = ctx;
    	}

        @Override
        public void run() {
            final Scanner in = new Scanner(System.in);
            while (in.hasNext()) {
            	String line = in.nextLine();
            	if (line.startsWith("bing")) {
            		String[] splitted = line.split("\\s+");
            		String query = splitted[1];
            		
            		String c = "GET /search?q=" + query + " HTTP/1.1" + System.lineSeparator()
            				 + "Host: www.bing.com";
            		
            		PEASHeader header = new PEASHeader();
            		
            		byte[] content = c.getBytes(Charset.defaultCharset());
            		
            		header.setCommand("QUERY");
            		header.setIssuer("127.0.0.1:11779");
            		header.setProtocol("HTTP");
            		header.setBodyLength(content.length);
            		header.setQuery(query);
            		
            		PEASBody body = new PEASBody(content.length);
            		body.getBody().writeBytes(content);
            		
            		PEASRequest req = new PEASRequest(header, body);
            		
            		System.out.println(req.toString());
            		System.out.println(c);
            		
            		ChannelFuture f2 = ctx.writeAndFlush(new PEASRequest(header, body));
            		
            		f2.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) {
                            if (future.isSuccess()) {
                            	System.out.println("sending successful");
                            } else {
                                System.out.println("sending failed");
                                future.channel().close();
                            }
                        }
                    });
            		
            	}
            	
            	
            	if (line.startsWith("close")) {
            		// Wait until the connection is closed.
            		
            	}
            	
            }
            in.close();
        }

    }
}