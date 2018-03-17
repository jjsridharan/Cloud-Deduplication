# Cloud-Deduplication

Project to achieve de-duplication at file level to save more space.

This is a cloud project where you can upload your files to the server and 
the files are stored in a higher compression rate. The de-duplication algorithm
used is so advanced that it provides quicker upload of user data from 
the client into the server and ensures that minimum data is used in the 
uploading process. It uses lesser network data as the algorithm devised
is efficient and faster. This project is done entirely in Java Platform.

This application requires gson-2.6.2 support which is downloaded by default
in the package.
For Server side 
Install necessary things by executing 
sh startup.sh

To run the application, go to the corresponding folder and type in terminal
Compile:
javac -cp ".:gson-2.6.2.jar:commons.jar" *.javac
Run:
java -cp ".:gson-2.6.2.jar:commons.jar" Server

To start Node JS server type the following command
node server.js

For Client-Side
To run the application, go to the corresponding folder and type in terminal
Compile:
javac -cp ".:gson-2.6.2.jar:commons.jar" *.javac
Run:
java -cp ".:gson-2.6.2.jar:commons.jar" login