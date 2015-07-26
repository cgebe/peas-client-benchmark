package client;





import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import protocol.PEASMessage;
import util.Config;
import util.Observer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import benchmark.Measurement;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class ClientHandler extends SimpleChannelInboundHandler<PEASMessage> {

	private boolean keyReceived = false;
	private Client client;
	
	public ClientHandler(Client client) {
		this.client = client;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PEASMessage obj) throws Exception {
		// suppose first message we send was key request
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
		if (!keyReceived) {
	        //RSA cipher initialization
			//AsymmetricKeyParameter publicKey = PublicKeyFactory.createKey(obj.getBody().getBody().array());
			//setRSAEncryptionKey(publicKey);
			
		} else {
			
		}
    	
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	super.channelInactive(ctx);
    }

    
}