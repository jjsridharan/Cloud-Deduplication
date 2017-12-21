import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPFile;

public class ListFiles
{
	public static void ListFilesandDirectory(String server,String path)throws Exception
	{
		CuckooHashMap<String,String> list=new CuckooHashMap<String,String>();
		list.put("list",path);
		Gson gson=new Gson();		
		String listsend=gson.toJson(list),result="";		
		Socket s=new Socket(server,9999);  
		DataInputStream din=new DataInputStream(s.getInputStream());  
		DataOutputStream dout=new DataOutputStream(s.getOutputStream());  		  
		dout.writeUTF(listsend);  
		dout.flush();
		result=din.readUTF();
		System.out.println("Files are\n"+result);
	}
	public static void main(String[] args)throws Exception
	{
		ListFilesandDirectory("127.0.0.1","/home/sridharan/Server/User1/");
	}
}
