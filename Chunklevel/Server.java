import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;	
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;   
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.bind.DatatypeConverter;

class DedupeProcess
{
	public static final int DATA_SHARDS = 20;
	public static final int PARITY_SHARDS = 4;
  	public static final int TOTAL_SHARDS = 24;
   	public static final int BYTES_IN_INT = 4;
	static String listrec=""; 	 
	static Gson gson=new Gson();
	CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>> map;	
	static List<String> result;
	
	DedupeProcess(CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>> map)
	{
		this.map=map;
		result=new ArrayList<String>();
	}
	
	public List<String> findduplicates(List<String> list)
	{	
		for(String i : list)	
		{
			if(map.get(i)==null)
				result.add(i);
		}
		return result;
	}
	
	public String process(List<String> lists)
	{		
		return gson.toJson(findduplicates(lists));
	}
}
public class Server
{  
	public static final int DATA_SHARDS = 20;
	public static final int PARITY_SHARDS = 4;
  	public static final int TOTAL_SHARDS = 24;
   	public static final int BYTES_IN_INT = 4;
	static CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>> map;
	static Integer bytecount;
	static DedupeProcess dedupe;
	static Socket socket;
	static DataInputStream din;
	static DataOutputStream dout;
	static Gson gson=new Gson();
	
	Server()
	{
		try
		{
			if(new File("dedupe.txt").exists())
			{
				decompress();
				System.gc();
				Gson gson=new Gson(); 
				Reader reader = new FileReader("dedupe.json");
				map = gson.fromJson(reader, new TypeToken<CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>>>(){}.getType());
				reader.close();
				new File("dedupe.json").delete();
				Scanner sa=new Scanner(new File("current.txt"));
				map.Current_Length=new Integer(sa.nextLine());
				bytecount=new Integer(sa.nextLine());
			}
			else
			{
				map=new CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>>();
				map.Current_Length=new Integer(0);
				bytecount=new Integer(0);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void compress(String data) throws IOException 
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
		GZIPOutputStream gzip = new GZIPOutputStream(bos);
		gzip.write(data.getBytes());
		gzip.close();
		OutputStream os= new FileOutputStream("dedupe.txt");
		bos.writeTo(os);
		bos.close();
		os.close();
		Writer wr=new FileWriter("current.txt");
		wr.write(""+ map.Current_Length+"\n"+bytecount);
		wr.close();
	}
	
	public static void decompress() throws IOException
	{
		FileInputStream fis = new FileInputStream("dedupe.txt");
            	GZIPInputStream gis = new GZIPInputStream(fis);
            	FileOutputStream fos = new FileOutputStream("dedupe.json");
            	byte[] buffer = new byte[4194304];
            	int len;
            	while((len = gis.read(buffer)) != -1)
		{
                	fos.write(buffer, 0, len);
            	}
		fos.close();
		gis.close();
	}
	
	public static void getFile(String mfile,String fileo)throws IOException
	{
		try
		{
			Reader reader=new FileReader(mfile);
			FileOutputStream fos = new FileOutputStream(fileo);
			int r=0,l=0;
			do
			{
				char[] chars = new char[48];
				r=reader.read(chars,0,48);
				if(r==48)
				{
					String str = String.valueOf(chars);
					Pairing<Integer,Pair<Integer,Integer>> pair=map.get(str);
					String position = String.valueOf(pair.getLeft());
					long pos = (long)(Double.parseDouble(position));
					String defile="dedupe" + pos/1000 + ".txt";
					RandomAccessFile raf = new RandomAccessFile(defile, "r");
					Pair<Integer,Integer> pair1=pair.getRight();
					raf.seek(pair1.getLeft());
					byte[] contentbuf=new byte[pair1.getRight()+1];
					raf.read(contentbuf,0,pair1.getRight());					
					String content=RetrieveFile.decompressstring(contentbuf);
					byte[] buf=DatatypeConverter.parseBase64Binary(content);
					fos.write(buf);
					raf.close();
				}
			}while(r!=-1);			
			fos.close();
			reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception :");
		}
		CopyAttributes.copy(mfile,fileo);
	}
	
	static synchronized void Copynewvalues(String base,CuckooHashMap<String,Pair<Integer,Integer>> hashoffset)throws Exception
	{
		String dedupefile="dedupe"+map.Current_Length/1000+".txt";
		FileOutputStream out = new FileOutputStream(dedupefile,true);
		Pairing<Integer,Pair<Integer,Integer>> pair;
		for(String hashval : hashoffset.keySet())
		{
			RandomAccessFile raf = new RandomAccessFile(base+"sepdedupe.txt", "r");
			Pair<Integer,Integer> pair1=hashoffset.get(hashval);
			raf.seek(pair1.getLeft());
			byte[] contentbuf=new byte[pair1.getRight()+1];
			raf.read(contentbuf,0,pair1.getRight());
			pair1=new Pair<Integer,Integer>(bytecount,pair1.getRight());
			pair=new Pairing<Integer,Pair<Integer,Integer>>(map.Current_Length++,pair1);						
			bytecount+=pair1.getRight()+1;
			map.put(hashval,pair);
			out.write(contentbuf);
			raf.close();
		}
		out.close();
		File f=new File(base+"sepdedupe.txt");
		f.delete();
	}
	
	static void CopyExtension(CuckooHashMap<String,String> extensionlist)throws Exception
	{
		for(String filename : extensionlist.keySet())
		{
			SaveAttribute.Save(filename,extensionlist.get(filename));
		}	
	}
	
	static void RetrieveFiles(String base,List<String> listoffiles)throws Exception
	{
		for(String file : listoffiles)
		{
			file=file.replaceAll("\n","");
			base=base.replaceAll("\n","");
			String path=base+file+".src";			
			getFile(path,base+file);		
		}
	}	
	
	static String ReedSolomon(String str2)
	{
		String receiveMessage=str2;
		final byte [] [] shards = new byte [TOTAL_SHARDS] [];
		final boolean [] shardPresent = new boolean [TOTAL_SHARDS];
		int shardSize = 0;
		int shardCount = 0;
		byte p[] = DatatypeConverter.parseBase64Binary(str2);
		int kt=(p.length-24)/24;
		int pt=0;
		int number;
		int byte_size=0;
		int j=0;
		int size=0,final_size=0;
		String temp="";
		while(receiveMessage.charAt(j)!='#')
		{
			temp=temp+receiveMessage.charAt(j);
			j++;
		}
		j=j+3;
		shardSize=Integer.parseInt(temp);
		shardSize-=1;
		int shardNumber;
		String str3;
		for(int i=0;i<24;i++)
		{
			str3="";
			shardNumber = receiveMessage.charAt(j)-'a';
			j=j+4;
			size=0;
			for(;j<receiveMessage.length()&&receiveMessage.charAt(j)!='#';j++)
			{
				str3=str3+receiveMessage.charAt(j);
				size++;
			}
			j = j+3;
			shards[i]=DatatypeConverter.parseBase64Binary(str3);
			shardPresent[i]=true;
			shardCount+=1;
			byte_size=shards[i].length;
			if(size<shardSize)
			{
				shardPresent[i]=false;
				shardCount--;
			}
		}
		if (shardCount < DATA_SHARDS) 
		{
			return "";
		}
		shardSize=byte_size;
		for (int i = 0; i < TOTAL_SHARDS; i++) 
		{
            		if (!shardPresent[i])
            		{
                	shards[i] = new byte [shardSize];
            		}
        	}			
		ReedSolomon reedSolomon = ReedSolomon.create(DATA_SHARDS, PARITY_SHARDS);
		reedSolomon.decodeMissing(shards, shardPresent, 0, shardSize);
		byte [] allBytes = new byte [shardSize * DATA_SHARDS];
		for (int i = 0; i < DATA_SHARDS; i++) 
		{
	            System.arraycopy(shards[i], 0, allBytes, shardSize * i, shardSize);
		}
		int fileSize = ByteBuffer.wrap(allBytes).getInt();
		String list_temp=new String(allBytes);
		int start=list_temp.indexOf('{');
		int end=list_temp.lastIndexOf('}');
		String listrec;
		listrec=list_temp.substring(start,end+1);
		return listrec;
	}
	
	static Runnable runnable=new Runnable()
	{
		Socket rsocket;	
		DataInputStream rdin;
		DataOutputStream rdout;
		public static final int DATA_SHARDS = 20;
	    	public static final int PARITY_SHARDS = 4;
	  	public static final int TOTAL_SHARDS = 24;
	    	public static final int BYTES_IN_INT = 4;

		public void run()
		{
			try
			{
				rsocket=socket;
				dedupe=new DedupeProcess(map);
				rdin=din;
				rdout=dout;
				String str2;
				str2=rdin.readUTF(); 				
				String listrec=ReedSolomon(str2);
				if(listrec.contains("TUP###"))
				{
					CuckooHashMap<String,String> hashlist=gson.fromJson(listrec, new TypeToken<CuckooHashMap<String,String>>(){}.getType());	
					List<String> listofhash=gson.fromJson(hashlist.get("TUP###"), new TypeToken<List<String>>(){}.getType());
					String result=dedupe.process(listofhash);
					System.out.println("Found missing hash values\n\n");
					rdout.writeUTF(result);  
					rdout.flush();					
				}
				else if(listrec.contains("DAOLWNOD###"))
				{
					CuckooHashMap<String,String> filelist=gson.fromJson(listrec, new TypeToken<CuckooHashMap<String,String>>(){}.getType());	
					List<String> listoffiles=gson.fromJson(filelist.get("DAOLWNOD###"), new TypeToken<List<String>>(){}.getType());
					String base=filelist.get("base");
					RetrieveFiles(base,listoffiles);
					System.out.println("Downloading files to client\n\n");
					rdout.writeUTF("success");  
					rdout.flush();
				}
				else if(listrec.contains("TSIL###"))
				{
					CuckooHashMap<String,String> list=gson.fromJson(listrec, new TypeToken<CuckooHashMap<String,String>>(){}.getType());	
					String path=list.get("TSIL###");
					List<ListingFile> res=ListFile.ListFilesandDirectory(path);
					String result=gson.toJson(res);
					System.out.println("List files to client\n\n");
					rdout.writeUTF(result);  
					rdout.flush();
				}
				else if(listrec.contains("SSECORP###"))
				{
					CuckooHashMap<String,String> hashlist=gson.fromJson(listrec, new TypeToken<CuckooHashMap<String,String>>(){}.getType());
					CuckooHashMap<String,Pair<Integer,Integer>> hashoffset=gson.fromJson(hashlist.get("SSECORP###"), new TypeToken<CuckooHashMap<String,Pair<Integer,Integer>>>(){}.getType());
					CuckooHashMap<String,String> extensionlist=gson.fromJson(hashlist.get("NOISNETXE###"), new TypeToken<CuckooHashMap<String,String>>(){}.getType());
					String base=hashlist.get("ESAB###");
					CopyExtension(extensionlist);
					Copynewvalues(base,hashoffset);
					System.out.println("Processed hash values for upload\n\n");	
					rdout.writeUTF("Files Received");  
					rdout.flush();
				}
				rdin.close();
				rdout.close();
				rsocket.close();			
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	};
	
	public static void main(String args[])throws Exception
	{  		
		new Server();
		ServerSocket ss=new ServerSocket(9999); 		
		int ts=1000;
		Thread t=null;
		Runtime.getRuntime().addShutdownHook(new Thread() 
		{
   			public void run()
   			{
   				try
   				{
   					compress(gson.toJson(map));
   				}
   				catch(Exception ex)
   				{
   					System.out.println(ex);
   				}
      		}
 		});
		while(ts!=0)
		{
			socket=ss.accept();
			din=new DataInputStream(socket.getInputStream());
			dout=new DataOutputStream(socket.getOutputStream());	
			t=new Thread(runnable);
			t.start();
			ts--;
		}
		t.join();
		compress(gson.toJson(map));	
	}
}  
