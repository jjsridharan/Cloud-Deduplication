import java.io.*;
import java.lang.StringBuilder;
public class ListFile
{
	public static String stripExtension (String str) 
	{
		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
        }
	public static StringBuilder ListFilesandDirectory(String path,StringBuilder result)throws Exception
	{
		File[] files= new File(path).listFiles();
		for(File file :files)
		{
			if(file.isFile())
			{
				String filename=stripExtension(file.getAbsolutePath())+".mp3";
				String split=filename.substring(filename.indexOf("Test") + 4);
				result.append(split+"\n");
			}
			else
			{
				if(!((file.getName()).equals("..") || (file.getName()).equals(".")))
				result=ListFilesandDirectory( file.getAbsolutePath()+"/",result);
			}
		}
		return result;
	}
	public static String ListFiles(String path)throws Exception
	{
		StringBuilder result=new StringBuilder("");
		result=ListFilesandDirectory(path,result);
		return new String(result);
	}
	
	public static void main(String args[])throws Exception
	{
			System.out.println(ListFiles("Test/"));
	}
}
