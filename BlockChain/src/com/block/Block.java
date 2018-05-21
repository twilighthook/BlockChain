package com.block;
import java.util.ArrayList;
import java.util.Date;

import com.transaction.StringUtil;
import com.transaction.Transaction;

public class Block {
	
	private long Timestamp;
	public String merkleRoot;
	private String hash;
	private String previousHash;
	private int nonce;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); 
	
	//Add new block need preHash to create
	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.Timestamp = new Date().getTime();
		
		//Making sure we do this after we set the other values.
		this.hash = computeHash();
	}
	
	public String computeHash() {
		String dataToHash = 
				previousHash + 
				Long.toString(Timestamp) + 
				Integer.toString(nonce) + 
				merkleRoot;
		
		String encoded = StringUtil.applySha256(dataToHash);
		
		return encoded;
	}
	
	public void mineBlock(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0', '0');
		while(!hash.substring( 0, difficulty).equals(target)) {
			nonce ++;
			//System.out.println(hash.substring( 0, difficulty) + " and " + target + " and " + nonce);
			hash = computeHash();
		}
		System.out.println("Block Mined!!! : " + hash);
	}
	
	//Add transactions to this block
	public boolean addTransaction(Transaction transaction) {
		if(transaction == null) return false;
		
		if(previousHash != "0") {
			if(transaction.processTransaction() != true) {
				System.out.println("Transaction failed to process. Discard!");
				return false;
			}
		}
		
		//the transaction array in block
		transactions.add(transaction);
		System.out.println("Transaction successfully added to Block!");
		
		return true;
	}

	public long getTimestamp() {
		return Timestamp;
	}

	public void setTimestamp(long timestamp) {
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

}
