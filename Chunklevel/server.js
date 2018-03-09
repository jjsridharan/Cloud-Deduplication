 var express = require('express');

 var multer = require('multer');

 var bodyParser = require('body-parser');
 
var cookieParser = require('cookie-parser')
 var fs = require('fs');
 const path = require('path');
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
var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" ListFiles > 1.txt');
 fs.readFile("1.txt", function (err, data) 
 {
    if (err) throw err;
    var out=(data.toString()).split("###");
    var outl=out.length;
    console.log(outl);
    for(var i=0;i<outl-1;i+=2)
    {
    	res.write("<button value=\""+out[i]+"###"+out[i+1]+"\">button</button>");
    }
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
     console.log(req.body);
         if (err)
         {

             return res.end("Something went wrong!");

         }
         else
         {
	
     console.log("");
             var exec = require('child_process').execSync;
var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" Upload /home/sridharan/Cloud-Deduplication/Chunklevel/'+req.cookies['cdir'],
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
	res.sendFile(__dirname + "/sample.html");

});

app.post("/login", function(req, res) 
{
var exec = require('child_process').execSync;
var child = exec('java -cp ".:commons.jar:gson-2.6.2.jar" Client login '+ req.body.uname+' '+ req.body.upass+' > 1.txt');
console.log("Hi");
 fs.readFile("1.txt", function (err, data) 
 {
    if (err) throw err;
    var out=data.toString();
    console.log(out=="1");
    if(!(out=="0"))
    {
    	res.cookie('cdir',out);
    	res.write("<script> alert('Login Successful');  setTimeout(function () {         window.location = '/index.html';  }, 2000)</script>");
    }
    else
    	res.write("<script> alert('Login Failed');  setTimeout(function () {         window.location = '/index.html';  }, 2000)</script>");
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


