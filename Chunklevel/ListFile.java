import java.io.*;
import java.lang.StringBuilder;

import java.util.ArrayList;
import java.util.List;
public class ListFile
{
	public static String stripExtension (String str) 
	{
		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
        }
	public static List<ListingFile> ListFilesandDirectory(String path)throws Exception
	{
		List<ListingFile> result=new ArrayList<ListingFile>();
		File[] files= new File(path).listFiles();
		for(File file :files)
		{
			if(file.isFile())
			{
				String filename=stripExtension(file.getAbsolutePath())+"."+RetrieveAttribute.Retrieve(file.getAbsolutePath());
				result.add(new ListingFile(filename,false));
			}
			else
			{
				if(!((file.getName()).equals("..") || (file.getName()).equals(".")))
				result.add(new ListingFile(file.getName(),true));				
			}
		}
		return result;
	}

	
	public static void main(String args[])throws Exception
	{
			List<ListingFile> res=ListFilesandDirectory("/home/sridharan/server/sridharan995/");
		for(ListingFile file : res)
		{
			System.out.println(file.name);
		}
	}
}
