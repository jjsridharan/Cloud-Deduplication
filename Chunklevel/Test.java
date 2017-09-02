import java.io.*;

public class Test
{
	public static void main(String[] args) throws Exception
	{
			InputStream fis=new FileInputStream("aa.mp3");
			FileOutputStream fos = new FileOutputStream("aaa.mp3",true);
			byte[] b=new byte[4194304];			
			int numbytes;
			do
			{

				numbytes=fis.read(b);
		                if(numbytes>0)
				{
					String s=new String(b);
					System.out.println(numbytes);
					fos.write(s.getBytes(),0,numbytes);
				}
				
			}while(numbytes!=-1);
			fis.close();
	}
}
