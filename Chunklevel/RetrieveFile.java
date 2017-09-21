import java.io.*;
import java.security.MessageDigest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.gson.Gson;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.bind.DatatypeConverter;

public class RetrieveFile
{
	static CuckooHashMap<String,Integer> map;
	RetrieveFile()
	{
		try
		{
			if(new File("dedupe.txt").exists())
			{
				decompress();
				Gson gson=new Gson();
				Reader reader = new FileReader("dedupe.json");
				map = gson.fromJson(reader, CuckooHashMap.class);
				new File("dedupe.json").delete();
			}
			else
			{
				map=new CuckooHashMap<String,Integer>();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static String stripExtension (String str) 
	{
		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
    	}
	public static int getNumberBytes(String index)
	{
		int length=index.length();
		boolean flag=false;
		StringBuilder ans=new StringBuilder("");
		for(int i=0;i<length;i++)
		{
			if(index.charAt(i)=='0' && flag==false)
			{
				continue;
			}
			flag=true;
			ans.append(index.charAt(i));
		}
		return Integer.parseInt(ans.toString());
	}
	public static void getFile(String mfile,String file)
	{
		try
		{
			Reader reader=new FileReader(mfile);
			FileOutputStream fos = new FileOutputStream(file,true);
			int r=0,l=0;
			do
			{
				char[] chars = new char[48];
				r=reader.read(chars,0,48);
				if(r==48)
				{
					String str = String.valueOf(chars);
					String position = String.valueOf(map.get(str));
					long pos = (long)(Double.parseDouble(position));
					String defile="dedupe" + pos/10000 + ".txt";
					RandomAccessFile raf = new RandomAccessFile(defile, "r");
					System.out.println(((pos%10000))*6000);
					raf.seek(((pos%10000))*5592425);
					byte contentbuf[]=new byte[5592426];
					raf.read(contentbuf,0,5592425);
					String content=new String(contentbuf);
					String index=content.substring(0,7);
					int numbytes=getNumberBytes(index);
					content=content.substring(7);
					index=content.substring(0,10);
					int contentlen=getNumberBytes(index);
					String value=content.substring(10);
					value=value.substring(0,contentlen);
					byte[] buf=DatatypeConverter.parseBase64Binary(value);
					fos.write(buf,0,numbytes);
					raf.close();
					System.out.println(str);
				}
			}while(r!=-1);			
			fos.close();
			reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception :");
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
	public static void main(String args[]) throws Exception
	{
		RetrieveFile f=new RetrieveFile();
		String path=new String("/home/sridharan/Cloud-Deduplication/Chunklevel/Test/a.mp3");
		String opath=stripExtension(path)+".src";	
		getFile(opath,path);
	}
}
