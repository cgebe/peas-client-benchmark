package client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;

import protocol.PEASBody;
import protocol.PEASHeader;
import protocol.PEASParser;
import protocol.PEASRequest;
import util.Encryption;
import util.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * Sends one message when a connection is open and echoes back any received
 * data to the server.  Simply put, the echo client initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public final class Client {
	
	private Channel channel;
    private SecretKey currentKey = null;
    //static final boolean SSL = System.getProperty("ssl") != null;
	private IvParameterSpec iv;
    private AsymmetricBlockCipher RSAcipher;
    
    public Client(String connectTo, int port) throws InterruptedException, IOException {
    	
        byte[] ivBytes = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        iv = new IvParameterSpec(ivBytes);
        
        byte[] keyBytes = Files.readAllBytes(Paths.get(".").resolve("pubKey2.der"));
		AsymmetricKeyParameter publicKey = PublicKeyFactory.createKey(keyBytes);
		setRSAEncryptionKey(publicKey);
        
    	// Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             //.option(ChannelOption.TCP_NODELAY, true)
             .handler(new ClientChannelInitializer());

            // Start the client.
            channel = b.connect(connectTo, port).sync().channel();

            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
            	if (line.startsWith("bing")) {
            		String[] splitted = line.split("\\s+");
            		String query = splitted[1];
            		
            		
            		String c = "GET /search?q=" + query + " HTTP/1.1" + System.lineSeparator()
            				 + "Host: www.bing.com";
            		
            		PEASHeader header = new PEASHeader();
            		
            		byte[] content = c.getBytes(Charset.defaultCharset());
            		
            		header.setCommand("QUERY");
            		header.setIssuer(connectTo + ":11779");
            		header.setProtocol("HTTP");
            		
            		try {
						currentKey = Encryption.generateNewKey();
						header.setQuery(createQueryField(currentKey, query));
					} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidCipherTextException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		
            		byte[] encrypted = Encryption.AESencrypt(content, currentKey, iv);

            		header.setContentLength(encrypted.length);
            		PEASBody body = new PEASBody(encrypted.length);
            		body.getContent().writeBytes(encrypted);
            		
            		/*
            		PEASHeader header = new PEASHeader();
            		
            		header.setCommand("KEY");
            		header.setIssuer("127.0.0.1:11779");
            		
            		PEASBody body = new PEASBody(0);
            		*/
            		lastWriteFuture = channel.writeAndFlush(new PEASRequest(header, body));
            		
            		lastWriteFuture.addListener(new ChannelFutureListener() {
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
            		
            	}
            	
            	
            	if ("bye".equals(line.toLowerCase())) {
                    channel.closeFuture().sync();
                    break;
                }
            }
            
            // only if line is null than sync last write
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }

        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        Client c = new Client("127.0.0.1", 11777);
    }
    
    private String createQueryField(SecretKey symKey, String query) throws InvalidKeyException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidCipherTextException {
    	byte[] keyBytes = symKey.getEncoded(); // 16 bytes length
    	byte[] encrypted;
    	
    	// check if query and key is fitting into RSA encrypted block of 117 bytes then place everything into one ==> E(K + Q)
    	// Otherwise split up query into two parts ==> Q1 and Q2 
    	// Q1 is added to the AES key to fill 117 bytes ==> |E(K + Q1)| < 117 bytes
    	// Q2 is normally encrypted via AES with the Key K.
    	// Result is: 
    	// 1. |E(K + Q)| <= 117 ==> Base64(E(K + Q)) or
    	// 2. |E(K + Q)| > 117 ==> Base64(E(K + Q1) + AES(K, Q2))
    	if ((query.length() + keyBytes.length) < 118) {
            byte[] singlePart = new byte[query.length() + keyBytes.length];
            
            // concatenate Key and Query, and encrypt it ==> E(K + Q) only when |K| + |Q| < 118 bytes / 1024 bit key
            System.arraycopy(keyBytes, 0, singlePart, 0, keyBytes.length);
            System.arraycopy(query.getBytes(Charset.defaultCharset()), 0, singlePart, keyBytes.length, query.length());
            
            encrypted = RSAencrypt(singlePart); // encrypted has 117 bytes when 1024bit key was used / later make size variable as parameter to this fucntion
        } else {
            byte[] partOne = new byte[117];
            byte[] partTwo = new byte[query.length() - 117 + keyBytes.length];

            // concat K and Parts of the Query Q to fill 117 bytes ==> E(K + Q1) -> |K| + |Q1| = 117
            System.arraycopy(keyBytes, 0, partOne, 0, keyBytes.length);
            System.arraycopy(query.getBytes(), 0, partOne, keyBytes.length, 117 - keyBytes.length);
            
            // encrypt first part
            partOne = RSAencrypt(partOne);

            // strip rest of the Query Q (Q - Q1 = Q2) from Query Q
            System.arraycopy(query.getBytes(), 117 - keyBytes.length, partTwo, 0, query.length() - 117 + keyBytes.length);
            partTwo = Encryption.AESencrypt(partTwo, symKey, iv);

            // create the result byte array of part one and part two
            encrypted = new byte[partOne.length + partTwo.length];
            
            // concat the byte arrays
            System.arraycopy(partOne, 0, encrypted, 0, partOne.length);
            System.arraycopy(partTwo, 0, encrypted, partOne.length, partTwo.length);
        }
    	
        return Base64.encodeBase64String(encrypted);
    }
    
	private void setRSAEncryptionKey(AsymmetricKeyParameter publicKey) {
      	try {
          	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
          
          	RSAcipher = new PKCS1Encoding(new RSAEngine());
	      	RSAcipher.init(true, publicKey);
      	}
      	catch (Exception e) {
    	  	System.out.println(e);
    	}
    }
	
	private byte[] RSAencrypt(byte[] input) throws InvalidCipherTextException {
      	byte[] encrypted = RSAcipher.processBlock(input, 0, input.length);
      	return encrypted;
	}
	

}
