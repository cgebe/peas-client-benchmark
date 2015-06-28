package client;

import benchmark.Measurement;
import util.Config;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import codec.PEASDecoder;
import codec.PEASEncoder;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	private Client client;
	private Measurement m;
	
	public ClientChannelInitializer(Client client) {
		this.client = client;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		// Logging on?
		if (Config.getInstance().getValue("LOGGING").equals("on")) {
			//pipeline.addLast(new LoggingHandler(LogLevel.INFO));
		}
        pipeline.addLast("peasdecoder", new PEASDecoder());
        pipeline.addLast("peasencoder", new PEASEncoder());
        
        if (Config.getInstance().getValue("LOGGING").equals("on")) {
        	pipeline.addLast("peasprinter", new PEASPrinter());
        }
        pipeline.addLast("processor", new ClientHandler(client));
	}

}