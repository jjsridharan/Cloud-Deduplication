import java.io.*;
import java.util.zip.GZIPOutputStream;
import javax.xml.bind.DatatypeConverter;

public class Test
{
	public static void gzipIt(){

     byte[] buffer = new byte[5024000];

     try{

    	GZIPOutputStream gzos =
    		new GZIPOutputStream(new FileOutputStream("Songs.txt"));

        FileInputStream in =
            new FileInputStream("dedupe0.txt");

        int len;
        while ((len = in.read(buffer)) > 0) {
        	gzos.write(buffer, 0, len);
        }

        in.close();

    	gzos.finish();
    	gzos.close();

    	System.out.println("Done");

    }catch(IOException ex){
       ex.printStackTrace();
    }
   }

	public static void main(String[] args) throws Exception
	{
		gzipIt();
	}
}
