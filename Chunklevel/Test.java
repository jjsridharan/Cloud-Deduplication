import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class Test {

  public static void main(String[] args) throws Exception 
  {
    	String server="192.168.1.101",user="sridharan",pass="student";
			int port=21;
			FTPClient ftpClient = new FTPClient();
			ftpClient.connect(server,port);
			ftpClient.enterLocalPassiveMode();
			int reply = ftpClient.getReplyCode();

			if(!FTPReply.isPositiveCompletion(reply)) 
			{
				ftpClient.disconnect();
				System.err.println("FTP server refused connection.");
				System.exit(1);
			}
			if(ftpClient.login(user,pass))
			{
				System.out.println("Successfull");
				
			}
  }
}
