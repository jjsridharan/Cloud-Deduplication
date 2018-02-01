
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.net.Socket;
import java.io.*;

class Encryption {
	private static Cipher cipher = null;

	public static void main(String[] args) throws Exception {
		System.out.print(((System.getProperty("user.home")).replace("\\","/"))+"/Downloads/");
	}
}
