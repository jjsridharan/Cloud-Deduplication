import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;

public class Test1 {

  public static void main(String[] args) throws Exception {
    System.out.println(RetrieveAttribute.Retrieve("G:\\Cloud-Deduplication\\Chunklevel\\Test\\hel.src"));
  }

}
 