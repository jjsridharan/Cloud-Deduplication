import java.io.*;
import javax.xml.bind.DatatypeConverter;

public class Test
{
	public static void main(String[] args) throws Exception
	{
			/*FileInputStream fis=new FileInputStream("aa.mp3");
			FileOutputStream fos = new FileOutputStream("aaa.mp3");
			byte[] b=new byte[1024];			
			int numbytes,i=0;
			boolean f=true;
			while((numbytes = fis.read(b)) > 0)
			{
					String s=DatatypeConverter.printBase64Binary(b);
					byte[] b1= DatatypeConverter.parseBase64Binary(s);
					fos.write(b1,0,numbytes);
			}while(numbytes!=-1);
			fis.close();
			fos.close();*/
			byte[] b=new byte[1];
			b[0]=-10;
			String s=DatatypeConverter.printBase64Binary(b);
			byte b1[]=DatatypeConverter.parseBase64Binary(s);
			System.out.println(b1.length);
			System.out.println(b.length);
	}
}
