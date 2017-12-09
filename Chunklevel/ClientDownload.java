import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;


public class ClientDownload
{
	static List<String> listoffiles;
	static CuckooHashMap<String,String> hashlist;
	static void DownloadFiles()throws Exception
	{
		for(String filename : listoffiles)
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
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
					ftpClient.enterLocalPassiveMode();           				
           				String downloadFile = "/home/sridharan/Cloud-Deduplication/Chunklevel/Test/Server/"+filename;
						System.out.println(downloadFile);
            				 OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filename));
            				boolean success = ftpClient.retrieveFile(downloadFile, outputStream);
					File permit=new File(filename);
					permit.setReadable(true, false);
					permit.setExecutable(true, false);
					permit.setWritable(true, false);
            				outputStream.close();
				}
				else
				{
					System.out.println("Login failed");
				} 
				ftpClient.logout();
		}
	}
	public static void main(String args[])throws Exception
	{
		Socket s=new Socket("192.168.1.101",9999);  
		DataInputStream din=new DataInputStream(s.getInputStream());  
		DataOutputStream dout=new DataOutputStream(s.getOutputStream());
		listoffiles=new ArrayList<String>();
		listoffiles.add("bb.mp3");
		Gson gson=new Gson();
		hashlist=new CuckooHashMap<String,String>();		
		hashlist.put("download",gson.toJson(listoffiles));		
		String listsend=gson.toJson(hashlist),result;
		dout.writeUTF(listsend);  
		dout.flush();
		result=din.readUTF();
		if(result.contains("success"))
		{
			DownloadFiles();
		}
		else
		{
			System.out.println("Failed to download");
		}
	}	
}
