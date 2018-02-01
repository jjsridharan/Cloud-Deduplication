
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.net.Socket;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

class Encryption {
	private static Cipher cipher = null;

	public static void main(String[] args) throws Exception {
FTPClient ftpClient = new FTPClient();
		ftpClient.connect("192.168.1.100",21);
		ftpClient.enterLocalPassiveMode();
		int reply = ftpClient.getReplyCode();
		if(!FTPReply.isPositiveCompletion(reply)) 
		{
			ftpClient.disconnect();
			System.err.println("FTP server refused connection.");
			System.exit(1);
		}
		if(ftpClient.login("sridharan","student"))
		{
			System.out.println("login successfull");
			boolean makeDirectory = ftpClient.makeDirectory("/home/sridharan/server/sridharan995");
		}
	}
}
