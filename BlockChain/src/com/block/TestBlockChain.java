package com.block;

public class TestBlockChain {
	
	public static int difficulty = 1;

	public static void main(String[] args) {
		
		BlockChain hookCoin = new BlockChain();
		
		Block a = new Block("0");
		Block b = new Block("1");
		Block c = new Block("2");
		
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
