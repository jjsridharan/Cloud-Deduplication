import java.net.Socket;  
import java.security.MessageDigest;
import java.io.*;  
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.bind.DatatypeConverter;

class Client
{  
	public static List<String> listofhash;
	public static CuckooHashMap<String,String> hashlist;
	public static CuckooHashMap<String,Pair<Integer,Integer>> hashoffset;
	public static CuckooHashMap<String,String> offsetlist;
	public static CuckooHashMap<String,Pair<String,Integer>> map;	
	static Pair<Integer,Integer> pair;
	public static int bytecount;
	Client()
	{	
		listofhash=new ArrayList<String>();
		hashlist=new CuckooHashMap<String,String>();
		offsetlist=new CuckooHashMap<String,String>();
		hashoffset=new CuckooHashMap<String,Pair<Integer,Integer>>();
		map=new CuckooHashMap<String,Pair<String,Integer>>();
		bytecount=0;
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
	public static byte[] ConvertFormat(int numbytes,byte[] b)throws IOException
	{
		byte[] cb=Arrays.copyOfRange(b, 0, numbytes);
		String content=DatatypeConverter.printBase64Binary(cb);
		byte[] ba=compressstring(content);
		pair=new Pair<Integer,Integer>(bytecount,ba.length);
		bytecount+=ba.length;
		return ba;
	}
	void getList(List<String> files)throws Exception
	{
		MessageDigest mesdigest;
		InputStream fis;
		FileWriter fw;
		BufferedWriter bw;
		String hashvalue;
		int index,numbytes,count;
		byte[] b=new byte[4194304];
		Pair<String,Integer> identity;	
		for(String filename : files)
		{
			File file=new File(filename);
			index = (file.getName()).lastIndexOf('.');
			String extension=(file.getName()).substring(index+1);
			System.out.println(file.getName());		
			String metapath=file.getParent()+"/"+stripExtension(file.getName())+".src";
			File metafile=new File(metapath);
			metafile.createNewFile();
			fw = new FileWriter(metapath);
			bw = new BufferedWriter(fw);					
			fis=new FileInputStream(file.getAbsolutePath());
			mesdigest=MessageDigest.getInstance("MD5");
			count=0;
			do
			{				
				numbytes=fis.read(b);
				if(numbytes>0)
				{	
					mesdigest.update(b,0,numbytes);
					hashvalue=getHash(mesdigest.digest());
					bw.write(hashvalue);
					listofhash.add(hashvalue);
					identity=new Pair<String,Integer>(filename,new Integer(count++));
					map.put(hashvalue,identity);
				}
			}while(numbytes!=-1);
			fis.close();
			bw.close();
			fw.close();
			CopyAttributes.copy(file.getAbsolutePath(),metapath);
			SaveAttribute.Save(metapath,extension);
		}
	}
	public void SeparateDedup(List<String> dedup)throws Exception
	{
		FileOutputStream out = new FileOutputStream("sepdedupe.txt");	
		Pair<String,Integer> identity;
		RandomAccessFile raf;
		byte[] b=new byte[4194304];
		int numbytes;
		for(String iterate : dedup)
		{
			identity=map.get(iterate);
			raf = new RandomAccessFile(identity.getLeft(), "r");
			raf.seek((identity.getRight()*4194304));
			numbytes=raf.read(b);
			System.out.println(numbytes);
			out.write(ConvertFormat(numbytes,b));
			hashoffset.put(iterate,pair);
			System.out.println(pair.getLeft()+""+pair.getRight());
			raf.close();	
		}
		out.close();
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
	public static void main(String args[])throws Exception
	{  
		List<String> files=new ArrayList<String>();
		List<String> duplicated;
		//files.add("Test/aa.mp3");
		//files.add("Test/ccc.mp3");
		files.add("Test/a.png");
		files.add("Test/sample.txt");
		Client client=new Client();
		client.getList(files);
		Gson gson=new Gson();
		hashlist.put("put",gson.toJson(listofhash));		
		String listsend=gson.toJson(hashlist),duped="";		
		Socket s=new Socket("127.0.0.1",9999);  
		DataInputStream din=new DataInputStream(s.getInputStream());  
		DataOutputStream dout=new DataOutputStream(s.getOutputStream());  		  
		//while(!listsend.equals("stop"))
		{  
			//System.out.println(listsend);
			dout.writeUTF(listsend);  
			dout.flush();
			duped=din.readUTF();
			//System.out.println(duped);
			duplicated=gson.fromJson(duped, new TypeToken<List<String>>(){}.getType());
			client.SeparateDedup(duplicated);
			listsend=gson.toJson(hashoffset);
			offsetlist.put("process",listsend);
			//System.out.println(gson.toJson(offsetlist));
			s=new Socket("127.0.0.1",9999);  
			din=new DataInputStream(s.getInputStream());  
			dout=new DataOutputStream(s.getOutputStream()); 
			dout.writeUTF(gson.toJson(offsetlist));
			dout.flush();
			duped=din.readUTF();
			//System.out.println(duped);
		}  
		dout.close();  
		s.close();  
	}
}  
