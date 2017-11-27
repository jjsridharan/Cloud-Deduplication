import java.net.ServerSocket;
import java.net.Socket;  
import java.io.*;  
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
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
		List<String> duplicated=findduplicates(lists);
		return gson.toJson(duplicated);
	}
}
public class Server
{  
	static CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>> map;
	static Integer bytecount;
	static DedupeProcess dedupe;
	static CuckooHashMap<String,Pair<Integer,Integer>> hashoffset;
	static Socket socket;
	static DataInputStream din;
	static DataOutputStream dout;
	static CuckooHashMap<String,String> hashlist;
	static List<String> listofhash;
	static Gson gson=new Gson();
	Server()
	{
		try
		{
			if(new File("dedupe.txt").exists())
			{
				decompress();
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
	static void Copynewvalues()throws Exception
	{
		String dedupefile="dedupe"+map.Current_Length/1000+".txt";
		FileOutputStream out = new FileOutputStream(dedupefile,true);
		Pairing<Integer,Pair<Integer,Integer>> pair;
		for(String e : hashoffset.keySet())
		{
			RandomAccessFile raf = new RandomAccessFile("Test/Server/sepdedupe.txt", "r");
			Pair<Integer,Integer> pair1=hashoffset.get(e);
			raf.seek(pair1.getLeft());
			byte[] contentbuf=new byte[pair1.getRight()+1];
			raf.read(contentbuf,0,pair1.getRight());
			pair1=new Pair<Integer,Integer>(bytecount,pair1.getRight());
			pair=new Pairing<Integer,Pair<Integer,Integer>>(map.Current_Length++,pair1);						
			bytecount+=pair1.getRight()+1;
			map.put(e,pair);
			out.write(contentbuf);
			raf.close();
			System.out.println(map.get(e).getRight().getRight());
		}
		out.close();
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
					hashlist=gson.fromJson(listrec, new TypeToken<CuckooHashMap<String,String>>(){}.getType());	
					listofhash=gson.fromJson(hashlist.get("put"), new TypeToken<List<String>>(){}.getType());
					String result=dedupe.process(listofhash);
					System.out.println(result+"Hello");
					rdout.writeUTF(result);  
					rdout.flush();					
				}
				else
				{
					hashlist=gson.fromJson(listrec, new TypeToken<CuckooHashMap<String,String>>(){}.getType());
					hashoffset=gson.fromJson(hashlist.get("process"), new TypeToken<CuckooHashMap<String,Pair<Integer,Integer>>>(){}.getType());
					Copynewvalues();	
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
