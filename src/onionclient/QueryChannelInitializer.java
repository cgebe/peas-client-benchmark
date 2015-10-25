package onionclient;


import protocol.PEASMessage;
import util.Config;
import util.PEASPrinterIn;
import util.PEASPrinterOut;
import codec.PEASDecoder;
import codec.PEASEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class QueryChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	private OnionClient client;
	private PEASMessage query;
	
	public QueryChannelInitializer(OnionClient client, PEASMessage req) {
		this.client = client;
		this.query = req;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		// Logging on?
		if (Config.getInstance().getValue("LOGGING").equals("on")) {
			//pipeline.addLast(new LoggingHandler(LogLevel.INFO));
		}
        pipeline.addLast("peasdecoder", new PEASDecoder()); // upstream 1
        pipeline.addLast("peasencoder", new PEASEncoder()); // downstream 1
        
        if (Config.getInstance().getValue("LOGGING").equals("on")) {
        	pipeline.addLast("peasprinterin", new PEASPrinterIn()); // upstream 2
        	pipeline.addLast("peasprinterout", new PEASPrinterOut()); // downstream 1
        }
        pipeline.addLast("queryhandler", new QueryHandler(client, query)); // upstream 3
	}

}
