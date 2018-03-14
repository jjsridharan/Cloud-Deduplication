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
		path=path.replaceAll("\n","");
		List<ListingFile> result=new ArrayList<ListingFile>();
		File[] files= new File(path).listFiles();
		for(File file :files)
		{
			try
			{
				if(file.isFile())
				{
					String filename=stripExtension(stripExtension(file.getAbsolutePath()))+"."+RetrieveAttribute.Retrieve(file.getAbsolutePath());
					result.add(new ListingFile(filename,false));
				}
				else
				{
					if(!((file.getName()).equals("..") || (file.getName()).equals(".")))
					result.add(new ListingFile(file.getAbsolutePath(),true));				
				}
			}
			catch(Exception ex)
			{
				if(!((file.getName()).equals("sepdedupe.txt")))
					file.delete();
			}
		}
		return result;
	}

	
	public static void main(String args[])throws Exception
	{
		List<ListingFile> res=ListFilesandDirectory("/home/student/server/hl8");
		for(ListingFile file : res)
		{
			System.out.println(file.name);
		}
	}
}
