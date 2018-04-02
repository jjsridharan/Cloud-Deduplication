git clone https://github.com/sridharan99/Cloud-Deduplication/tree/Testing
cd Cloud-Deduplication/Chunklevel/
git checkout Testing
javac -cp ".:commons.jar:gson-2.6.2.jar" *.java
sudo gnome-terminal -e 'sh -c "sh startup.sh;"'
java -cp ".:commons.jar:gson-2.6.2.jar" Server
