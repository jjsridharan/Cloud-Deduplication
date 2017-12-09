import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;

public class SaveAttribute 
{
	public static void Save(String fname,String extension) throws Exception
	{
		Path path = Paths.get(fname);
		UserDefinedFileAttributeView view = Files.getFileAttributeView(path,UserDefinedFileAttributeView.class);
		view.write("extension", Charset.defaultCharset().encode(extension));
	}
}
