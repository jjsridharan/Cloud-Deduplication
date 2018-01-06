<?php
	$mysql_hostname = "localhost";
	$mysql_user = "id4035272_user";
	$mysql_password = "sridharan";
	$mysql_database = "id4035272_cloud";
	$conn = @mysqli_connect($mysql_hostname, $mysql_user,$mysql_password) or die("Could not connect database");
	mysqli_select_db($conn,$mysql_database) or die("<h1>Could not select database<h1>");
	$user=$_POST['username'];
	$pass=$_POST['password'];
	$name=$_POST['name'];
	$secquest=$_POST['secquest'];
	$qry="select * from User where username='$user' LIMIT 1";
	$res=mysqli_query($conn,$qry);
	
	if($res && mysqli_num_rows($res)>0)
	{
		echo "User name alread exists";
	}
	else
	{
		$qry="select usercount from Server";
		$res=mysqli_query($conn,$qry);
		if($res && mysqli_num_rows($res)>0)
		{
			$result=mysqli_fetch_assoc($res);
			$count=$result['usercount'];
			$add=$user.$count;
			$qry="insert into User values('$user','$pass','$name','$secquest','$add')";
			$res=mysqli_query($conn,$qry);
			if($res)
			{
				echo "Successfully Registered";
					$count=$count+1;
		        	$qry="Update Server set usercount='$count'";
		        	$res=mysqli_query($conn,$qry);
		   	}
			else
			{
				echo "Failed to Register";
			}
		
		}
		else
		{
			echo "Failed to register";
		}
	}
?>