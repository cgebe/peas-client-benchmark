package client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import codec.JSONDecoder;
import codec.JSONEncoder;
import codec.PEASDecoder;
import codec.PEASDecoder3;
import codec.PEASEncoder;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	private ChannelPipeline pipeline;

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		pipeline = ch.pipeline();
		pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        pipeline.addLast("peasdecoder", new PEASDecoder3());
        pipeline.addLast("peasencoder", new PEASEncoder());
        //pipeline.addLast("peasprinter", new PEASPrinter());
        pipeline.addLast("processor", new ClientHandler());
	}

}