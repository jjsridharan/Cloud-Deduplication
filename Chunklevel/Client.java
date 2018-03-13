import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class Client
{
	public static String RegisterUser(String username,String password,String mail,String phone)throws Exception
	{
		String url = "https://clouddeduplication.000webhostapp.com/register.php";
 		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");		
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		String urlParameters = "username="+username+"&password="+password+"&mail="+mail+"&phone="+phone;
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();

		if(response.toString().contains("Success"))
		{
			return "Successfully Registered";
		}
		else if(response.toString().contains("User"))
		{
			return "username already exists. Please try different username";
		}
		else
		{
			return "Error in registering. Please try after some time";
		}
	}
	public static String Login(String username,String password)throws Exception
	{
		String url = "https://clouddeduplication.000webhostapp.com/login.php";
 		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");		
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		String urlParameters = "username="+username+"&password="+password;
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		

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
			return response.toString();
		}
		else
		{
			return "Invalid Credentials";
		}
	}
	public static String LogActivity(String username,String log)throws Exception
	{
		String url = "https://clouddeduplication.000webhostapp.com/logdetails.php";
 		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");		
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		String urlParameters = "username="+username+"&log="+log;
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();

		if(!(response.toString().contains("Error")))
		{
			return response.toString();
		}
		else
		{
			return "Error in logging data. This activity will not be logged.";
		}
	}
	public static String ShowLog(String username)throws Exception
	{
		String url = "https://clouddeduplication.000webhostapp.com/showlog.php";
 		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");		
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		String urlParameters = "username="+username;
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();

		if(!(response.toString().contains("Error")))
		{
			return response.toString();
		}
		else
		{
			return "unable to fetch log";
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
	public static String GetServerDetails()throws Exception
	{
		String url = "https://clouddeduplication.000webhostapp.com/getdetails.php";
 		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");		
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");		
		con.setDoOutput(true);
		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();
		return response.toString();
		
	}
	public static String ForgotPassword(String username,String secquest)throws Exception
	{
		secquest=secquest.toLowerCase();
		String url = "https://clouddeduplication.000webhostapp.com/forgotpassword.php";
 		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");		
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		String urlParameters = "username="+username+"&secquest="+secquest;
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();
		if(!((response.toString()).contains("Error")))
		{
			return response.toString();
		}
		else
		{
			return "Invalid Credentials";
		}
	}
	public static void main(String args[])throws Exception
	{
		if(args[0].equals("login"))
		{
			String response=Login(args[1],args[2]);
			String responses=LogActivity(args[1],"Login Attempt");
			if(!response.contains("Invalid Credentials"))
			{
				System.out.print(response);
			}
			else
				System.out.print("0");
		}
		else if(args[0].equals("signup"))
		{
			String response=RegisterUser(args[1],args[2],args[3],args[4]);			
			if(response.contains("Successfully Registered"))
			{
				String responses=LogActivity(args[1],"Created Account");
				System.out.print(response);
			}
			else if(response.contains("username already exists"))
			{
				System.out.print(response);
			}
			else
				System.out.print("0");
			
		}
		else if(args[0].equals("forgot"))
		{	
			String responses=LogActivity(args[1],"Attempting Forgot Password");						
			String response=ForgotPassword(args[1],args[2]);
			System.out.print(response);			
		}
		else if(args[0].equals("logactivity"))
		{			
			String response=LogActivity(args[1],args[2]);
			System.out.print(response);			
		}
		else if(args[0].equals("showlog"))
		{			
			String response=ShowLog(args[1]);
			System.out.print(response);			
		}
	}

}
