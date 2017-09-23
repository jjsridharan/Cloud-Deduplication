import java.io.*;
import java.security.MessageDigest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.bind.DatatypeConverter;
import java.util.Scanner;

public class ChunkHash
{
	static CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>> map;
	static Pair<Integer,Integer> pair1;
	static Pairing<Integer,Pair<Integer,Integer>> pair;
	static Integer bytecount;
	ChunkHash()
	{
		try
		{
			if(new File("dedupe.txt").exists())
			{
				decompress();
				Gson gson=new Gson(); 
				Reader reader = new FileReader("dedupe.json");
				map = gson.fromJson(reader, new TypeToken<CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>>>(){}.getType());
				reader.close();
				new File("dedupe.json").delete();
				Scanner sa=new Scanner(new File("current.txt"));
				map.Current_Length=new Integer(sa.nextLine());
				bytecount=new Integer(sa.nextLine());
				System.out.println(map.Current_Length);
			}
			else
			{
				map=new CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>>();
				map.Current_Length=new Integer(0);
				bytecount=new Integer(0);
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
	public static byte[] ConvertFormat(int numbytes,byte[] b)throws IOException
	{
		byte[] cb=Arrays.copyOfRange(b, 0, numbytes);
		String content=DatatypeConverter.printBase64Binary(cb);
		byte[] ba=compressstring(content);
		pair1=new Pair<Integer,Integer>(bytecount,ba.length);
		pair=new Pairing<Integer,Pair<Integer,Integer>>(map.Current_Length++,pair1);	
		System.out.println(map.Current_Length);
		bytecount+=ba.length;	
		return ba;
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
			String dedupefile="dedupe"+map.Current_Length/1000+".txt";
			FileOutputStream out = new FileOutputStream(dedupefile,true);
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
						out.write(ConvertFormat(numbytes,b));
						map.put(hashvalue,pair);
						if(map.Current_Length%1000==0)
						{
							dedupefile="dedupe"+map.Current_Length/1000+".txt";
							out.flush();
							out.close();
							out = new FileOutputStream(dedupefile,true);
							bytecount=new Integer(0);							
						}
						if(map.Current_Length%100==0)
						{
							out.flush();
						}
					}
				}
			}while(numbytes!=-1);
			fis.close();
			bw.close();			
			fw.close();
			out.flush();	
			out.close();		
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
				System.out.println(file.getName());
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
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
		GZIPOutputStream gzip = new GZIPOutputStream(bos);
		gzip.write(data.getBytes());
		gzip.close();
		OutputStream os= new FileOutputStream("dedupe.txt");
		bos.writeTo(os);
		bos.close();
		os.close();
		Writer wr=new FileWriter("current.txt");
		wr.write(""+ map.Current_Length+"\n"+bytecount);
		wr.close();		
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
