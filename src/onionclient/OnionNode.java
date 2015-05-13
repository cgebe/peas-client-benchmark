package onionclient;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;

public class OnionNode {
	
	
	private String hostname;
	private int port;
    private Cipher RSAcipher;
    private KeyPair KeyPair;
    private KeyAgreement KeyAgreement;
    private Cipher AEScipher;
    private Cipher AESdecipher;
    private byte[] payload;
	
	public OnionNode() {
		
	}
	
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Cipher getRSAcipher() {
		return RSAcipher;
	}

	public void setRSAcipher(Cipher rSAcipher) {
		RSAcipher = rSAcipher;
	}

	public KeyPair getKeyPair() {
		return KeyPair;
	}

	public void setKeyPair(KeyPair keyPair) {
		KeyPair = keyPair;
	}

	public KeyAgreement getKeyAgreement() {
		return KeyAgreement;
	}

	public void setKeyAgreement(KeyAgreement keyAgreement) {
		KeyAgreement = keyAgreement;
	}

	public Cipher getAEScipher() {
		return AEScipher;
	}

	public void setAEScipher(Cipher aEScipher) {
		AEScipher = aEScipher;
	}

	public Cipher getAESdecipher() {
		return AESdecipher;
	}

	public void setAESdecipher(Cipher aESdecipher) {
		AESdecipher = aESdecipher;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

}
