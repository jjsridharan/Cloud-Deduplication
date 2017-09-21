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

public class ChunkHash
{
	static CuckooHashMap<String,Integer> map;
	ChunkHash()
	{
		try
		{
			if(new File("dedupe.txt").exists())
			{
				decompress();
				Gson gson=new Gson();
				Reader reader = new FileReader("dedupe.json");
				map = gson.fromJson(reader, CuckooHashMap.class);
				reader.close();
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
	public static String FormString(int numbytes,int length)
	{
		int len=0,cp=numbytes;
		while(cp!=0)
		{
			len++;
			cp/=10;
		}
		StringBuilder result=new StringBuilder();
		for(int i=0;i<length-len;i++)
		{
			result.append("0");
		}
		return result.toString();
	}
	public static String ConvertFormat(int numbytes,byte[] b)
	{
		String content=DatatypeConverter.printBase64Binary(b);
		String res=FormString(numbytes,7)+String.valueOf(numbytes)+FormString(content.length(),10)+String.valueOf(content.length())+content;
		System.out.println(res.length());
		return res;
	}
	public static void getChecksum(String file,String metapath)
	{
		try
		{
			File metafile=new File(metapath);
			metafile.createNewFile();
			InputStream fis=new FileInputStream(file);
			byte[] b=new byte[4194304];			
			int numbytes;
			String hashvalue;
			FileWriter fw = new FileWriter(metapath, true);
			BufferedWriter bw = new BufferedWriter(fw);
			String dedupefile="dedupe"+map.Current_Length/10000+".txt";
			FileWriter defw = new FileWriter(dedupefile, true);
			BufferedWriter debw = new BufferedWriter(defw);
			do
			{
				MessageDigest mesdigest=MessageDigest.getInstance("MD5");
				numbytes=fis.read(b);
		        	if(numbytes>0)
				{	
					mesdigest.update(b,0,numbytes);
					hashvalue=getHash(mesdigest.digest());
					bw.write(hashvalue);
					System.out.println(hashvalue.length());
					if(map.get(hashvalue)==null)
					{			
						debw.write(ConvertFormat(numbytes,b));
						map.put(hashvalue,map.Current_Length++);
						if(map.Current_Length%10000==0)
						{
							dedupefile="dedupe"+map.Current_Length/10000+".txt";							
							defw = new FileWriter(dedupefile, true);
							debw = new BufferedWriter(defw);
						}
					}
				}
			}while(numbytes!=-1);
			fis.close();
			bw.close();			
			fw.close();	
			debw.close();
			defw.close();			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception :"+e);
		}
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
     	public void removeDedupe(String path)throws Exception
	{
		File[] files= new File(path).listFiles();
		for(File file :files)
		{
			if(file.isFile())
			{
				String metapath=file.getParent()+"/"+stripExtension(file.getName())+".src";
				getChecksum(file.getAbsolutePath(),metapath);
				file.delete();
			}
			else
			{
				if(!((file.getName()).equals("..") || (file.getName()).equals(".")))
				removeDedupe( file.getAbsolutePath()+"/");
			}
		}
	}
	public static byte[] compressstring(String str) throws IOException 
	{
		if ((str == null) || (str.length() == 0)) 
		{
			  return null;
		}
                ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(str.getBytes("UTF-8"));
		gzip.flush();
		gzip.close();
		return obj.toByteArray();
	  }
	public static void compress(String data) throws IOException 
	{
		File f=new File("dedupe.json");
		f.delete();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
		GZIPOutputStream gzip = new GZIPOutputStream(bos);
		gzip.write(data.getBytes());
		gzip.close();
		OutputStream os= new FileOutputStream("dedupe.txt");
		bos.writeTo(os);
		bos.close();
		os.close();
		
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
	public static void main(String args[]) throws Exception
	{
		ChunkHash f=new ChunkHash();
		f.removeDedupe("Test/");
		Gson gson=new Gson();
		compress(gson.toJson(map));			
	}
}
