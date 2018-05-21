package com.block;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import com.wallet.Wallet;
import com.transaction.*;

public class BlockChain {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	// Unspent Transaction Output
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	
	public static float minimumTransaction = 0.1f;
	public static int difficulty = 3;
	public static Wallet walletA;
	public static Wallet walletB;
	public static Transaction genesisTransaction;

	public static void main(String[] args) {

		//// Setup Bouncey castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// create wallet
		walletA = new Wallet();
		walletB = new Wallet();
		Wallet coinbase = new Wallet();

		// create genesis transaction, which sends 100 Coin to walletA:
		genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
		genesisTransaction.generateSignature(coinbase.privateKey); // signature the wallet
		genesisTransaction.transactionId = "0";
		genesisTransaction.tranOutputs.add(new TransactionOutput(genesisTransaction.reciepient,
				genesisTransaction.value, genesisTransaction.transactionId));
		UTXOs.put(genesisTransaction.tranOutputs.get(0).id, genesisTransaction.tranOutputs.get(0));

		System.out.println("Creating and Mining Genesis block...");
		// create the first block for block chain use
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);

		// testing
		Block block1 = new Block(genesis.getHash());
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
		addBlock(block1);
		// After the adding transaction the balance in wallet will be changed
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		Block block2 = new Block(block1.getHash());
		// It will get a error with not enough balance in walletA
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		Block block3 = new Block(block2.getHash());
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		isChainValid();
	}

	public static Boolean isChainValid() {

		Block currentBlock;
		Block previousBlock;
		// '\0' is empty char
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();

		// loop check each block in BlockChain
		for (int i = 1; i < blockchain.size(); i++) {

			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);

			// checkpoint1 : compare the block registered hash and calculate hash
			if (!currentBlock.getHash().equals(currentBlock.computeHash())) {
				System.out.println("#Current Hashes not equal");
				return false;
			}

			// checkpoint2 : compare the previous block registered hash and calculate hash
			if (!previousBlock.getHash().equals(previousBlock.computeHash())) {
				System.out.println("#Previous Hashes not equal");
				return false;
			}

			// checkpoint3 : make sure the block had been mined
			if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
				System.out.println("#This block hasn't been mined");
				return false;
			}

			// checkpoint4 : loop check each transaction in each block
			TransactionOutput tempOutput;
			for (int t = 0; t < currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);

				// checkpoint4-1 : check whether the transaction is verified
				if (!currentTransaction.verifiySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false;
				}

				// checkpoint4-2 : check the value of output and input transaction is equal
				if (!(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue())) {
					System.out.println("#The value check between output and input is not equal");
				}

				// ??checkpoint4-3 : check the object in input
				for (TransactionInput input : currentTransaction.tranInputs) {
					tempOutput = tempUTXOs.get(input.transactionOutputId);

					if (tempOutput == null) {
						System.out.println("#Referenced input on Transaction(\" + t + \") is Missing");
						return false;
					}

					if (input.UTXO.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}

					tempUTXOs.remove(input.transactionOutputId);
				}

				// checkpoint4-4 : check the object in output
				for (TransactionOutput output : currentTransaction.tranOutputs) {
					tempUTXOs.put(output.id, output);
				}

				// ??checkpoint4-5 :
				if (currentTransaction.tranOutputs.get(0).reciepient != currentTransaction.reciepient) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}

				// ??checkpoint4-6 :
				if (currentTransaction.tranOutputs.get(1).reciepient != currentTransaction.sender) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}
			}
		}

		System.out.println("Blockchain is valid");
		return true;
	}

	public BlockChain() {
		blockchain.add(generateGenesis());
	}

	private Block generateGenesis() {

		Block genesis = new Block("0");
		// first node(genesis) needn't previousHash
		genesis.setPreviousHash(null);
		genesis.computeHash();

		return genesis;
	}

	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}

	public void displayChain() {

		for (int i = 0; i < blockchain.size(); i++) {
			System.out.println("Block: " + i);
			System.out.println("Timestamp: " + blockchain.get(i).getTimestamp());
			System.out.println("PreviousHash: " + blockchain.get(i).getPreviousHash());
			System.out.println("Hash: " + blockchain.get(i).getHash());
			System.out.println();
		}
	}

	public Block getLastestBlock() {
		return blockchain.get(blockchain.size() - 1);
	}

	public void isValid() {
		for (int i = blockchain.size() - 1; i > 0; i--) {
			if (!(blockchain.get(i).getHash().equals(blockchain.get(i).computeHash()))) {
				System.out.println("Chain is not valid!");
				return;
			}
			if (!(blockchain.get(i).getPreviousHash().equals(blockchain.get(i - 1).computeHash()))) {
				System.out.println("Chain is not valid!");
				return;
			}
		}
		System.out.println("Chain is valid!");
	}

}
