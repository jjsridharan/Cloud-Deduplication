import java.io.*;
public class ListFile
{
	public static String stripExtension (String str) 
	{
		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
    }
	public static void ListFiles(String path)throws Exception
	{
		File[] files= new File(path).listFiles();
		for(File file :files)
		{
			if(file.isFile())
			{
				String filename=stripExtension(file.getAbsolutePath())+"."+RetrieveAttribute.Retrieve(file.getAbsolutePath());
				String split=filename.substring(filename.indexOf("Test") + 4);
				System.out.println(split);
			}
			else
			{
				if(!((file.getName()).equals("..") || (file.getName()).equals(".")))
				ListFiles( file.getAbsolutePath()+"/");
			}
		}
	}
	
	public static void main(String args[])throws Exception
	{
			ListFiles("Test");
	}
}