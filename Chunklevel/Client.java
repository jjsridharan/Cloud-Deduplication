import java.net.Socket;  
import java.security.MessageDigest;
import java.io.*;  
import java.util.*;
import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
	public static String stripExtension (String str) 
	{
		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
	}
	void getList(List<String> files)throws Exception
	{
		for(String filename : files)
		{
			File file=new File(filename);
			int index = (file.getName()).lastIndexOf('.');
			String extension=(file.getName()).substring(index+1);
			System.out.println(file.getName());		
			String metapath=file.getParent()+"/"+stripExtension(file.getName())+".src";
			File metafile=new File(metapath);
			metafile.createNewFile();
			FileWriter fw = new FileWriter(metapath, true);
			BufferedWriter bw = new BufferedWriter(fw);
			byte[] b=new byte[4194304];			
			int numbytes;
			String hashvalue;
			InputStream fis=new FileInputStream(file.getAbsolutePath());
			MessageDigest mesdigest=MessageDigest.getInstance("MD5");
			do
			{				
				numbytes=fis.read(b);
		        if(numbytes>0)
				{	
					mesdigest.update(b,0,numbytes);
					hashvalue=getHash(mesdigest.digest());
					bw.write(hashvalue);
					listofhash.add(hashvalue);
				}
			}while(numbytes!=-1);
			fis.close();
			bw.close();
			fw.close();
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