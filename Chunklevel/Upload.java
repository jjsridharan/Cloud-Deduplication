import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;  
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class Upload
{  
	static List<String> fileslist,listhashes;
	static CuckooHashMap<String,String> listofhash;
	static CuckooHashMap<String,String> hashlist,offsetlist,extensionlist;
	static CuckooHashMap<String,Pair<Integer,Integer>> hashoffset; 
	static CuckooHashMap<String,Pair<String,Integer>> map;	
	static Pair<Integer,Integer> pair;
	static int bytecount;
	static long bytesuploaded=0;
	Upload()
	{	
		listofhash=new CuckooHashMap<String,String>();
		listhashes=new ArrayList<String>();
		fileslist=new ArrayList<String>();
		hashlist=new CuckooHashMap<String,String>();
		offsetlist=new CuckooHashMap<String,String>();
		extensionlist=new CuckooHashMap<String,String>();
		hashoffset=new CuckooHashMap<String,Pair<Integer,Integer>>();
		map=new CuckooHashMap<String,Pair<String,Integer>>();
		bytecount=0;
	}
	public static String getHash(byte[] b)
	{
		String result = "";
		int i;
       		for (i=0; i < b.length; i++) 
	  	{
           		result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16);
      		}
       		return result;
	}
	public static String stripExtension (String str) 
	{
		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
	}
	public static byte[] ConvertFormat(int numbytes,byte[] b)throws IOException
	{
		byte[] cb=Arrays.copyOfRange(b, 0, numbytes);
		String content=DatatypeConverter.printBase64Binary(cb);
		byte[] ba=compressstring(content);
		pair=new Pair<Integer,Integer>(bytecount,ba.length);
		bytecount+=ba.length;
		return ba;
	}
	void getList(String base,List<String> files)throws Exception
	{
		MessageDigest mesdigest;
		InputStream fis;
		FileWriter fw;
		BufferedWriter bw;
		String hashvalue;
		int index,numbytes,count;
		byte[] b=new byte[4194304];
		Pair<String,Integer> identity;	
		for(String filename : files)
		{
			File file=new File(filename);
			index = (file.getName()).lastIndexOf('.');
			String extension=(file.getName()).substring(index+1);
			System.out.println("Getting Hash Values for "+file.getName());		
			String metapath=file.getParent()+"/"+stripExtension(file.getName())+".src";
			File metafile=new File(metapath);
			metafile.createNewFile();
			fw = new FileWriter(metapath);
			bw = new BufferedWriter(fw);					
			fis=new FileInputStream(file.getAbsolutePath());
			mesdigest=MessageDigest.getInstance("MD5");
			count=0;
			do
			{				
				numbytes=fis.read(b);
				if(numbytes>0)
				{	
					mesdigest.update(b,0,numbytes);
					hashvalue=getHash(mesdigest.digest());
					bw.write(hashvalue);
					if(listofhash.get(hashvalue)==null)
					{
					listofhash.put(hashvalue,"true");
					listhashes.add(hashvalue);
					}
					identity=new Pair<String,Integer>(filename,new Integer(count++));
					map.put(hashvalue,identity);
				}
			}while(numbytes!=-1);
			fis.close();
			bw.close();
			fw.close();
			metapath=base+"/"+stripExtension(file.getName())+".src";
			extensionlist.put(metapath,extension);
			fileslist.add(metapath);
		}
	}
	public void SeparateDedup(List<String> dedup)throws Exception
	{
		FileOutputStream out = new FileOutputStream("sepdedupe.txt");	
		Pair<String,Integer> identity;
		RandomAccessFile raf;		
		int numbytes;
		System.out.println("Seperating missing hash values...");
		for(String iterate : dedup)
		{
			identity=map.get(iterate);
			byte[] b=new byte[4194304];
			raf = new RandomAccessFile(identity.getLeft(), "r");
			raf.seek((identity.getRight()*4194304));
			numbytes=raf.read(b);
			out.write(ConvertFormat(numbytes,b));
			hashoffset.put(iterate,pair);
			raf.close();	
		}
		out.close();
	}
	public static byte[] compressstring(String str) throws IOException 
	{
		if ((str == null) || (str.length() == 0)) 
		{
			  return null;
		}
                ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(str.getBytes("UTF-8"));
		gzip.flush();
		gzip.close();
		return obj.toByteArray();
	}
	static void UploadFileList(String server,String user,String pass,String base,List<String> files)
	{
		try
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
					System.out.println("Successfull");
					for(String i : files)
					{
						System.out.println(i+"success");
						File file=new File(i);
						InputStream in = new FileInputStream(file.getParent()+"//"+stripExtension(file.getName())+".src");
						ftpClient.setFileType(FTP.BINARY_FILE_TYPE);		
		        			ftpClient.enterLocalPassiveMode();
		        			boolean Store = ftpClient.storeFile(base+stripExtension(file.getName())+".src", in);
						System.out.println(ftpClient.getReplyString());
						file=new File(file.getParent()+"/"+stripExtension(file.getName())+".src");
						System.out.println(file.getParent()+"/"+stripExtension(file.getName())+".src");
						bytesuploaded+=file.length();
						in.close();
						file.delete();
					}
					InputStream in = new FileInputStream("sepdedupe.txt");
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);		
		        		ftpClient.enterLocalPassiveMode();
					boolean Store = ftpClient.storeFile(base+"sepdedupe.txt", in);
					File f=new File("sepdedupe.txt");
					bytesuploaded+=f.length();
					f.delete();
				}
				else
				{
					System.out.println("Login failed");
				} 
				
				ftpClient.logout();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	public static List<String> addfiles(String path)throws Exception
	{
		List<String> filelist=new ArrayList<String>();
		File[] files= new File(path).listFiles();
		for(File file :files)
		{
			filelist.add(file.getAbsolutePath());
			System.out.println(file.getAbsolutePath());
		}
		return filelist;
	}
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
	public static void CheckforDirectory(String server,String user,String pass,String base)throws Exception
	{
		FTPClient ftpClient = new FTPClient();
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
			boolean directoryExists = ftpClient.changeWorkingDirectory(base);
			if(directoryExists)	return ;
			CheckforDirectory(server,user,pass,base.substring(0,base.lastIndexOf("/")));
			boolean makeDirectory = ftpClient.makeDirectory(base);
			System.out.println(base);
		}
		ftpClient.logout();
	}
	public static void UploadDirectory(String base,String directory)throws Exception
	{
		List<String> listoffiles=new ArrayList<String>();
		File folder=new File(directory);
		File[] listOfFiles= folder.listFiles();
		int numfiles=listOfFiles.length;
		for(int j=0;j<numfiles;j++)
		{
			if(listOfFiles[j].isDirectory())
			{
				if((listOfFiles[j].getName()).charAt(0)!='.')
				UploadDirectory(base+"/"+directory.substring(directory.lastIndexOf("/")),listOfFiles[j].getAbsolutePath());
			}
			else
			{
				if((listOfFiles[j].getName()).charAt(0)!='.')
				listoffiles.add(listOfFiles[j].getAbsolutePath());
			}
		}
		UploadFiles(base+directory.substring(directory.lastIndexOf("/")),listoffiles);
	}
	public static void UploadFiles(String base,List<String> files)
	{		
		try
		{
			String ip,user,pass;
			//String response=Client.GetServerDetails();	
			//String responsearr[]=response.split("###",0);
			ip="192.168.117.137";//responsearr[0];
			user="student";//responsearr[1];
			pass="student";//responsearr[2];
			base="/home/student/server/sridharan995/";//responsearr[3]+base+"/";
			System.out.println("Inside function");
			CheckforDirectory(ip,user,pass,base);
			System.out.println("Inside function");		
			Upload client=new Upload();
			client.getList(base,files);
			Gson gson=new Gson();
			hashlist.put("put",gson.toJson(listhashes));	
			String listsend=gson.toJson(hashlist),duped="";		
			Socket s=new Socket(ip,9999);  
			DataInputStream din=new DataInputStream(s.getInputStream());  
			DataOutputStream dout=new DataOutputStream(s.getOutputStream());  		  
			bytesuploaded+=listsend.length();
			listsend=ReedSolomonFunc(listsend);
			dout.writeUTF(listsend);
			 
			dout.flush();
			duped=din.readUTF();
			List<String> duplicated=gson.fromJson(duped, new TypeToken<List<String>>(){}.getType());
			client.SeparateDedup(duplicated);
			listsend=gson.toJson(hashoffset);
			offsetlist.put("process",listsend);
			listsend=gson.toJson(extensionlist);
			offsetlist.put("extension",listsend);
			offsetlist.put("base",base);
			s=new Socket(ip,9999);  
			din=new DataInputStream(s.getInputStream());  
			dout=new DataOutputStream(s.getOutputStream()); 
			UploadFileList(ip,user,pass,base,files);
			bytesuploaded+=listsend.length();
			listsend=gson.toJson(offsetlist);
			listsend=ReedSolomonFunc(listsend);
			dout.writeUTF(listsend);
			dout.flush();
			duped=din.readUTF();
			dout.close(); 
			s.close();	
			System.out.println("Files Uploaded Successfully");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void main(String args[])throws Exception
	{  
		System.out.println(args[0]);
		List<String> files=addfiles(args[0]);
		UploadFiles("sridharan995",files);
		System.out.println("Number of Bytes Uploaded :"+bytesuploaded);
	}
}  
