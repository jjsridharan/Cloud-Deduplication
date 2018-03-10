 var express = require('express');

 var multer = require('multer');

 var bodyParser = require('body-parser');
 
var cookieParser = require('cookie-parser')
 var fs = require('fs');
 const path = require('path');
var mime = require('mime');
 var app = express();

app.use(cookieParser());
  app.use(bodyParser.urlencoded({ extended: false }))
  app.use(bodyParser.json());
  function mkDirByPathSync(targetDir, {isRelativeToScript = false} = {}) {
  const sep = path.sep;
  const initDir = path.isAbsolute(targetDir) ? sep : '';
  const baseDir = isRelativeToScript ? __dirname : '.';

  targetDir.split(sep).reduce((parentDir, childDir) => {
    const curDir = path.resolve(baseDir, parentDir, childDir);
    try {
      fs.mkdirSync(curDir);
      console.log(`Directory ${curDir} created!`);
    } catch (err) {
      if (err.code !== 'EEXIST') {
        throw err;
      }

      console.log(`Directory ${curDir} already exists!`);
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
   
     filename: function(req, file, callback) {

         callback(null, file.originalname);

     }

 });

 var upload = multer({

     storage: Storage

 }).array("imgUploader", 3); //Field name and max count
 var login=function(){return null;};
 app.get("/download.html", function(req, res) {

var exec = require('child_process').execSync;
var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" ListFiles '+req.cookies['cdir']+' > 1.txt');
 fs.readFile("1.txt", function (err, data) 
 {
    if (err) throw err;
    var out=(data.toString()).split("###");
    var outl=out.length;
    console.log(outl);
    res.write("<html><head><script src='https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js'></script><script> $(document).ready(function(){$('button').on('click',function(){var val=$(this).val(); var con=val.split('###');if(con[1]=='true'){document.getElementById('dname').value=con[0]; $('#dir').submit();alert('directory');}else{document.getElementById('fname').value=con[0];$('#down').submit();alert('download');}});});</script></head><body><form id='down'action= '/download' method='POST'><input type='text' id='fname' name='fname' hidden/></form><form action='/directory' method='POST'id='dir'><input type='text' name='dname' id='dname' hidden/></form>");
    for(var i=0;i<outl-1;i+=2)
    {
    	res.write("<button value=\""+out[i]+"###"+out[i+1]+"\">button</button>");
    }
    res.write("</body></html>");
    res.end();
});

		
 });
 app.get("/sample.html", function(req, res) 
 {
     res.sendFile(__dirname + "/sample.html");

 });
 app.get("/login.html", function(req, res) 
 {
     res.sendFile(__dirname + "/login.html");

 });
 app.get("/", function(req, res) {

     console.log(req.cookies['cdir']);
     res.sendFile(__dirname + "/index.html");

 });
   app.get("/upload.html", function(req, res) {

     res.sendFile(__dirname + "/upload.html");

 });
 app.post("/api/Upload", function(req, res) {
    
     
     upload(req, res, function(err)
     {
         if (err)
         {

             return res.end("Something went wrong!");

         }
         else
         {
	
     console.log("");
             var exec = require('child_process').execSync;
var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" Upload /home/student/Cloud-Deduplication/Chunklevel/'+req.cookies['cdir'],
  function (error, stdout, stderr){
    console.log('Output -> ' + stdout);
    if(error !== null){
      console.log("Error -> "+error);
    }
});
return res.end("File uploaded sucessfully!.");
         }
         

     });

 });
 
app.post("/download", function(req, res) 
{
	console.log(req.body.fname);
	var fname=req.body.fname;
	fname=fname.substring(fname.lastIndexOf("/")+1);
 var exec = require('child_process').execSync;
var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" DownloadFiles '+req.cookies['cdir']+' '+fname);
   var file = __dirname +'/'+ req.cookies['cdir']+'/'+fname; 
  

 
    var fileName = fname; // The default name the browser will use

    res.download(file, fileName);
	
});

app.post("/login", function(req, res) 
{
var exec = require('child_process').execSync;
var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" Client login'+ req.body.uname+' '+ req.body.upass+' > 1.txt');
console.log("Hi");
 fs.readFile("1.txt", function (err, data) 
 {
    if (err) throw err;
    var out=data.toString();
    console.log(out=="1");
    if(!(out=="0"))
    {
    	res.cookie('cdir',out);
    	res.write("<script> alert('Login Successful');  setTimeout(function () {         window.location = '/';  }, 50)</script>");
    }
    else
    	res.write("<script> alert('Login Failed');  setTimeout(function () {         window.location = '/';  }, 50)</script>");
    res.end();
});

});
app.post("/directory", function(req, res) 
{
	res.sendFile(__dirname + "/sample.html");

});

 app.listen(2000, function(a) {

     console.log("Listening to port 2000");

 });


