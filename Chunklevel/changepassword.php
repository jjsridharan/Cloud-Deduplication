<?php
	$mysql_hostname = "localhost";
	$mysql_user = "id4035272_user";
	$mysql_password = "sridharan";
	$mysql_database = "id4035272_cloud";
	$conn = @mysqli_connect($mysql_hostname, $mysql_user,$mysql_password) or die("Could not connect database");
	mysqli_select_db($conn,$mysql_database) or die("<h1>Could not select database<h1>");
	$user=$_POST['username'];
	$pass=$_POST['password'];
	$qry="select * from User where username='$user' LIMIT 1";
	$res=mysqli_query($conn,$qry);
	
	if($res && mysqli_num_rows($res)>0)
	{
	    $row=mysqli_fetch_assoc($res);
	    $op=$row['password'];
		$qry="Update User set password='$pass'";
		$res=mysqli_query($conn,$qry);
		if(mysqli_affected_rows($conn)>0)
		{
		    echo "Successfully Changed";
		}
		else
		{
		    $qry="Update User set password='$op'";
		    $res=mysqli_query($conn,$qry);
		    echo "Error";
		}
	}
	else
	{
		echo "Error";
	}
?>