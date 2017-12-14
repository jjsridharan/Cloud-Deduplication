import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import java.util.ArrayList;
import java.util.List;
public class DeleteFiles
{
	public static String stripExtension (String str) 
	{
		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
	}
    public static void DeleteFileList(List<String> files)throws Exception
	{
		String server="127.0.0.1",user="sridharan",pass="student";
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
			for(String filename : files)
			{
				filename=stripExtension(filename)+".src";
				ftpClient.deleteFile(filename);
			}
			ftpClient.logout();
		}
		else
		{
			System.out.println("Login failed");
		} 				
		
	}	
    public static void main(String[] args)throws Exception
    {
		List<String> list=new ArrayList<String>();
		list.add("/home/sridharan/Cloud-Deduplication/Chunklevel/Test/Server/aa.mp3");
		DeleteFileList(list);
    }
}
