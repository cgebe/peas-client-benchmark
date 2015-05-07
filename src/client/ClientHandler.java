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
    	// send to privacy proxy
        
        
    	/*
        Message msg = new Message();
        msg.setQueryId("100");
        msg.setQuery("Planet Erde");
        msg.setProtocol("http");
        msg.setRequest("GET / HTTP/1.1\nHost: google.com");
        msg.setRecieverHost("localhost");
        msg.setRecieverPort("11777");
        msg.setIssuerHost("localhost");
        msg.setIssuerPort("11779");
        //ctx.writeAndFlush(msg);
        System.out.println("sent");
        //ChannelFuture f = ctx.writeAndFlush(Unpooled.copiedBuffer("Netty MAY rock!", CharsetUtil.UTF_8));
        */
    	/*
    	
    	Map<String, String> map = new HashMap<String, String>();
    	
    	byte[] sample = hexStringToByteArray("e04f");
    	
        map.put("command", "QUERY");
        map.put("issuer", "127.0.0.1:11779");
        map.put("protocol", "HTTP");
        map.put("bodylength", String.valueOf(sample.length));
        map.put("query", "Planet Erde");
        
        /*
        map.put("command", "KEY");
        map.put("issuer", "127.0.0.1:11777");
        
        
		String s = "QUERY 127.0.0.1:11779" + System.getProperty("line.separator")
				  + "Query: TESTQUERY" + System.getProperty("line.separator")
				  + "Protocol: HTTP" + System.getProperty("line.separator")
				  + "Content-Length: " + String.valueOf(sample.length);
		
		//String s = "KEY 127.0.0.1:11779";
        
		//PEASHeader header = (PEASHeader) PEASParser.parseHeader(s);
		
		//PEASBody body = new PEASBody(sample.length);
		//body.getBody().writeBytes(sample);
		
		//PEASRequest req = new PEASRequest(header, new PEASBody(0));
        

        ChannelFuture f = ctx.writeAndFlush(req);
        
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
            	//future.channel().close();
                if (future.isSuccess()) {
                	//future.channel().writeAndFlush(msg);
                	System.out.println("success write");
                    // ctx.channel().read();
                } else {
                    //future.channel().close();
                    System.out.println("failed write");
                }
            }
        });

        Map<String, String> map = new HashMap<String, String>();
        map.put("command", "QUERY");
        map.put("issuer", "127.0.0.1:11777");
        map.put("protocol", "http");
        map.put("query", "Planet Erde");
        map.put("body", "request");
        
        PEASRequest req = (PEASRequest) PEASParser.parse(map);
        
        System.out.println(req.toString());

        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
            	future.channel().close();
                if (future.isSuccess()) {
                	future.channel().writeAndFlush("test\r\n");
                	System.out.println("sent msg");
                    // ctx.channel().read();
                } else {
                    future.channel().close();
                    System.out.println("closed");
                }
            }
        });

        */
        
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

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
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
                        	//future.channel().close();
                            if (future.isSuccess()) {
                            	//future.channel().writeAndFlush(msg);
                            	System.out.println("sending successful");
                                // ctx.channel().read();
                            } else {
                                //future.channel().close();
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
        }

    }
}