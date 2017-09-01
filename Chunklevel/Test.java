import java.io.*;
import com.google.gson.Gson;
public class Test
{
	
	public static void main(String[] args)
	{
		Gson gson=new Gson();
		CuckooHashMap<String,String> map=new CuckooHashMap<String,String>();
		map.put("Hello","put");
		try (FileWriter writer = new FileWriter("staff.json")) {

            gson.toJson(map, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }
	try (Reader reader = new FileReader("staff.json")) {

			// Convert JSON to Java Object
            CuckooHashMap<String,String> map1 = gson.fromJson(reader, CuckooHashMap.class);
            System.out.println(map1.get("Hello"));

			// Convert JSON to JsonElement, and later to String
            /*JsonElement json = gson.fromJson(reader, JsonElement.class);
            String jsonInString = gson.toJson(json);
            System.out.println(jsonInString);*/

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
