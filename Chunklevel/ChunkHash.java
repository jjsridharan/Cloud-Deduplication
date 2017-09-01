import java.io.*;
import java.security.MessageDigest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.json.Gson;

public class ChunkHash
{
	CuckooHashMap<String,String> map;
	ChunkHash()
	{
		if(new File("dedupe.json").exists())
		{
			Gson gson=new Gson();
			map = gson.fromJson(reader, CuckooHashMap.class);
		}
		else
		{
			map=new CuckooHashMap<String,String>();
		}
	}
	
	public static byte[] getChecksum(String file)
	{
		try
		{
			InputStream fis=new FileInputStream(file);
			byte[] b=new byte[4194304];
			MessageDigest mesdigest=MessageDigest.getInstance("MD5");
			int numbytes;

			do
			{
				numbytes=fis.read(b);
		                if(numbytes>0)
					mesdigest.update(b,0,numbytes);
			}while(numbytes!=-1);

			fis.close();
			return mesdigest.digest();		
		}
		catch(Exception e)
		{
			System.out.println("Exception :"+e);
			return new byte[1];
		}
	}

	public static String getHash(String file)
	{
		byte[] b= getChecksum(file);
		String result = "";
       		for (int i=0; i < b.length; i++) 
	  	{
           		result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16);
      		}
       		return result;
	}
     	public void removeDedupe(String path)throws Exception
	{
		File[] files= new File(path).listFiles();
		for(File file :files)
		{
			if(file.isFile())
			{
				String hash=getHash(path+file.getName());
				System.out.println(path+file.getName());
			}
			else
			{
				if(!((file.getName()).equals("..") || (file.getName()).equals(".")))
				removeDedupe( file.getAbsolutePath()+"\\");
			}
		}
	}

	public static void main(String args[]) throws Exception
	{
		ChunkHash f=new ChunkHash();
		f.removeDedupe("Test/");	
	}
}
