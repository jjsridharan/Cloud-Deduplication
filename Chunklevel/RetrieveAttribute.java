import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;

public class RetrieveAttribute 
{
	public static String Retrieve(String fname) throws Exception
	{
		Path path = Paths.get(fname);
		UserDefinedFileAttributeView view = Files.getFileAttributeView(path,UserDefinedFileAttributeView.class);
		String name = "extension";
		ByteBuffer buffer = ByteBuffer.allocate(view.size(name));
		view.read(name, buffer);
		buffer.flip();
		return Charset.defaultCharset().decode(buffer).toString();    
	}
}
 
