import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class Client
{
	public static void RegisterUser(String username,String password,String name,String secretquestion)throws Exception
	{
		String url = "https://clouddeduplication.000webhostapp.com/register.php";
 		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");		
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		System.out.println(username);
		secretquestion=secretquestion.toLowerCase();
		String urlParameters = "username="+username+"&password="+password+"&name="+name+"&secquest="+secretquestion;
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		if(response.toString().contains("Success"))
		{
			System.out.println("Successfully Registered");
		}
		else if(response.toString().contains("User"))
		{
			System.out.println("username already exists");
		}
		else
		{
			System.out.println("Error in registering");
		}
	}
	public static void Login(String username,String password)throws Exception
	{
		String url = "https://clouddeduplication.000webhostapp.com/login.php";
 		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");		
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		System.out.println(username);
		String urlParameters = "username="+username+"&password="+password;
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();

		//print result
		
		if(!(response.toString().contains("Error")))
		{
			System.out.println("Successfully Logged in");
			System.out.println(response.toString());
		}
		else
		{
			System.out.println("Error in login");
		}
	}
	public static void ChangePassword(String username,String password)throws Exception
	{
		String url = "https://clouddeduplication.000webhostapp.com/changepassword.php";
 		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");		
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		System.out.println(username);
		String urlParameters = "username="+username+"&password="+password;
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		if(response.toString().contains("Success"))
		{
			System.out.println("Successfully Password Changed");
		}
		else
		{
			System.out.println("Error in Changing Password");
		}
	}
	public static void ForgotPassword(String username,String password,String secquest)throws Exception
	{
		secquest=secquest.toLowerCase();
		String url = "https://clouddeduplication.000webhostapp.com/forgotpassword.php";
 		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");		
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		System.out.println(username);
		String urlParameters = "username="+username+"&secquest="+secquest;
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		if(response.toString().contains("Success"))
		{
			ChangePassword(username,password);
			System.out.println("Successfully Password Changed");
		}
		else
		{
			System.out.println("Error in Changing Password");
		}
	}
	public static void main(String args[])throws Exception
	{
		ForgotPassword("Sridharan99","sri","ajiths");
	}

}
