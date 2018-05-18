package com.wallet;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {
		public PrivateKey privateKey;
		public PublicKey publicKey;
		
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
		
}
