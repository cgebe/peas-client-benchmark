package client;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Scanner;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;

import com.squareup.crypto.rsa.NativeRSAEngine;

import benchmark.Measurement;
import protocol.PEASBody;
import protocol.PEASHeader;
import protocol.PEASMessage;
import util.Config;
import util.Encryption;
import util.Observer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ResourceLeakDetector;
import issuer.server.IssuerServer;


/**
 * Sends one message when a connection is open and echoes back any received
 * data to the server.  Simply put, the echo client initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public final class Client {
	
    private SecretKey currentKey = null;
    //static final boolean SSL = System.getProperty("ssl") != null;
	private IvParameterSpec iv;
    private AsymmetricBlockCipher RSAcipher;
    private boolean isSending;
	private Measurement queryTime;
    
    public Client() throws InterruptedException, IOException, URISyntaxException {
    	this.isSending = false;
    	this.queryTime = new Measurement();
        byte[] ivBytes = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        iv = new IvParameterSpec(ivBytes);
        
        //byte[] keyBytes = Files.readAllBytes(Paths.get("./resources/").resolve("pubKey2.der"));
        String jarPath = new File(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath();
        InputStream inputStream = new FileInputStream(new File(jarPath + "/resources/pubKey2.der"));
        //new FileInputStream(new File("config/config.xml"));
        //InputStream inputStream = Client.class.getClassLoader().getResourceAsStream("pubKey2.der");
        byte[] keyBytes = IOUtils.toByteArray(inputStream);
        
		AsymmetricKeyParameter publicKey = PublicKeyFactory.createKey(keyBytes);
		setRSAEncryptionKey(publicKey);
		
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
		
    }
    
    public static void main(String[] args) throws Exception {
    	Client c = new Client();
    	
    	Scanner sc = new Scanner(System.in);

		while (true) {
			try {
				System.out.print("Enter your query: ");
			
				String query = sc.nextLine();
				
				System.out.println();
				c.doQuery("localhost", 11777, "localhost", 11779, query);
				
			} catch (InvalidKeyException | NoSuchAlgorithmException
					| InvalidAlgorithmParameterException
					| InvalidCipherTextException e) {
				sc.close();
				e.printStackTrace();
			}
		}
    }
    
    public void doQuery(String receiver, int receiverPort, String issuer, int issuerPort, String query) throws InterruptedException, InvalidKeyException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidCipherTextException {
    	if (Config.getInstance().getValue("MEASURE_QUERY_TIME").equals("on")) {
    		queryTime.setBegin(System.nanoTime());
    	}
    	
    	// Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             //.option(ChannelOption.TCP_NODELAY, true)
             .handler(new ClientChannelInitializer(this));

    		String c = "GET /search?q=" + query + " HTTP/1.1" + System.lineSeparator()
    				 + "Host: www.google.com";
    		
    		System.out.println(c);
    		System.out.println();
    		
    		PEASHeader header = new PEASHeader();
    		
    		byte[] content = c.getBytes(Charset.defaultCharset());
    		
    		header.setCommand("QUERY");
    		header.setIssuer(issuer + ":" + issuerPort);
    		header.setProtocol("HTTP");
    		
			currentKey = Encryption.generateNewKey();
			header.setQuery(createQueryField(currentKey, query));

    		byte[] encrypted = Encryption.AESencrypt(content, currentKey, iv);

    		header.setContentLength(encrypted.length);
    		PEASBody body = new PEASBody(encrypted.length);
    		body.getContent().writeBytes(encrypted);
    		
            // Start the client.
    		ChannelFuture f1 = b.connect(receiver, receiverPort);
    		
    		f1.addListener(new ChannelFutureListener() {
                 @Override
                 public void operationComplete(ChannelFuture future) {
                     if (future.isSuccess()) {
                     	System.out.println("Connection To Receiver/Node Established");
                     } else {
                     	System.out.println("Connection To Receiver/Node Failed");
                     	future.cause().printStackTrace();
                     }
                 }
             });
    		
            Channel ch = f1.sync().channel();
            
    		ChannelFuture f = ch.writeAndFlush(new PEASMessage(header, body));
    		
    		f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                    	//System.out.println("sending successful");
                    } else {
                    	//System.out.println("sending failed");
                    }
                }
            });
    		
    		ch.closeFuture().sync();
    		
    		ch.close().syncUninterruptibly();
    		
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    	
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

	public boolean isSending() {
		return isSending;
	}

	public void setSending(boolean isSending) {
		this.isSending = isSending;
	}

	public Measurement getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(Measurement queryTime) {
		this.queryTime = queryTime;
	}

}
