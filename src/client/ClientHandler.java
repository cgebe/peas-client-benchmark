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
import protocol.PEASMessage;
import protocol.PEASParser;
import util.Encryption;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import benchmark.Measurement;

import com.squareup.crypto.rsa.NativeRSAEngine;

import onionclient.OnionClient;

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
public class ClientHandler extends SimpleChannelInboundHandler<PEASMessage> {

	private boolean keyReceived = false;
	private Client client;
	private Measurement m;
	
	public ClientHandler(Client client, Measurement m) {
		this.client = client;
		this.m = m;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PEASMessage obj) throws Exception {
		// suppose first message we send was key request
		ctx.close();
		m.setEnd(System.nanoTime());
		System.out.println("query lasted: " + m.getTimeInMs());
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

    
    
    
}