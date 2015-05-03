package client;

import java.util.HashMap;
import java.util.Map;

import protocol.PEASException;
import protocol.PEASParser;
import protocol.PEASRequest;
import util.Message;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class ClientHandler extends SimpleChannelInboundHandler<Message> {


    public ClientHandler() {
        
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws PEASException {
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
    	Map<String, String> map = new HashMap<String, String>();
    	/*
        map.put("command", "QUERY");
        map.put("issuer", "127.0.0.1:11777");
        map.put("protocol", "http");
        map.put("query", "Planet Erde");
        map.put("body", "request");
        */
        map.put("command", "KEY");
        map.put("issuer", "127.0.0.1:11777");
        
		PEASRequest req = (PEASRequest) PEASParser.parse(map);

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
        
        /*
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
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }


}