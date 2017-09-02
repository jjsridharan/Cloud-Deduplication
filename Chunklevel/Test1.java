import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

class Test1
{
	public static String stripExtension (String str) {
        // Handle null case specially.

        if (str == null) return null;

        // Get position of last '.'.

        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.

        if (pos == -1) return str;

        // Otherwise return the string, up to the dot.

        return str.substring(0, pos);
    	}
	public static void main(String arg[]) throws Exception
	{	
		File f=new File("/home/sridharan/Cloud-Deduplication/Chunklevel/test.txt");
		String metapath=f.getParent()+"/"+stripExtension(f.getName())+".src";
		File mf=new File(metapath);
		Path filepath=Paths.get(f.getAbsolutePath());
		mf.createNewFile();
		 BasicFileAttributes attr =Files.readAttributes(filepath, BasicFileAttributes.class);
        	System.out.println("creationTime     = " + attr.creationTime());
        	System.out.println("lastAccessTime   = " + attr.lastAccessTime());
        	System.out.println("lastModifiedTime = " + attr.lastModifiedTime());

        	System.out.println("isDirectory      = " + attr.isDirectory());
        	System.out.println("isOther          = " + attr.isOther());
        	System.out.println("isRegularFile    = " + attr.isRegularFile());
        	System.out.println("isSymbolicLink   = " + attr.isSymbolicLink());
        	System.out.println("size             = " + attr.size());
	}
}
