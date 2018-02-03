import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import javax.xml.bind.DatatypeConverter;

public class CreateFolder
{
	static void CreateFolder(String base)throws Exception
	{
		String server,user,pass;
		String response=Client.GetServerDetails();	
		String responsearr[]=response.split("###",0);
		server=responsearr[0];
		user=responsearr[1];
		pass=responsearr[2];
		base=responsearr[3]+base+"/";
		Upload.CheckforDirectory(server,user,pass,base);
	}
	public static void main(String args[])throws Exception
	{
		List<String> listoffiles;
		listoffiles=new ArrayList<String>();
		listoffiles.add("bb.mp3");
		//DownloadFiles("/home/student/Server/User1/",listoffiles);
	}	
}
