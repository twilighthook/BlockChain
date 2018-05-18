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
				//ECDSA����ꦱ�u�[�K
				KeyPairGenerator keygen = KeyPairGenerator.getInstance("ECDSA" , "BC");
				//SHA1PRNG���H���ƺt��k
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
				//���prime192v1���u�@���[�K�覡
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
