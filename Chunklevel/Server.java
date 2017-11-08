import java.net.ServerSocket;
import java.net.Socket;  
import java.io.*;  
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.*;
class DedupeProcess implements Runnable
{
	private Socket socket;
	static DataInputStream din;
	static DataOutputStream dout;
	static BufferedReader br;
	static String listrec=""; 	 
	static Gson gson=new Gson();
	static CuckooHashMap<String,List<String>> list;
	DedupeProcess(Socket s)
	{
		socket=s;		
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
	public static void main(String args[])throws Exception
	{  
		
		ServerSocket ss=new ServerSocket(9999);  
		DedupeProcess dedupe;
		while(true)
		{
			Socket s=ss.accept();  
			dedupe=new DedupeProcess(s);
			Thread t=new Thread(dedupe);
			t.start();
		}  	
	}
}  
