import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.bind.DatatypeConverter;

public class RetrieveFile
{
	static CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>> map;
	RetrieveFile()
	{
		try
		{
			if(new File("dedupe.txt").exists())
			{
				decompress();
				Gson gson=new Gson();
				Reader reader = new FileReader("dedupe.json");
				map = gson.fromJson(reader, new TypeToken<CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>>>(){}.getType());
				new File("dedupe.json").delete();
			}
			else
			{
				map=new CuckooHashMap<String,Pairing<Integer,Pair<Integer,Integer>>>();
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
	public static void getFile(String mfile,String file)throws IOException
	{
		try
		{
			Reader reader=new FileReader(mfile);
			FileOutputStream fos = new FileOutputStream(file,true);
			//copy attributes
			int r=0,l=0;
			do
			{
				char[] chars = new char[48];
				r=reader.read(chars,0,48);
				if(r==48)
				{
					String str = String.valueOf(chars);
					Pairing<Integer,Pair<Integer,Integer>> pair=map.get(str);
					String position = String.valueOf(pair.getLeft());
					long pos = (long)(Double.parseDouble(position));
					String defile="dedupe" + pos/1000 + ".txt";
					RandomAccessFile raf = new RandomAccessFile(defile, "r");
					Pair<Integer,Integer> pair1=pair.getRight();
					raf.seek(pair1.getLeft());
					byte[] contentbuf=new byte[pair1.getRight()+1];
					raf.read(contentbuf,0,pair1.getRight());					
					String content=decompressstring(contentbuf);
					byte[] buf=DatatypeConverter.parseBase64Binary(content);
					fos.write(buf);
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
	public static String decompressstring(byte[] compressed) {
        if ((compressed == null) || (compressed.length == 0)) {
            throw new IllegalArgumentException("Cannot unzip null or empty bytes");
        }
        if (!isZipped(compressed)) {
            return new String(compressed);
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressed)) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
                    try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        StringBuilder output = new StringBuilder();
                        String line;
                        while((line = bufferedReader.readLine()) != null){
                            output.append(line);
                        }
                        return output.toString();
                    }
                }
            }
        } catch(IOException e) {
            throw new RuntimeException("Failed to unzip content", e);
        }
    }

    public static boolean isZipped(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
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
		String path=new String("Test\\5-  Delta Bookshelf.mp4");  
		String opath=stripExtension(path)+".src";	
		getFile(opath,path);
	}
}
