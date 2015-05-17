package onionclient;

import receiver.handler.upstream.PEASPrinter;
import codec.PEASDecoder3;
import codec.PEASEncoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class QueryChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	private ChannelPipeline pipeline;


	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		pipeline = ch.pipeline();
		//pipeline.addLast("framer", new FixedLengthFrameDecoder(1));
        pipeline.addLast("peasdecoder", new PEASDecoder3()); // upstream 1
        pipeline.addLast("peasencoder", new PEASEncoder()); // downstream 1
        pipeline.addLast("peasprinter", new PEASPrinter()); // upstream 2
        //pipeline.addLast("handshakehandler", new HandshakeHandler(client)); // upstream 3
	}

}
