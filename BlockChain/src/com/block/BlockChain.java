package com.block;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.wallet.Wallet;
import com.transaction.*;

public class BlockChain {
	
	public ArrayList<Block> blockchain = new ArrayList<Block>();
	//Unspent Transaction Output
	public static HashMap<String , TransactionOutput> UTXOs = new HashMap<String , TransactionOutput>();  
	public static float minimumTransaction = 0.1f;
	public static int difficulty = 5;
	public static Wallet walletA;
	public static Wallet walletB;
	
	public BlockChain() {
		blockchain.add(generateGenesis());
	}
	
	private Block generateGenesis() {
		
		Block genesis = new Block("0X200" , new java.util.Date() , "<transactions>");
		//first node(genesis) needn't previousHash
		genesis.setPreviousHash(null);
		genesis.computeHash();
		
		
		return genesis;
	}
	
	public void addBlock(Block blk) {
		Block newBlock = blk;
		newBlock.setPreviousHash( blockchain.get(blockchain.size()-1).getHash() );
		newBlock.computeHash();
		this.blockchain.add(newBlock);
	}
	
	public void displayChain() {
		
		for(int i = 0 ; i < blockchain.size() ; i++) {
			System.out.println("Block: " + i);
			System.out.println("Version: " + blockchain.get(i).getVersion());
			System.out.println("Timestamp: " + blockchain.get(i).getTimestamp());
			System.out.println("PreviousHash: " + blockchain.get(i).getPreviousHash());
			System.out.println("Hash: " + blockchain.get(i).getHash());
			System.out.println();
		}
	}
	
	public Block getLastestBlock() {
		return this.blockchain.get( blockchain.size() - 1 );
	}
	
	public void isValid() {
		for(int i = blockchain.size() - 1 ; i > 0 ; i--) {
			if( !(blockchain.get(i).getHash().equals(blockchain.get(i).computeHash())) ) {
				System.out.println("Chain is not valid!");
				return;
			}
			if( !(blockchain.get(i).getPreviousHash().equals(blockchain.get(i-1).computeHash())) ) {
				System.out.println("Chain is not valid!");
				return;
			}
		}
		System.out.println("Chain is valid!");
	}

}
