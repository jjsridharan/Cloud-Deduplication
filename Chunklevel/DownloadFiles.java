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


public class DownloadFiles
{
	
	static void DownloadFiles(String server,String user,String pass,String base,List<String> listoffiles)throws Exception
	{
		Socket s=new Socket(server,9999);  
		DataInputStream din=new DataInputStream(s.getInputStream());  
		DataOutputStream dout=new DataOutputStream(s.getOutputStream());		
		Gson gson=new Gson();
		CuckooHashMap<String,String> hashlist=new CuckooHashMap<String,String>();		
		hashlist.put("download",gson.toJson(listoffiles));
		hashlist.put("base",base);
		String listsend=gson.toJson(hashlist),result;
		dout.writeUTF(listsend);  
		dout.flush();
		result=din.readUTF();
		if(result.contains("success"))
		{
			for(String filename : listoffiles)
			{
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
		   				String downloadFile = base+filename;
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
		else
		{
			System.out.println("Failed to Download");
		}
	}
	public static void main(String args[])throws Exception
	{
		List<String> listoffiles;
		listoffiles=new ArrayList<String>();
		listoffiles.add("bb.mp3");
		DownloadFiles("192.168.43.52","tamizh","senthamizhan123","/home/tamizh/Server/User1/",listoffiles);
	}	
}
