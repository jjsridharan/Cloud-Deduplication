import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPFile;


public class ListFiles
{
	public static List<ListingFile> ListFilesandDirectory(String path)throws Exception
	{
		String response=Client.GetServerDetails();	
		String responsearr[]=response.split("###",0);
		String server=responsearr[0];
		String user=responsearr[1];
		String pass=responsearr[2];
		path=responsearr[3]+path+"/";
		Upload.CheckforDirectory(server,user,pass,path);
		CuckooHashMap<String,String> list=new CuckooHashMap<String,String>();
		list.put("TSIL###",path);
		Gson gson=new Gson();		
		String listsend=gson.toJson(list),result="";		
		Socket s=new Socket(server,9999);
		listsend=Upload.ReedSolomonFunc(listsend);  
		DataInputStream din=new DataInputStream(s.getInputStream());  
		DataOutputStream dout=new DataOutputStream(s.getOutputStream());  		  
		dout.writeUTF(listsend);  
		dout.flush();
		result=din.readUTF();
		List<ListingFile> res=gson.fromJson(result, new TypeToken<List<ListingFile>>(){}.getType());	
		res.add(new ListingFile(responsearr[3],false));
		return res;
	}
	public static void main(String[] args)throws Exception
	{
		List<ListingFile> list=ListFilesandDirectory(args[0]);		
		for(ListingFile i : list)
		{
			System.out.print(i.name+"###"+i.isdirectory+"###");
		}
	}
}
