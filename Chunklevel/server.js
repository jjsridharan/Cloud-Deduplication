var express = require('express');
var multer = require('multer');
var bodyParser = require('body-parser');
var path=require('path');
var cookieParser = require('cookie-parser')
var fs = require('fs');
var rimraf = require('rimraf');
var app = express();

app.use(cookieParser());
app.use(bodyParser.urlencoded({ extended: false }))
app.use(bodyParser.json());
function mkDirByPathSync(targetDir, {isRelativeToScript = false} = {})
{
	const sep = path.sep;
	const initDir = path.isAbsolute(targetDir) ? sep : '';
	const baseDir = isRelativeToScript ? __dirname : '.';
	targetDir.split(sep).reduce((parentDir, childDir) => 
	{
		const curDir = path.resolve(baseDir, parentDir, childDir);
		try 
		{
		      fs.mkdirSync(curDir);
		      console.log(`Directory ${curDir} created!`);
		} 
		catch (err) 
		{
		      if (err.code !== 'EEXIST') 
		      {
			throw err;
		      }
		}
		return curDir;
	}, initDir);
}


var Storage = multer.diskStorage(
{
	destination: function(req, file, callback)
	{
    		mkDirByPathSync(req.cookies['cdir']);
	     	callback(null, "./"+req.cookies['cdir']);
	},   
	filename: function(req, file, callback) 
	{
        	callback(null, file.originalname);
     	}
});

var upload = multer(
{
     storage: Storage
}).array("imgUploader"); 

app.get("/download.html", function(req, res) 
{
	if(!(typeof req.cookies['cdir'] !== 'undefined' && req.cookies['cdir']))
     		res.sendFile(__dirname + "/index.html");
     	else
     	{
		var indexof=req.cookies['uname'];
		if((req.cookies['cdir']).indexOf("/")!=-1)
		{
			indexof+=(req.cookies['cdir']).substring((req.cookies['cdir']).indexOf("/"));
		}
		var exec = require('child_process').execSync;
		var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" ListFiles '+req.cookies['cdir']+'  > login/'+req.cookies['uname']+'list.txt');
		fs.readFile('login/'+req.cookies['uname']+'list.txt', function (err, data) 
	 	{
			if (err) throw err;
			var out=(data.toString()).split("###");
			var outl=out.length;			
	    		res.write("<html><head><link href='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css' rel='stylesheet'/><script src='https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js'></script> <script>    $(document).ready(function()  {  $('button').on('click',function()  	{  	var val=$(this).val();   	var con=val.split('###');if(con[1]=='true')  	{  	document.getElementById('dname').value=con[0];   	$('#dir').submit();  	}   	else  	{  	document.getElementById('fname').value=con[0];  	$('#down').submit();  	  	}  	});  });  </script>  </head>  <body>  <form id='down'action= '/download' method='post'><input type='text' id='fname' name='fname' hidden/></form><form action='/directory' method='post' id='dir'><input type='text' name='folder' id='dname' hidden/></form><strong>Index Of "+indexof+"</strong><br/><br/>");
	    		res.write("<a><button type='button' value=\"..###true\" class='btn btn-link'>..</button><br/></a>");   		
	    		for(var i=0;i<outl-3;i+=2)
	    		{
	    			out[i]=out[i].substring(out[i].indexOf(req.cookies['cdir'])+(req.cookies['cdir']).length+1);
	    			res.write("<a><button type='button' value=\""+out[i]+"###"+out[i+1]+"\" class='btn btn-link'>"+out[i]+"</button><br/></a>");
	   		}
	   		res.write("<br/><br/><strong>Create Folder</strong><br/><br/><form method='post' action='/directory'><input required type='text' name='folder' /><input type='submit' value='Create Folder'/></form>");
	    		res.write("<br/><br/><strong>Upload Files</strong><br/><br/><form id='frmUploader' enctype='multipart/form-data' action='api/Upload/' method='post'><input type='file' name='imgUploader' multiple required/><input type='submit' name='submit' id='btnSubmit' value='Upload' /> </form><br/><br/><br/><a href='delete.html'>Delete Files</a><br/><br/><br/><a href='logout'>Logout</a></body></html>");
	    		res.end();
		});	
	}	
 });
 
 app.get("/delete.html", function(req, res) 
{
	if(!(typeof req.cookies['cdir'] !== 'undefined' && req.cookies['cdir']))
     		res.sendFile(__dirname + "/index.html");
     	else
     	{
		var indexof=req.cookies['uname'];
		if((req.cookies['cdir']).indexOf("/")!=-1)
		{
			indexof+=(req.cookies['cdir']).substring((req.cookies['cdir']).indexOf("/"));
		}
		var exec = require('child_process').execSync;
		var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" ListFiles '+req.cookies['cdir']+'  > login/list.txt');
		fs.readFile('login/list.txt', function (err, data) 
	 	{
			if (err) throw err;
			var out=(data.toString()).split("###");
			var outl=out.length;
			var indexof=req.cookies['uname'];
			if((req.cookies['cdir']).indexOf("/")!=-1)
			{
				indexof+=(req.cookies['cdir']).substring((req.cookies['cdir']).indexOf("/"));
			}
	    		res.write("<html><head><link href='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css' rel='stylesheet'/><script src='https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js'></script> <script>    $(document).ready(function()  {  $('button').on('click',function()  	{  	var val=$(this).val();   	var con=val.split('###');if(con[1]=='true')  	{  	document.getElementById('dname').value=con[0];   	$('#dir').submit();  	}   	else  	{  	if(confirm('Do you really want to delete this file?')){document.getElementById('fname').value=con[0];  	$('#down').submit();  	  }	}  	});  });  </script>  </head>  <body>  <form id='down'action= '/delete' method='post'><input type='text' id='fname' name='fname' hidden/></form><form action='/directory' method='post' id='dir'><input type='text' name='folder' id='dname' hidden/></form><strong>Index Of "+indexof+"</strong><br/><br/>");
	    		res.write("<a><button type='button' value=\"..###true\" class='btn btn-link'>..</button><br/></a>");   		
	    		for(var i=0;i<outl-3;i+=2)
	    		{
	    			out[i]=out[i].substring(out[i].indexOf(req.cookies['cdir'])+(req.cookies['cdir']).length+1);
	    			res.write("<a><button type='button' value=\""+out[i]+"###"+out[i+1]+"\" class='btn btn-link'>"+out[i]+"</button><br/></a>");
	   		}
	   		res.write("<br/><br/><br/><a href='download.html'>Download & Upload Files</a><br/><br/><br/><a href='logout'>Logout</a></body></html>");
	    		res.end();
		});	
	}	
 });
app.get("/login.html", function(req, res) 
{
     if(!(typeof req.cookies['cdir'] !== 'undefined' && req.cookies['cdir']))
     	res.sendFile(__dirname + "/login.html");
     else
     	res.redirect("/download.html");
});
app.get("/forgot.html", function(req, res) 
{
     if(!(typeof req.cookies['cdir'] !== 'undefined' && req.cookies['cdir']))
     	res.sendFile(__dirname + "/forgot.html");
     else
     	res.redirect("/download.html");
});	
app.get("/signup.html", function(req, res) 
{
      if(!(typeof req.cookies['cdir'] !== 'undefined' && req.cookies['cdir']))
     	res.sendFile(__dirname + "/signup.html");
     else
     	res.redirect("/download.html");
});
app.get("/", function(req, res) 
{
     if(!(typeof req.cookies['cdir'] !== 'undefined' && req.cookies['cdir']))
     	res.sendFile(__dirname + "/index.html");
     else
     	res.redirect('/download.html');
});
app.get("/index.html", function(req, res) 
{
     console.log(req.cookies['cdir']=="undefined");
     if(!(typeof req.cookies['cdir'] !== 'undefined' && req.cookies['cdir']))
     	res.sendFile(__dirname + "/index.html");
     else
     	res.redirect('/download.html');
});
app.post("/api/Upload", function(req, res) 
{
	upload(req, res, function(err)
	{
	        if (err)
	        {
		       res.write("<script> alert('Something went wrong');  setTimeout(function () {         window.location = '/download.html';  }, 50)</script>");
		}
	        else
         	{
			var indexof=req.cookies['uname'];
			if((req.cookies['cdir']).indexOf("/")!=-1)
			{
				indexof+=(req.cookies['cdir']).substring((req.cookies['cdir']).indexOf("/"));
			}
			var exec = require('child_process').exec;
			var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" Upload '+req.cookies['cdir']+' '+req.cookies['uname']+' '+indexof,
			function(err, out, code) 
			{
			  	if (err instanceof Error)
				    throw err;
				console.log(out.toString());
				var files=req.files;
				for(var i=0;i<files.length;i++)
				{
					console.log(req.cookies['cdir']+"/"+files[i].originalname);
					fs.unlink(req.cookies['cdir']+"/"+files[i].originalname,function(err){ if (err instanceof Error)
				    throw err;});
				}
			});
			res.write("<script> alert('Files Uploaded Successfully');  setTimeout(function () {         window.location = '/download.html';  }, 50)</script>");
		}
		res.end();
	});
});
 
app.post("/download", function(req, res) 
{	
	var fname=req.body.fname;
	fname=fname.substring(fname.lastIndexOf("/")+1);
	mkDirByPathSync(req.cookies['cdir']);
	var indexof=req.cookies['uname'];
	if((req.cookies['cdir']).indexOf("/")!=-1)
	{
		indexof+=(req.cookies['cdir']).substring((req.cookies['cdir']).indexOf("/"));
	}
 	var exec = require('child_process').exec;
	var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" DownloadFiles '+req.cookies['cdir']+' "'+fname+'" '+req.cookies['uname']+' '+indexof,function(err,data,code)
	{   	
		if (err instanceof Error)
				    throw err;
	   	var file = __dirname +'/'+ req.cookies['cdir']+'/'+fname;  
   		var fileName = fname; 
    		res.download(file, fileName,function(err)
    		{
    			if (err instanceof Error)
				    throw err;
			fs.unlink(file,function(err)
			{
				if (err instanceof Error)
				    throw err;
				
			});
    		});	
	});
});

app.post("/delete", function(req, res) 
{	
	console.log(req.body.fname);
	var fname=req.body.fname;
	fname=fname.substring(fname.lastIndexOf("/")+1);
	mkDirByPathSync(req.cookies['cdir']);
	var indexof=req.cookies['uname'];
	if((req.cookies['cdir']).indexOf("/")!=-1)
	{
		indexof+=(req.cookies['cdir']).substring((req.cookies['cdir']).indexOf("/"));
	}
 	var exec = require('child_process').exec;
	var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" DeleteFiles "'+req.cookies['cdir']+'/'+fname+'" '+req.cookies['uname']+' '+indexof,function(err,data,out)   	
   	{
		 if (err instanceof Error)
				    throw err;
		res.write("<script> alert('File Deleted Successfully');  setTimeout(function () {         window.location = '/delete.html';  }, 500)</script>");	
   		res.end();
	}
);
});

app.post("/login", function(req, res) 
{
	var exec = require('child_process').exec;
	mkDirByPathSync('login');
	var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" Client login '+ req.body.uname+' '+ req.body.upass+' > login/'+req.body.uname+'.txt',function(err,data,code)
	{
		if (err instanceof Error)
				    throw err;
		fs.readFile('login/'+req.body.uname+'.txt', function (err, data) 
	 	{
	    		if (err) throw err;
				var out=data.toString();
			if(!(out=="0"))
	    		{
	    			res.cookie('cdir',out);
	    			res.cookie('uname',req.body.uname);
	    			res.write("<script> alert('Login Successful');  setTimeout(function () {         window.location = '/';  }, 50)</script>");
	   		}
	   		else
			{
			    	res.write("<script> alert('Login Failed');  setTimeout(function () {         window.location = '/';  }, 50)</script>");
			}
	    		res.end();
		});
	}
	);

});
app.post("/forgot", function(req, res) 
{
	if((typeof req.cookies['cdir'] !== 'undefined' && req.cookies['cdir']))
     		res.redirect("/index.html");
     	else
	{
		var exec = require('child_process').execSync;
		mkDirByPathSync('login');
		var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" Client forgot '+ req.body.uname+' '+ req.body.umobile+' > login/'+req.body.uname+'forgot.txt',function(err,data,code)
		{
 			if (err instanceof Error)
				throw err;

		fs.readFile('login/'+req.body.uname+'forgot.txt', function (err, data) 
	 	{
	    		if (err) throw err;
				var out=data.toString();
			if(out=="Invalid Credentials")
	    		{
	    			res.write("<script> alert('Invalid Credentials');  setTimeout(function () {         window.location = '/';  }, 50)</script>");
	   		}
	   		else
			{
			    	res.write("<script> alert('Your Password is \""+out+"\"');  setTimeout(function () {         window.location = '/';  }, 50)</script>");
			}
	    		res.end();
		});
		});
	}

});

app.get("/logout", function(req, res) 
{
	res.clearCookie("cdir");
	res.clearCookie("uname");
	res.write("<script> alert('Successfully Logged out');  setTimeout(function () {         window.location = '/';  }, 50)</script>");
	res.end();
});

app.post("/signup", function(req, res) 
{
	if(req.body.upass==req.body.cupass)
	{
		var exec = require('child_process').exec;
		mkDirByPathSync('login');
		var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" Client signup '+ req.body.uname+' '+ req.body.upass+' '+req.body.uphone+' '+req.body.umail+' > login/'+req.body.uname+'signup.txt',function(err,data,code)
		{
 			if (err instanceof Error)
				throw err;
			fs.readFile('login/'+req.body.uname+'signup.txt', function (err, data) 
		 	{
		    		if (err) throw err;
				var out=data.toString();
				if(out=="Successfully Registered")
		    		{
		    			res.write("<script> alert('User Registration Successful. Please Login using the credentials');  window.location = '/login.html'; </script>");
		   		}
		   		else if(out=="username already exists. Please try different username")
				{
				    	res.write("<script> alert('User name already exists. Please try a different name'); window.location = '/signup.html';</script>");
				}
				else
					res.write("<script> alert('Error Try again later.'); window.location = '/';</script>");
		    		res.end();
			});
		}
		);
	}
	else
	{
		res.write("<script> alert('Passwords dont match');  window.location = '/signup.html'; </script>");			
		res.end();
	}

});

app.get("/showlog.html",function(req,res)
{
	if(!(typeof req.cookies['cdir'] !== 'undefined' && req.cookies['cdir']))
     		res.sendFile(__dirname + "/login.html");
    	else
	{
		var exec = require('child_process').exec;
		mkDirByPathSync('login');
		var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" Client showlog '+ req.cookies['uname']+' > login/'+req.cookies['uname']+'log.txt',function(err,data,code)
		{
 			if (err instanceof Error)
				throw err;
			fs.readFile('login/'+req.cookies['uname']+'log.txt', function (err, data) 
		 	{
		    		if (err) throw err;
					var out=data.toString();
				if(out=="unable to fetch log")
		    		{
		    			res.write("<script> alert('Unable to fetch log');  setTimeout(function () {         window.location = '/';  }, 50)</script>");
		   		}
		   		else
				{
					res.write("<html><body>");
				    	var out=(data.toString()).split("###");
					var outl=out.length;
					for(var i=outl-1;i>=0;i--)
		    			{	    				
			    			res.write(out[i]+"<br/><br/>");
			   		}
					res.write("<br/><br/><a href='/'>Back to Home</a></body></html>");
				}
		    		res.end();
			});
		});
	}
});

app.post("/directory", function(req, res) 
{
	if(req.body.folder=="..")
	{
		var lindexof=(req.cookies['cdir']).lastIndexOf("/");
		if(lindexof!=-1)
		{
			res.cookie('cdir',(req.cookies['cdir']).substring(0,lindexof));
		}		
	}
	else
	{
		var str=req.body.folder;
		str = str.replace(/\s*$/,"");
		res.cookie('cdir',req.cookies['cdir']+'/'+str);
	}
	res.redirect('/download.html');
});
app.get('*', function(req, res) {
    res.redirect('/');
});

 app.listen(2000, function(a) {

     console.log("Listening to port 2000");

 });

