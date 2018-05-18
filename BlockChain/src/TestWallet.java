import java.security.Security;

import com.transaction.StringUtil;
import com.transaction.Transaction;
import com.wallet.Wallet;

public class TestWallet {
	
	public static void main(String[] args) {
		// Setup Bouncey castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// Create the new wallets
		Wallet walletA = new Wallet();
		Wallet walletB = new Wallet();

		// Test public and private keys
		System.out.println("Private and public keys:");
		System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
		System.out.println(StringUtil.getStringFromKey(walletA.publicKey));

		// Create a test transaction from WalletA to walletB
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		transaction.generateSignature(walletA.privateKey);

		// Verify the signature works and verify it from the public key
		System.out.println("Is signature verified");
		System.out.println(transaction.verifiySignature());
	}
	
}
