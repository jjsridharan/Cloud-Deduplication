import java.net.ServerSocket;
import java.net.Socket;  
import java.io.*;  
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
class DedupeProcess implements Runnable
{
	private Socket socket;
	static DataInputStream din;
	static DataOutputStream dout;
	static BufferedReader br;
	static String listrec=""; 	 
	static Gson gson=new Gson();
	static CuckooHashMap<String,List<String>> list;
	static CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>> map;
	DedupeProcess(Socket s,CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>> map)
	{
		socket=s;		
		this.map=map;
	}
	public List<String> findduplicates(List<String> list)
	{	
		
	}
	public void run()
	{
		
		try
		{
			din=new DataInputStream(socket.getInputStream());
			dout=new DataOutputStream(socket.getOutputStream());  
			br=new BufferedReader(new InputStreamReader(System.in));
			listrec=din.readUTF();  
			list=gson.fromJson(listrec, new TypeToken<CuckooHashMap<String,List<String>>>(){}.getType());		
			System.out.println(listrec);
			List<String> duplicated=findduplicates(list("put"));
			dout.writeUTF(listrec);
			dout.flush();
			din.close();
			dout.close();
			socket.close(); 
		}
		catch(Exception e)
		{
			
		}
	}
}
public class Server extends Thread
{  
	CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>> map;
	static Integer bytecount;
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
	public static void main(String args[])throws Exception
	{  		
		new Server();
		ServerSocket ss=new ServerSocket(9999);  
		DedupeProcess dedupe;
		while(true)
		{
			Socket s=ss.accept();  
			dedupe=new DedupeProcess(s);
			Thread t=new Thread(dedupe,map);
			t.start();
		}  	
	}
}  
