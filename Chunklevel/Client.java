import java.net.Socket;  
import java.security.MessageDigest;
import java.io.*;  
import java.util.*;
import com.google.gson.Gson;
class Client
{  
	public static List<String> listofhash;
	public static CuckooHashMap<String,List<String>> list;
	Client()
	{	
		listofhash=new ArrayList<String>();
		list=new CuckooHashMap<String,List<String>>();
	}
	public static String getHash(byte[] b)
	{
		String result = "";
       		for (int i=0; i < b.length; i++) 
	  	{
           		result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16);
      		}
       		return result;
		
	}
	void getList(List<String> files)throws Exception
	{
		for(String filename :files)
		{
			byte[] b=new byte[4194304];			
			int numbytes;
			String hashvalue;
			InputStream fis=new FileInputStream(filename);
			MessageDigest mesdigest=MessageDigest.getInstance("MD5");
			do
			{				
				numbytes=fis.read(b);
		        if(numbytes>0)
				{	
					mesdigest.update(b,0,numbytes);
					hashvalue=getHash(mesdigest.digest());
					listofhash.add(hashvalue);
				}
			}while(numbytes!=-1);
			fis.close();		
		}
	}
	public static void main(String args[])throws Exception
	{  
		List<String> files=new ArrayList<String>();
		files.add("Test/aa.mp3");
		Client client=new Client();
		client.getList(files);
		list.put("put",listofhash);
		Gson gson=new Gson(); 
		String listsend=gson.toJson(list),str2="";		
		Socket s=new Socket("127.0.0.1",9999);  
		DataInputStream din=new DataInputStream(s.getInputStream());  
		DataOutputStream dout=new DataOutputStream(s.getOutputStream());  		  
		//while(!listsend.equals("stop"))
		{  
			System.out.println(listsend);
			dout.writeUTF(listsend);  
			dout.flush();  
			str2=din.readUTF();  
			System.out.println(str2);  
		}  
		dout.close();  
		s.close();  
	}
}  