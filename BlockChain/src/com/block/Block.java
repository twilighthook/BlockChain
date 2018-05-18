package com.block;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

public class Block {
	
	private String version; 
	private Date Timestamp;
	private String hash;
	private String previousHash;
	private String data;
	private int nonce;
	
	public Block(String version , Date timestamp , String data) {
		this.version = version;
		this.Timestamp = timestamp;
		this.data = data;
		this.hash = computeHash();
	}
	
	public String computeHash() {
		String dataToHash = " " + Integer.toString(nonce) + this.version + this.Timestamp + this.previousHash + this.data;
		
		
		MessageDigest digest;
		String encoded = null;
		
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
			encoded = Base64.getEncoder().encodeToString(hash);
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		
		this.hash = encoded;
		return encoded;
	}
	
	public void mineBlock(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0', '0');
		System.out.println(target);
		while(!hash.substring( 0, difficulty).equals(target)) {
			nonce ++;
			System.out.println(hash.substring( 0, difficulty) + " and " + target + " and " + nonce);
			hash = computeHash();
		}
		System.out.println("Block Mined!!! : " + hash);
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getTimestamp() {
		return Timestamp;
	}

	public void setTimestamp(Date timestamp) {
		Timestamp = timestamp;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
