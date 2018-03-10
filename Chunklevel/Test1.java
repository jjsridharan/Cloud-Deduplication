import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test1 {

  public static void main(String[] args) throws Exception 
  {
 
        // starting time
     long start = System.currentTimeMillis();
     CuckooHashMap<String, String> map = new CuckooHashMap<String, String>();
	for(int i=0;i<100;i++)
	{
		map.put(new Integer(i).toString(),new Integer(i).toString());
	}
	   long end = System.currentTimeMillis();
        System.out.println("Counting to 100 takes " +
                                    (end - start) + "ms");
	
  }

}
 
