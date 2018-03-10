import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class DeleteFiles
{
	public static String stripExtension (String str) 
	{
		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
	}
	static void DeleteFilesRecursively(FTPClient ftpClient,String folder,String dest)throws Exception
	{
		List<ListingFile> listfromserver=ListFiles.ListFilesandDirectory(folder);
		List<String> listoffiles=new ArrayList<String>();
		int numfiles=listfromserver.size()-1;
		File destname=new File(dest);
		destname.mkdirs();
		for(int j=0;j<numfiles;j++)
		{
			if((listfromserver.get(j)).isdirectory)
			{
				String subdir=folder+((listfromserver.get(j)).name).substring(((listfromserver.get(j)).name).lastIndexOf("/"));
				System.out.println(subdir);
				DeleteFilesRecursively(ftpClient,subdir,dest+subdir.substring(subdir.lastIndexOf('/')+1)+"/");
			ftpClient.removeDirectory((listfromserver.get(j)).name);
			}
			else
			{
				listoffiles.add((listfromserver.get(j)).name);
			}
		}
		DeleteFileList(listoffiles);
	}
	public static void DeleteDirectory(String folder,String fname)throws Exception
	{
		String server,user,pass,base;
		String response=Client.GetServerDetails();	
		String responsearr[]=response.split("###",0);
		server=responsearr[0];
		user=responsearr[1];
		pass=responsearr[2];
		base=responsearr[3];
		FTPClient ftpClient = DownloadFiles.login(server,user,pass);
		DeleteFilesRecursively(ftpClient,folder,((System.getProperty("user.home")).replace("\\","/"))+"/Downloads/"+fname+"/");
		ftpClient.removeDirectory(base+folder);
		
	}
    	public static void DeleteFileList(List<String> files)throws Exception
	{
		String server,user,pass;
		String response=Client.GetServerDetails();	
		String responsearr[]=response.split("###",0);
		server=responsearr[0];
		user=responsearr[1];
		pass=responsearr[2];
		FTPClient ftpClient = DownloadFiles.login(server,user,pass);		
		for(String filename : files)
		{
			filename=filename.replaceAll("\n","");
			filename=stripExtension(filename)+".src";
			ftpClient.deleteFile(filename);
		}
	}	
   	public static void main(String[] args)throws Exception
   	{
   		String response=Client.GetServerDetails();	
		String responsearr[]=response.split("###",0);
		List<String> list=new ArrayList<String>();
		list.add(responsearr[3]+args[0]);
		DeleteFileList(list);
    	}
}
