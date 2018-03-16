import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.OutputStream;
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
	static CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>> hashoffset; 
	static CuckooHashMap<String,Pair<String,Integer>> map;	
	static Pair<Integer,Integer> pair;
	static Pairing<Integer,Pair<Integer,Integer>> pair1;
	static int bytecount,sepcount;
	static long bytesuploaded=0;
	Upload()
	{	
		listofhash=new CuckooHashMap<String,String>();
		listhashes=new ArrayList<String>();
		fileslist=new ArrayList<String>();
		hashlist=new CuckooHashMap<String,String>();
		offsetlist=new CuckooHashMap<String,String>();
		extensionlist=new CuckooHashMap<String,String>();
		hashoffset=new CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>>();
		map=new CuckooHashMap<String,Pair<String,Integer>>();
		bytecount=0;
		sepcount=0;
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
		pair1= new Pairing<Integer,Pair<Integer,Integer>>(sepcount,pair);
		bytecount+=ba.length;
		if(bytecount>=2107483647)
		{
			bytecount=0;
			sepcount++;
		}
		return ba;
	}
	void getList(String base,List<String> files)throws Exception
	{
		System.out.println("Step 2 : Getting Hash values for individual chunk\n");
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
			System.out.println("Getting Hash Values for "+file.getName()+"\n");		
			String metapath=file.getParent()+"/"+file.getName()+".src";
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
			metapath=base+"/"+file.getName()+".src";
			extensionlist.put(metapath,extension);
			fileslist.add(metapath);
		}
	}
	public void SeparateDedup(List<String> dedup)throws Exception
	{
		int sepfile=0;
		FileOutputStream out = new FileOutputStream("sepdedupe"+sepcount+".txt");	
		Pair<String,Integer> identity;
		FileInputStream raf;		
		int numbytes;
		System.out.println("Step 5: Seperating missing hash values...\n");
		for(String iterate : dedup)
		{
			identity=map.get(iterate);
			byte[] b=new byte[4194304];
			raf = new FileInputStream(identity.getLeft());
			int pos=identity.getRight();
			while(pos!=0)
			{
				if(pos>=500)
				{
					raf.skip(500*4194304);
					pos-=500;
				}
				else
				{
					raf.skip(pos*4194304);
					pos=0;
				}
			}
			numbytes=raf.read(b);
			out.write(ConvertFormat(numbytes,b));
			hashoffset.put(iterate,pair1);
			raf.close();
			if(sepfile!=sepcount)
			{
				out.close();
				out = new FileOutputStream("sepdedupe"+sepcount+".txt");	
				sepfile=sepcount;
			}				
		}
		out.close();
		System.out.println("Separated Missing Hash values");
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
				System.out.println("Step 6 : Uploading files to server\n");		
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
					for(String i : files)
					{
						File file=new File(i);
						InputStream in = new FileInputStream(file.getParent()+"/"+file.getName()+".src");
						ftpClient.setFileType(FTP.BINARY_FILE_TYPE);		
		        			ftpClient.enterLocalPassiveMode();
		        			boolean Store = ftpClient.storeFile(base+file.getName()+".src", in);
						System.out.println(ftpClient.getReplyString());
						file=new File(file.getParent()+"/"+file.getName()+".src");
						bytesuploaded+=file.length();
						in.close();
						file.delete();
					}
					for(int i=0;i<=sepcount;i++)
					{
						InputStream in = new FileInputStream("sepdedupe"+i+".txt");
						ftpClient.setFileType(FTP.BINARY_FILE_TYPE);		
		        			ftpClient.enterLocalPassiveMode();
						boolean Store = ftpClient.storeFile(base+"sepdedupe"+i+".txt", in);
						File f=new File("sepdedupe"+i+".txt");
						bytesuploaded+=f.length();
						f.delete();
					}
				}
				else
				{
					System.out.println("Login failed");
				} 
				
				ftpClient.logout();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void UploadCommands(String server,String user,String pass,String base,String fname)
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
					InputStream in = new FileInputStream(fname);
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);		
		        		ftpClient.enterLocalPassiveMode();
		        		boolean Store = ftpClient.storeFile(base+fname,in);
		        		System.out.println(ftpClient.getReplyString());											
					File f=new File(fname);					
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
			e.printStackTrace();
		}
	}
	public static void DownloadCommands(String server,String user,String pass,String base,String fname)
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
					OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fname,false));
		    			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);		
		        		ftpClient.enterLocalPassiveMode();
		        		boolean success = ftpClient.retrieveFile(base+fname, outputStream);				
		        		System.out.println(ftpClient.getReplyString());											
					outputStream.close();
					ftpClient.logout();
				}
				else
				{
					System.out.println("Login failed");
				} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static List<String> addfiles(String path)throws Exception
	{
		List<String> filelist=new ArrayList<String>();
		File[] files= new File(path).listFiles();
		for(File file :files)
		{
			if(!file.isDirectory())
				filelist.add(file.getAbsolutePath());
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
		final byte [] temp = listsend.getBytes();
		ByteBuffer.wrap(allBytes).putInt(fileSize);		
		for(int i=4;i<temp.length+4;i++)
			allBytes[i]=temp[i-4];		
		byte [] [] shards = new byte [TOTAL_SHARDS] [shardSize];
		for (int i = 0; i < DATA_SHARDS; i++) 
		{
            		System.arraycopy(allBytes, i * shardSize, shards[i], 0, shardSize);
        	}
		ReedSolomon reedSolomon = ReedSolomon.create(DATA_SHARDS, PARITY_SHARDS);
        	reedSolomon.encodeParity(shards, 0, shardSize);		
		String str2="";
		char shardNumber='a';
		int initlen=0;
		for (int i = 0; i < TOTAL_SHARDS; i++)
		{
			str2=str2+"###"+shardNumber+"###"+DatatypeConverter.printBase64Binary(shards[i]);
			if(initlen==0)
				initlen=str2.length()-6;
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
	
	public static String GetCommandsRoot(String base)
	{
		if(base.indexOf("/")==-1) return base;
		return base.substring(0,base.indexOf("/"));
		
	}
	public static void UploadFiles(String base,List<String> files)
	{		
		try
		{
			String ip,user,pass,log,commands,duped="";
			System.out.println("\n\n\nStep 1: Waiting for connection with server...\n");			
			commands=GetCommandsRoot(base)+"/.commands/";
			String response=Client.GetServerDetails();	
			String responsearr[]=response.split("###",0);
			ip=responsearr[0];
			user=responsearr[1];
			pass=responsearr[2];
			base=responsearr[3]+base+"/";
			System.out.println("Connection established with server\n");						
			CheckforDirectory(ip,user,pass,base);
			Upload client=new Upload();
			client.getList(base,files);
			Gson gson=new Gson();
			duped=gson.toJson(listhashes);
			FileWriter writer = new FileWriter("put.json");
   			writer.write(duped);
   			writer.close();
			CheckforDirectory(ip,user,pass,responsearr[3]+commands);
			UploadCommands(ip,user,pass,responsearr[3]+commands,"put.json");
			hashlist.put("TUP###",responsearr[3]+commands);	
			String listsend=gson.toJson(hashlist);		
			Socket s=new Socket(ip,9999);  
			DataInputStream din=new DataInputStream(s.getInputStream());  
			DataOutputStream dout=new DataOutputStream(s.getOutputStream());  		  
			bytesuploaded+=listsend.length();
			listsend=ReedSolomonFunc(listsend);
			dout.writeUTF(listsend);			 
			dout.flush();
			System.out.println("Step 3: Uploading hash values to server...\n");
			duped=din.readUTF();
			System.out.println("Step 4: Received missing hash values from server...\n");			
			DownloadCommands(ip,user,pass,responsearr[3]+commands,"missing.json");
			FileReader reader = new FileReader("missing.json");					
			List<String> duplicated=gson.fromJson(reader, new TypeToken<List<String>>(){}.getType());
			reader.close();
			new File("missing.json");
			client.SeparateDedup(duplicated);
			duped=gson.toJson(hashoffset);
			writer = new FileWriter("process.json");
   			writer.write(duped);
   			writer.close();
			CheckforDirectory(ip,user,pass,responsearr[3]+commands);
			UploadCommands(ip,user,pass,responsearr[3]+commands,"process.json");
			offsetlist.put("SSECORP###",responsearr[3]+commands);
			listsend=gson.toJson(extensionlist);
			offsetlist.put("NOISNETXE###",listsend);
			offsetlist.put("ESAB###",base);
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
			System.out.println("Files Uploaded Successfully\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void main(String args[])throws Exception
	{  
		List<String> files=addfiles(args[0]);
		UploadFiles(args[0],files);
		String log="Uploading "+files.size()+" files to "+args[2];
		System.out.println(Client.LogActivity(args[1],log));
					
	}
}  
