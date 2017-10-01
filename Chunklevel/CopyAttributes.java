import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

public class CopyAttributes
{
	public static FileTime ct,lat,lmt;
	public static Boolean id,io,irf,isl;
	public static long si;
	public static boolean ia,ih,iro,is;
	public static String own,group,perm;
	public static void copy(String mfile,String fileo)
	{
	try{
			Path file=Paths.get(mfile);
			BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);			
			System.out.println("------------------------File Attributes for /home/nihil/virtual.cpp -------------------------"); 
			//Calendar c = Calendar.getInstance();
			ct=attr.creationTime();
			lat=attr.lastAccessTime();
			lmt=attr.lastModifiedTime();
			id=attr.isDirectory();
			io=attr.isOther();
			irf=attr.isRegularFile();
			isl=attr.isSymbolicLink();
			si=attr.size();

			//// NATURE OF FILE  ////
			DosFileAttributes attrs = Files.readAttributes(file, DosFileAttributes.class);			
			ia=attrs.isArchive();
			ih=attrs.isHidden();
			iro=attrs.isReadOnly();
			is=attrs.isSystem();
/*			System.out.println("------------------------Before -------------------------"); 
			PosixFileAttributeView view = Files.getFileAttributeView(file,PosixFileAttributeView.class);

			//// OWNER INFO  ////
			PosixFileAttributes attrp = view.readAttributes();
						System.out.println("------------------------After -------------------------");
			System.out.format("Owner : %s\nGroup : %s\nPermissions : %s%n\n",attrp.owner().getName(),attrp.group().getName(), 			                       PosixFilePermissions.toString(attrp.permissions()));
			own=attrp.owner().getName();
			group=attrp.group().getName();
			perm=attrp.group().getName();
*/		

			FileOwnerAttributeView foav = Files.getFileAttributeView(file,FileOwnerAttributeView.class);

		        UserPrincipal owner123 = foav.getOwner();
      			System.out.format("Original owner  of  %s  is %s%n", mfile,owner123.getName());
			own=owner123.getName();
		
			// New File Assigning
			Path file2=Paths.get(fileo);
			Files.setAttribute(file2, "basic:lastAccessTime",lat,NOFOLLOW_LINKS);
        		Files.setAttribute(file2, "basic:creationTime",ct,NOFOLLOW_LINKS);
        		Files.setAttribute(file2, "basic:lastModifiedTime",lmt,NOFOLLOW_LINKS);
        		Files.setAttribute(file2, "dos:archive",ia);
        		Files.setAttribute(file2, "dos:hidden",ih);
        		Files.setAttribute(file2, "dos:readonly",iro);
        		Files.setAttribute(file2, "dos:system",is);
        		UserPrincipal owner = file.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName(own);
        		Files.setOwner(file2, owner);
        		//GroupPrincipal group2 = file.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByGroupName(group);
        		//Files.getFileAttributeView(file2, PosixFileAttributeView.class).setGroup(group2);
			}
    		
		catch(Exception e){System.out.println("Exception");}
	}
}
	