import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import javax.xml.bind.DatatypeConverter;

public class DownloadFiles
{
	public static String ReedSolomonFunc(String listsend)
	{
		
		int DATA_SHARDS = 20;
		int PARITY_SHARDS = 4;
		int TOTAL_SHARDS = 24;

		int BYTES_IN_INT = 4;
		int fileSize=listsend.length()+1;
		int shardSize;
		final int storedSize = fileSize + BYTES_IN_INT;
		shardSize = (storedSize + DATA_SHARDS - 1) / DATA_SHARDS;
  		final int bufferSize = shardSize * DATA_SHARDS;
        	final byte [] allBytes = new byte[bufferSize];
        	System.out.println(allBytes.length);
		final byte [] temp = listsend.getBytes();
		ByteBuffer.wrap(allBytes).putInt(fileSize);
		
		for(int i=4;i<temp.length+4;i++)
			allBytes[i]=temp[i-4];
		
		byte [] [] shards = new byte [TOTAL_SHARDS] [shardSize];
		for (int i = 0; i < DATA_SHARDS; i++) {
            		System.arraycopy(allBytes, i * shardSize, shards[i], 0, shardSize);
        	}

		//Encoder called
		ReedSolomon reedSolomon = ReedSolomon.create(DATA_SHARDS, PARITY_SHARDS);
        	reedSolomon.encodeParity(shards, 0, shardSize);
		//Encoded
		
		String str2="";
		char shardNumber='a';
		int initlen=0;
		for (int i = 0; i < TOTAL_SHARDS; i++) {
		str2=str2+"###"+shardNumber+"###"+DatatypeConverter.printBase64Binary(shards[i]);
		if(initlen==0)
			initlen=str2.length()-6;
		System.out.println("\n Shard Length : "+shards[i].length+" String Length : "+initlen);
	    shardNumber++;
		}
	
		str2=Integer.toString(initlen)+str2;
		return str2;
	}
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
		listsend=ReedSolomonFunc(listsend);
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
		DownloadFiles("192.168.117.88","student","student","/home/student/Server/User1/",listoffiles);
	}	
}
