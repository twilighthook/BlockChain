package com.block;

public class TestBlockChain {
	
	public static int difficulty = 1;

	public static void main(String[] args) {
		
		BlockChain hookCoin = new BlockChain();
		
		Block a = new Block("0X200" , new java.util.Date() , "<transactions>");
		Block b = new Block("0X200" , new java.util.Date() , "<transactions>");
		Block c = new Block("0X200" , new java.util.Date() , "<transactions>");
		
		hookCoin.addBlock(a);
		System.out.println("Trying to Mine block 1... ");
		a.mineBlock(difficulty);
		
		hookCoin.addBlock(b);
		System.out.println("Trying to Mine block 2... ");
		b.mineBlock(difficulty);
		
		hookCoin.addBlock(c);
		System.out.println("Trying to Mine block 3... ");
		c.mineBlock(difficulty);
		
		hookCoin.displayChain();
		
	}
	
}
