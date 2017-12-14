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
				System.out.println(map.Current_Length);
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
					System.out.println(pair1.getLeft()+""+pair1.getRight());
					String content=RetrieveFile.decompressstring(contentbuf);
					byte[] buf=DatatypeConverter.parseBase64Binary(content);
					fos.write(buf);
					raf.close();
					System.out.println(str);
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
	static synchronized void Copynewvalues(CuckooHashMap<String,Pair<Integer,Integer>> hashoffset)throws Exception
	{
		String dedupefile="dedupe"+map.Current_Length/1000+".txt";
		FileOutputStream out = new FileOutputStream(dedupefile,true);
		Pairing<Integer,Pair<Integer,Integer>> pair;
		for(String hashval : hashoffset.keySet())
		{
			RandomAccessFile raf = new RandomAccessFile("sepdedupe.txt", "r");
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
			System.out.println(map.get(hashval).getRight().getRight());
		}
		out.close();
	}
	static void CopyExtension(CuckooHashMap<String,String> extensionlist)throws Exception
	{
		for(String filename : extensionlist.keySet())
		{
			SaveAttribute.Save(filename,extensionlist.get(filename));
		}	
	}
	static void RetrieveFiles(List<String> listoffiles)throws Exception
	{
		for(String file : listoffiles)
		{
			String path="Test/Server/"+RetrieveFile.stripExtension(file)+".src";
			getFile(path,"Test/Server/"+file);		
		}
	}
	static Runnable runnable=new Runnable()
	{
		Socket rsocket;	
		DataInputStream rdin;
		DataOutputStream rdout;
		public void run()
		{
			try
			{
				rsocket=socket;
				dedupe=new DedupeProcess(map);
				rdin=din;
				rdout=dout;
				String listrec=rdin.readUTF(); 
				System.out.println(listrec); 
				if(listrec.contains("put"))
				{
					CuckooHashMap<String,String> hashlist=gson.fromJson(listrec, new TypeToken<CuckooHashMap<String,String>>(){}.getType());	
					List<String> listofhash=gson.fromJson(hashlist.get("put"), new TypeToken<List<String>>(){}.getType());
					String result=dedupe.process(listofhash);
					System.out.println(result+"Hello");
					rdout.writeUTF(result);  
					rdout.flush();					
				}
				else if(listrec.contains("download"))
				{
					CuckooHashMap<String,String> filelist=gson.fromJson(listrec, new TypeToken<CuckooHashMap<String,String>>(){}.getType());	
					List<String> listoffiles=gson.fromJson(filelist.get("download"), new TypeToken<List<String>>(){}.getType());
					RetrieveFiles(listoffiles);
					rdout.writeUTF("success");  
					rdout.flush();
				}
				else if(listrec.contains("list"))
				{
					CuckooHashMap<String,String> list=gson.fromJson(listrec, new TypeToken<CuckooHashMap<String,String>>(){}.getType());	
					String path=list.get("list");
					String result=ListFile.ListFiles(path);
					rdout.writeUTF(result);  
					rdout.flush();
				}
				else
				{
					CuckooHashMap<String,String> hashlist=gson.fromJson(listrec, new TypeToken<CuckooHashMap<String,String>>(){}.getType());
					CuckooHashMap<String,Pair<Integer,Integer>> hashoffset=gson.fromJson(hashlist.get("process"), new TypeToken<CuckooHashMap<String,Pair<Integer,Integer>>>(){}.getType());
					CuckooHashMap<String,String> extensionlist=gson.fromJson(hashlist.get("extension"), new TypeToken<CuckooHashMap<String,String>>(){}.getType());
					CopyExtension(extensionlist);
					Copynewvalues(hashoffset);	
					rdout.writeUTF("Hello");  
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
		int ts=2;
		Thread t=null;
		while(ts!=0)
		{
			socket=ss.accept();
			din=new DataInputStream(socket.getInputStream());
			dout=new DataOutputStream(socket.getOutputStream());	
			t=new Thread(runnable);
			t.start();
			System.out.println("adf");
			ts--;
		}
		t.join();
		compress(gson.toJson(map));	
	}
}  
