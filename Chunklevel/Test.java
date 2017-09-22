import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Scanner;
import java.io.*;
public class Test
{
	public static void main(String[] args) throws Exception
	{
		CuckooHashMap<String,Pair<Integer,Pair<Integer,Integer>>> map=new CuckooHashMap<String,Pair<Integer,Pair<Integer,Integer>>>();
		Writer wr=new FileWriter("current.txt");
		wr.write(""+ map.Current_Length+"\n"+map.Current_Length);
		wr.close();
		Gson gson=new Gson();
		System.out.println(gson.toJson(map));
		Scanner sa=new Scanner(new File("current.txt"));
		String s=sa.nextLine();
		System.out.println((new Integer(s)+1)+sa.nextLine());
	}
}
