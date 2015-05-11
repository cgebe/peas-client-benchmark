package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import protocol.PEASBody;
import protocol.PEASException;
import protocol.PEASHeader;
import protocol.PEASObject;
import protocol.PEASParser;
import protocol.PEASRequest;
import util.Encryption;
import util.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import com.squareup.crypto.rsa.NativeRSAEngine;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.apache.commons.codec.binary.Base64;
/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class ClientHandler extends SimpleChannelInboundHandler<PEASObject> {

	private boolean keyReceived = false;
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws PEASException, InterruptedException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        // Initialization vector building

    	// Cipher Initialisation
        //AEScipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        //byte[] keyBytes = Files.readAllBytes(Paths.get(".").resolve("pubKey2.der"));
		//AsymmetricKeyParameter publicKey = PublicKeyFactory.createKey(keyBytes);
		//setRSAEncryptionKey(publicKey);
		
        //Thread reader = new Reader(ctx);
    	//reader.run();
        
        /*
        PEASHeader header = new PEASHeader();
        header.setCommand("KEY");
        header.setIssuer("127.0.0.1:11779");
        
        PEASRequest req = new PEASRequest(header, new PEASBody(0));
        
        ChannelFuture f2 = ctx.writeAndFlush(req);
		
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
        */
    	
    }
    
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PEASObject obj) throws Exception {
		// suppose first message we send was key request
		
		if (!keyReceived) {
	        //RSA cipher initialization
			//AsymmetricKeyParameter publicKey = PublicKeyFactory.createKey(obj.getBody().getBody().array());
			//setRSAEncryptionKey(publicKey);
			
		} else {
			System.out.println("response to query received");
		}
		
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    
    
    
}