import java.io.*;
import java.security.MessageDigest;
//import com.google.gson.Gson;
public class Test
{
	
	public static void main(String[] args) throws Exception
	{
		InputStream fis=new FileInputStream("test.txt");
		byte[] b=new byte[4194304];
			
		int numbytes;
		
			do
			{
				MessageDigest mesdigest=MessageDigest.getInstance("MD5");
				numbytes=fis.read(b);
		                if(numbytes>0)
					mesdigest.update(b,0,numbytes);
				System.out.println(new String(b));
			}while(numbytes!=-1);
			fis.close();
	}
}
