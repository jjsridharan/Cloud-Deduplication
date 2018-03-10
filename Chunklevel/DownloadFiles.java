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
import org.apache.commons.net.ftp.FTPFile;
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
        	//System.out.println(allBytes.length);
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
		//System.out.println("\n Shard Length : "+shards[i].length+" String Length : "+initlen);
	    shardNumber++;
		}
	
		str2=Integer.toString(initlen)+str2;
		return str2;
	}
	static FTPClient login(String server,String user,String pass)throws Exception
	{		
		FTPClient ftpClient = new FTPClient();
		ftpClient.setControlEncoding("UTF-8");
		ftpClient.connect(server,21);
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
			return ftpClient;
		}
		else
		{
			System.out.println("Login failed");
		} 				
		
		return null;
	}
	static void DownloadFilesRecursively(String folder,String dest)throws Exception
	{
		List<ListingFile> listfromserver=ListFiles.ListFilesandDirectory(folder);
		List<String> listoffiles=new ArrayList<String>();
		int numfiles=listfromserver.size()-1;
		File destname=new File(dest);
		destname.mkdirs();
		for(int j=0;j<numfiles;j++)
		{
			if((listfromserver.get(j)).isdirectory)
			{
				String subdir=folder+((listfromserver.get(j)).name).substring(((listfromserver.get(j)).name).lastIndexOf("/"));
				System.out.println(subdir);
				DownloadFilesRecursively(subdir,dest+subdir.substring(subdir.lastIndexOf('/')+1)+"/");
			}
			else
			{
				listoffiles.add(((listfromserver.get(j)).name).substring(((listfromserver.get(numfiles)).name).length()));
			}
		}
		System.out.println(dest+"hello");
		DownloadFiles("",listoffiles,dest);
	}
	static void DownloadDirectory(String folder,String fname)throws Exception
	{
		String server,user,pass,base;
		String response=Client.GetServerDetails();	
		String responsearr[]=response.split("###",0);
		server=responsearr[0];
		user=responsearr[1];
		pass=responsearr[2];
		base=responsearr[3]+folder+"/";
		DownloadFilesRecursively(folder,((System.getProperty("user.home")).replace("\\","/"))+"/Downloads/"+fname+"/");
	}
	static void DownloadFiles(String base,List<String> listoffiles,String dstlocation)throws Exception
	{
		String server,user,pass;
		//String response=Client.GetServerDetails();	
		//String responsearr[]=response.split("###",0);
		server="192.168.117.137";
		user="student";
		pass="student";
	//	if(!base.equals(""))
		//base=responsearr[3]+base+"/";
	//	else
		//base=responsearr[3];
		Upload.CheckforDirectory(server,user,pass,base);
		System.out.println("adf");
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
				FTPClient ftpClient=login(server,user,pass);
				filename=filename.replaceAll("\n","");
				base=base.replaceAll("\n","");				
		   		String downloadFile = base+filename;						
				if(filename.lastIndexOf("/")!=-1)
					filename=filename.substring(filename.lastIndexOf("/")+1);
				filename=dstlocation+filename;
				OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filename,false));
		    		boolean success = ftpClient.retrieveFile(downloadFile, outputStream);
				System.out.println("Status:" +success +". "+ downloadFile + " File Transfered sucessfully\n"+filename);
				File permit=new File(filename);
				permit.setReadable(true, false);
				permit.setExecutable(true, false);
				permit.setWritable(true, false);
		    		outputStream.close();
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
		listoffiles.add(args[1]);
		DownloadFiles("/home/student/server/"+args[0]+"/",listoffiles,args[0]+"/");
	}	
}
