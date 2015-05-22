package onionclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import onionclient.query.QueryChannelInitializer;
import onionclient.query.QueryChannelInitializer;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;

import protocol.PEASBody;
import protocol.PEASHeader;
import protocol.PEASRequest;
import util.Encryption;
import client.ClientChannelInitializer;

public final class OnionClient {
	
	List<OnionNode> nodes;

    //static final boolean SSL = System.getProperty("ssl") != null;
	private IvParameterSpec iv;
	
    private final KeyPairGenerator KeyPairGen;
    
	private int currentWorkingNode;

	private boolean keysExchanged = false;
    
    private static final byte ModulusBytes[] = {
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
        (byte) 0xC9, (byte) 0x0F, (byte) 0xDA, (byte) 0xA2,
        (byte) 0x21, (byte) 0x68, (byte) 0xC2, (byte) 0x34,
        (byte) 0xC4, (byte) 0xC6, (byte) 0x62, (byte) 0x8B,
        (byte) 0x80, (byte) 0xDC, (byte) 0x1C, (byte) 0xD1,
        (byte) 0x29, (byte) 0x02, (byte) 0x4E, (byte) 0x08,
        (byte) 0x8A, (byte) 0x67, (byte) 0xCC, (byte) 0x74,
        (byte) 0x02, (byte) 0x0B, (byte) 0xBE, (byte) 0xA6,
        (byte) 0x3B, (byte) 0x13, (byte) 0x9B, (byte) 0x22,
        (byte) 0x51, (byte) 0x4A, (byte) 0x08, (byte) 0x79,
        (byte) 0x8E, (byte) 0x34, (byte) 0x04, (byte) 0xDD,
        (byte) 0xEF, (byte) 0x95, (byte) 0x19, (byte) 0xB3,
        (byte) 0xCD, (byte) 0x3A, (byte) 0x43, (byte) 0x1B,
        (byte) 0x30, (byte) 0x2B, (byte) 0x0A, (byte) 0x6D,
        (byte) 0xF2, (byte) 0x5F, (byte) 0x14, (byte) 0x37,
        (byte) 0x4F, (byte) 0xE1, (byte) 0x35, (byte) 0x6D,
        (byte) 0x6D, (byte) 0x51, (byte) 0xC2, (byte) 0x45,
        (byte) 0xE4, (byte) 0x85, (byte) 0xB5, (byte) 0x76,
        (byte) 0x62, (byte) 0x5E, (byte) 0x7E, (byte) 0xC6,
        (byte) 0xF4, (byte) 0x4C, (byte) 0x42, (byte) 0xE9,
        (byte) 0xA6, (byte) 0x37, (byte) 0xED, (byte) 0x6B,
        (byte) 0x0B, (byte) 0xFF, (byte) 0x5C, (byte) 0xB6,
        (byte) 0xF4, (byte) 0x06, (byte) 0xB7, (byte) 0xED,
        (byte) 0xEE, (byte) 0x38, (byte) 0x6B, (byte) 0xFB,
        (byte) 0x5A, (byte) 0x89, (byte) 0x9F, (byte) 0xA5,
        (byte) 0xAE, (byte) 0x9F, (byte) 0x24, (byte) 0x11,
        (byte) 0x7C, (byte) 0x4B, (byte) 0x1F, (byte) 0xE6,
        (byte) 0x49, (byte) 0x28, (byte) 0x66, (byte) 0x51,
        (byte) 0xEC, (byte) 0xE6, (byte) 0x53, (byte) 0x81,
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,};
    private static final BigInteger Modulus = new BigInteger(1, ModulusBytes);
    private static final BigInteger Base = BigInteger.valueOf(2);
   
 
    public OnionClient(List<Map<String, String>> adresses) throws InterruptedException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException, IllegalStateException {
        this.nodes = new ArrayList<OnionNode>();
        this.currentWorkingNode = 0;
        
        // same key for all nodes
        PublicKey key = readPublicKey(Paths.get(".").resolve("pubKey2.der"));
        
        for (int i = 0; i < adresses.size(); i++) {
        	// create node object for managing
        	OnionNode node = new OnionNode();
        	
        	// set hostname and port
        	node.setHostname(adresses.get(i).get("hostname"));
        	node.setPort(Integer.parseInt(adresses.get(i).get("port")));
        	
    		
        	// rsa cipher init
            node.setRSAcipher(Cipher.getInstance("RSA/ECB/PKCS1Padding"));
            node.getRSAcipher().init(Cipher.ENCRYPT_MODE, key);
            
        	nodes.add(node);
        }
        
        byte[] ivBytes = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        iv = new IvParameterSpec(ivBytes);
        
        // DH Initialisation
        DHParameterSpec dhParamSpec = new DHParameterSpec(Modulus, Base);
        KeyPairGen = KeyPairGenerator.getInstance("DH");
        KeyPairGen.initialize(dhParamSpec);


        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            String line = in.readLine();
            if (line == null) {
                break;
            }

        	if (line.startsWith("query")) {
        		
    			PEASHeader header = new PEASHeader();
        		
        		header.setCommand("QUERY");
        		header.setIssuer("ONION");
        		
        		String query = "erde";
        		
        		String c = "GET /search?q=" + query + " HTTP/1.1" + System.lineSeparator()
                				 + "Host: www.bing.com";
        		
        		header.setQuery(query);
        		header.setContentLength(c.getBytes().length);
        		PEASBody body = new PEASBody(c.getBytes());
        		
    			doQuery(new PEASRequest(header, body));
        		
        	}
        	
        }

    }

    private void doQuery(PEASRequest req) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalStateException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, IOException, InterruptedException {

    	EventLoopGroup group = new NioEventLoopGroup();
        try {
        	
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new QueryChannelInitializer(this, req));
            //.option(ChannelOption.TCP_NODELAY, true)

        	// Start the client.
            Channel ch = b.connect(nodes.get(0).getHostname(), nodes.get(0).getPort()).sync().channel();

    		ch.closeFuture().sync();
            
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }

    }
    

	public String createForwarderChain(int node) throws IllegalBlockSizeException, BadPaddingException {
		StringBuilder chain = new StringBuilder();
		for (int i = node; i > 0; i--) {
			StringBuilder address = new StringBuilder();
			address.append(nodes.get(i).getHostname());
			address.append(":");
			address.append(nodes.get(i).getPort());
			
			// if last node then dont add _ because forward is not there
			if (i != node) {
				address.append("_");
			}
			
			// add address at the beginning of the chain
			chain.insert(0, address.toString());
			
			// encrypt whole chain with key
			chain = new StringBuilder(Base64.encodeBase64String(nodes.get(i - 1).getAEScipher().doFinal(chain.toString().getBytes())));
		}

		if (chain.toString().equals("")) {
			return null;
		} else {
			return chain.toString();
		}
	}

	public byte[] createHandshakeContent(int node) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException {
		nodes.get(node).setKeyPair(KeyPairGen.generateKeyPair());
		nodes.get(node).setKeyAgreement(KeyAgreement.getInstance("DH"));
		nodes.get(node).getKeyAgreement().init(nodes.get(node).getKeyPair().getPrivate());

        //pubKey = Kpair[numberOfRelay].getPublic().getEncoded();
        byte[] pubKey = ((DHPublicKey) nodes.get(node).getKeyPair().getPublic()).getY().toByteArray();

        byte[] cipherRequestBytes = RSAencrypt(pubKey, node);
        for (int i = node; i > 0; i--) {
            cipherRequestBytes = nodes.get(i - 1).getAEScipher().doFinal(cipherRequestBytes);
        }

        return cipherRequestBytes;
	}
	
	
	private byte[] RSAencrypt(byte[] bytes, int node) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] encryptedBytes;
        if (bytes.length < 118) {
        	// if pubkey fits into 117 bytes that can be encrypted by rsa
            encryptedBytes = nodes.get(currentWorkingNode).getRSAcipher().doFinal(bytes);
        } else {
            SecretKey symmetricKey = Encryption.generateNewKey();
            Cipher AEScipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            AEScipher.init(Cipher.ENCRYPT_MODE, symmetricKey, iv);
            byte[] symmetricKeyBytes = symmetricKey.getEncoded();
            byte[] bytes1 = new byte[117];
            byte[] bytes2 = new byte[bytes.length - 117 + symmetricKeyBytes.length];

            // first part 117 bytes of the key/symmetrical key encrypt with rsa
            System.arraycopy(symmetricKeyBytes, 0, bytes1, 0, symmetricKeyBytes.length);
            System.arraycopy(bytes, 0, bytes1, symmetricKeyBytes.length, 117 - symmetricKeyBytes.length);
            bytes1 = nodes.get(currentWorkingNode).getRSAcipher().doFinal(bytes1);

            // rest encrypt with aes
            System.arraycopy(bytes, 117 - symmetricKeyBytes.length, bytes2, 0, bytes.length - 117 + symmetricKeyBytes.length);
            bytes2 = AEScipher.doFinal(bytes2);

            // concat arrays
            encryptedBytes = new byte[bytes1.length + bytes2.length];
            System.arraycopy(bytes1, 0, encryptedBytes, 0, bytes1.length);
            System.arraycopy(bytes2, 0, encryptedBytes, bytes1.length, bytes2.length);

        }
        return encryptedBytes;
    }

	public static void main(String[] args) throws Exception {
		List<Map<String, String>> servers = new ArrayList<Map<String, String>>();
		
		// 1
		Map<String, String> firstServer = new HashMap<String, String>();
		firstServer.put("hostname", "127.0.0.1");
		firstServer.put("port", "12345");
		
		// 2
		Map<String, String> secondServer = new HashMap<String, String>();
		secondServer.put("hostname", "127.0.0.1");
		secondServer.put("port", "12346");
		
		// 3
		Map<String, String> thirdServer = new HashMap<String, String>();
		thirdServer.put("hostname", "127.0.0.1");
		thirdServer.put("port", "12347");
		
		servers.add(firstServer);
		servers.add(secondServer);
		servers.add(thirdServer);
		
        OnionClient c = new OnionClient(servers);
    }
	

    public void computeKeyAgreement(int node, byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalStateException, InvalidAlgorithmParameterException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        KeyFactory KeyFac = KeyFactory.getInstance("DH");

        DHPublicKeySpec dhPublicKeySpec = new DHPublicKeySpec(new BigInteger(bytes), Modulus, Base);
        PublicKey ORPubKey = KeyFac.generatePublic(dhPublicKeySpec);
        nodes.get(node).getKeyAgreement().doPhase(ORPubKey, true);

        final byte[] sharedSecret = nodes.get(node).getKeyAgreement().generateSecret();
        SecretKey symmetricKey = new SecretKeySpec(sharedSecret, 0, 16, "AES");
        System.out.println("sk " + node + ": " + Encryption.bytesToHex(symmetricKey.getEncoded()));
        nodes.get(node).setAEScipher(Cipher.getInstance("AES/CBC/PKCS5Padding"));
        nodes.get(node).getAEScipher().init(Cipher.ENCRYPT_MODE, symmetricKey, iv);
        nodes.get(node).setAESdecipher(Cipher.getInstance("AES/CBC/PKCS5Padding"));
        nodes.get(node).getAESdecipher().init(Cipher.DECRYPT_MODE, symmetricKey, iv);
    }
    
    public int getCurrentWorkingNode() {
    	return currentWorkingNode;
    }
    
    public void setCurrentWorkingNode(int node) {
    	currentWorkingNode = node;
    }
    
    public List<OnionNode> getNodes() {
    	return nodes;
    }
    
    public void getNodes(List<OnionNode> nodes) {
    	this.nodes = nodes;
    }
    
    /**
     * Read a RSA public key from a given file.
     *
     * @param pathOfKey Path to the file containing the key to read
     * @return A public key
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private PublicKey readPublicKey(Path path) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        File file = path.toFile();
        FileInputStream fis = new FileInputStream(file);
        byte[] keyBytes;
        try (DataInputStream dis = new DataInputStream(fis)) {
            keyBytes = new byte[(int) file.length()];
            dis.readFully(keyBytes);
        }
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);

        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

	public ByteBuf encryptByteBuf(ByteBuf buffer) throws IllegalBlockSizeException, BadPaddingException {
		byte[] bytes = buffer.array();
		for (int i = nodes.size(); i > 0; i--) {
            bytes = nodes.get(i - 1).getAEScipher().doFinal(bytes);
        }
		return Unpooled.wrappedBuffer(bytes);
	}
	
	public ByteBuf decryptByteBuf(ByteBuf buffer) throws IllegalBlockSizeException, BadPaddingException {
		byte[] bytes = buffer.array();
		for (int i = 0; i < nodes.size() - 1; i++) {
            bytes = nodes.get(i).getAESdecipher().doFinal(bytes);
        }
		return Unpooled.wrappedBuffer(bytes);
	}


}
