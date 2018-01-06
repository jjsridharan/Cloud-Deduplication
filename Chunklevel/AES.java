
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class AES
{
	private static Cipher cipher = null;

	public static void main(String[] args) throws Exception 
	{		
		//System.out.println("Key="+stringKey);
		/*String plainText = "Java Cryptography Extension";
		System.out.println("Plain Text Before Encryption: " + plainText);
		byte[] plainTextByte = plainText.getBytes("UTF8");
		//byte[] encryptedBytes = encrypt(plainTextByte, secretKey);
		//System.out.println(secretKey);
		String encryptedText = new String(encryptedBytes, "UTF8");
		System.out.println("Encrypted Text After Encryption: " + encryptedText);
		byte[] decryptedBytes = decrypt(encryptedBytes, secretKey);
		String decryptedText = new String(decryptedBytes, "UTF8");
		System.out.println("Decrypted Text After Decryption: " + decryptedText);*/
	}

	static byte[] encrypt(byte[] plainTextByte)throws Exception 
	{
		cipher = Cipher.getInstance("AES");
		String stringKey = "VP5HgVCw6Sm77ukqv7JwiQ==";
		byte[] buf=DatatypeConverter.parseBase64Binary(stringKey);
		SecretKey secretKey =secretKey=new SecretKeySpec(buf, 0, buf.length, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedBytes = cipher.doFinal(plainTextByte);
		return encryptedBytes;
	}

	static byte[] decrypt(byte[] encryptedBytes, SecretKey secretKey)throws Exception 
	{
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		return decryptedBytes;
	}
}