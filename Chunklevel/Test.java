import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;

public class Test {

  public static void main(String[] args) throws Exception 
  {
    Path path = Paths.get("Test\\hel.jpg");
    UserDefinedFileAttributeView view = Files.getFileAttributeView(path,UserDefinedFileAttributeView.class);
    view.write("extension", Charset.defaultCharset().encode("mp3"));
    System.out.println("Publishable set");
  }
}
