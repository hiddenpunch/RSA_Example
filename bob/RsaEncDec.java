import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;

/*
1. KeyPair generation

keytool -genkeypair -rfc -keyalg rsa -keysize [keysize] -keystore [keystore file name]
        -storetype pkcs12 -storepass [keystore password] -validity 365 -alias [alias]
        -dname CN=alice
ex) keytool -genkeypair -rfc -keyalg rsa -keysize 2048 -keystore aliceKeystore.p12 -storetype pkcs12
        -storepass storepass -validity 365 -alias alice -dname CN=alice


2. Extract public key as X.509 certificate as base64 encoding

keytool -exportcert -rfc -keystore [keystore file name] -storetype pkcs12
        -storepass [keystore password] -alias [alice] -file [x.509 certificate file name]
ex) keytool -exportcert -rfc -keystore aliceKeystore.p12 -storetype pkcs12 -storepass storepass
        -alias alice -file alice.crt

*/
public class RsaEncDec {

	static File mKeyFile = null;
	static File mTargetFile = null;

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Invalid arguments.");
			printHowToUseAndExit();
		}

		if (!("enc".equals(args[0]) || "dec".equals(args[0]))) {
			System.out.println("Invalid first argument. It should be [enc|dec]");
			printHowToUseAndExit();
		}

		mKeyFile = new File(args[1]);
		if (!mKeyFile.exists()) {
			System.out.println(args[1] + " does not exist.");
			printHowToUseAndExit();
		}

		mTargetFile = new File(args[2]);
		if (!mTargetFile.exists()) {
			System.out.println(args[2] + " does not exist.");
			printHowToUseAndExit();
		}

		if ("enc".equals(args[0])) {
			if (!RsaEncrypt()) {
				printHowToUseAndExit();
			}
		}

		if ("dec".equals(args[0])) {
			if (!RsaDecrypt()) {
				printHowToUseAndExit();
			}
		}
	}

	private static void printHowToUseAndExit() {
		System.out.println("Use, java RsaEncDec [enc|dec] [pkcs12|cert] [plaintext|ciphertext]");
		System.exit(0);
	}

	private static boolean RsaDecrypt() {
        // TODO decrypt input file and write output file

		byte[] dataBytes = null;
		try{
			dataBytes = Files.readAllBytes(mTargetFile.toPath());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		PrivateKey privateKey = null;
		byte[] deciphertext = null;
		String password = "hidpun25";
		try{
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			FileInputStream is = new FileInputStream(mKeyFile);
			keyStore.load(is, password.toCharArray());
			privateKey = (PrivateKey)keyStore.getKey("alice", password.toCharArray());
			deciphertext = decryptedText(dataBytes, privateKey);
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return writeFile(deciphertext, "RsaDecryptedOutput");

	}

	private static boolean writeFile(byte[] data, String fileName) {

		try {
			File output = new File(fileName);
			FileOutputStream fos = new FileOutputStream(output);
			fos.write(data);
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static boolean RsaEncrypt() {
        // TODO encrypt input file and write output file

		// mKeyFile : pulbic key
		// mTargetFile : data
		// output : RsaEncryptedOut
		byte[] dataBytes = null;
		try{
			dataBytes = Files.readAllBytes(mTargetFile.toPath());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		PublicKey publicKey = null;
		byte[] ciphertext = null;
		try{
			CertificateFactory fact = CertificateFactory.getInstance("X.509");
			FileInputStream is = new FileInputStream(mKeyFile);
			X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
			publicKey =  cer.getPublicKey();
			ciphertext = encryptedText(dataBytes, publicKey);
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return writeFile(ciphertext, "RsaEncryptedOutput");
	}

	private static byte[] encryptedText(byte[] dataBytes, PublicKey publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(dataBytes);
	}

	private static byte[] decryptedText(byte[] dataBytes, PrivateKey privateKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(dataBytes);
	}
}
