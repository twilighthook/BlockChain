package com.transaction;

import java.security.*;
import java.util.ArrayList;

import com.block.BlockChain;

public class Transaction {

	public String transactionId; // hash of transaction
	public PublicKey sender;
	public PublicKey reciepient;
	public float value;
	public byte[] signature;

	public ArrayList<TransactionInput> tranInputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> tranOutputs = new ArrayList<TransactionOutput>();

	public static int sequence = 1; // count how many transaction generate

	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.tranInputs = inputs;
	}

	private String calculateHash() {
		sequence++;
		return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value) + sequence);
	}

	// Signs all the data we don't wish to be tampered with.
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value);
		signature = StringUtil.applyECDSASig(privateKey, data);
	}

	// Verifies the data we signed hasn't been tampered with
	public boolean verifiySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}

	public boolean processTransaction() {
		
		if(verifiySignature() == false) {
			System.out.println("## Transaction Signature failed to vertify");
			return false;
		}
		
		//gather transaction inputs (Make sure they are unspent):
		for(TransactionInput i : tranInputs) {
			i.UTXO = BlockChain.UTXOs.get(i.transactionOutputId);
		}
		
		//check if transaction is valid:
		if(getInputsValue() < BlockChain.minimumTransaction) {
			System.out.println("##Transaction Inputs to small: " + getInputsValue());
			return false;
		}
		
		//generate transaction output
		float leftOver = getInputsValue() - value;
		transactionId = calculateHash();
		tranOutputs.add(new TransactionOutput ( this.reciepient , value , transactionId ) );
		tranOutputs.add(new TransactionOutput ( this.sender , leftOver , transactionId ) );
		
		//add output to unspent list
		for(TransactionOutput o : tranOutputs) {
			BlockChain.UTXOs.put(o.id, o);
		}
		
		//remove transaction input from UTXO list as  spent
		for( TransactionInput i : tranInputs ) {
			if( i.UTXO == null ) continue;
			BlockChain.UTXOs.remove( i.UTXO.id );
		}
		
		return true;
	}

	// returns sum of inputs(UTXOs) values
	public float getInputsValue() {
		float total = 0;
		for (TransactionInput i : tranInputs) {
			if (i.UTXO == null)
				continue; // if Transaction can't be found skip it
			total += i.UTXO.value;
		}
		return total;
	}
	
	public float getOutputsValue() {
		float total = 0;
		for(TransactionOutput o : tranOutputs) {
			total += o.value;
		}
		return total;
	}

}
