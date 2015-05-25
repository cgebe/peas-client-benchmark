package onionclient;


import benchmark.Measurement;
import protocol.PEASMessage;
import receiver.handler.upstream.PEASPrinter;
import util.Config;
import codec.PEASDecoder3;
import codec.PEASEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class QueryChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	private ChannelPipeline pipeline;
	private OnionClient client;
	private PEASMessage query;
	private Measurement m;
	
	public QueryChannelInitializer(OnionClient client, PEASMessage req, Measurement m) {
		this.client = client;
		this.query = req;
		this.m = m;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		pipeline = ch.pipeline();
		
		// Logging on?
		if (Config.getInstance().getValue("LOGGING").equals("on")) {
			pipeline.addLast(new LoggingHandler(LogLevel.INFO));
		}
        pipeline.addLast("peasdecoder", new PEASDecoder3()); // upstream 1
        pipeline.addLast("peasencoder", new PEASEncoder()); // downstream 1
        pipeline.addLast("peasprinter", new PEASPrinter()); // upstream 2
        pipeline.addLast("queryhandler", new QueryHandler(client, query, m)); // upstream 3
	}

}
