package com.wallet;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.block.BlockChain;
import com.transaction.Transaction;
import com.transaction.TransactionInput;
import com.transaction.TransactionOutput;

public class Wallet {
		public PrivateKey privateKey;
		public PublicKey publicKey;
		
		//only UTXOs owned by this wallet.
		public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>(); 
		
		public Wallet() {
			generateKeyPair();
		}
		
		public void generateKeyPair(){
			try {
				//ECDSA為橢圓曲線加密
				KeyPairGenerator keygen = KeyPairGenerator.getInstance("ECDSA" , "BC");
				//SHA1PRNG為隨機數演算法
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
				//選擇prime192v1曲線作為加密方式
				ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
				
				// Initialize the key generator and generate a KeyPair
				keygen.initialize(ecSpec, random);
				KeyPair keyPair = keygen.generateKeyPair();
				
				// Set the public and private keys from the keyPair
				privateKey = keyPair.getPrivate();
	        	publicKey = keyPair.getPublic();
	        	
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		public float getBalance() {
			float total = 0;
			
			for( Map.Entry< String , TransactionOutput > item: BlockChain.UTXOs.entrySet() ) {
				TransactionOutput UTXO = item.getValue();
				if( UTXO.isMine(publicKey) ) {	//if output belongs to me ( if coins belong to me )
					UTXOs.put(UTXO.id , UTXO);
					total += UTXO.value;
				}
			}
			return total;
		}
		
		//while need sending fund, generates and returns a new transaction from this wallet.
		public Transaction sendFunds(PublicKey _recipient , float value) {
			if(getBalance() < value) {
				System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
				return null;
			}
			
			//create array lists of inputs
			ArrayList<TransactionInput> tranInputs = new ArrayList<TransactionInput>();
			
			float total = 0;
			for( Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()) {
				TransactionOutput UTXO = item.getValue();
				total += UTXO.value;
				tranInputs.add(new TransactionInput(UTXO.id));
				if(total > value) break;
			}
			
			Transaction newTransaction = new Transaction(publicKey , _recipient , value , tranInputs);
			newTransaction.generateSignature(privateKey);
			
			for( TransactionInput input : tranInputs) {
				UTXOs.remove(input.transactionOutputId);
			}
			return newTransaction;
		}
		
}
