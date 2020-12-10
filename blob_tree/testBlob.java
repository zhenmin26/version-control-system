import java.util.Scanner;
import java.io.File;

public class testBlob {
    public static void main(String[] args) throws Exception {
        Blob myBlob = new Blob(new File("/Users/luzhenmin/Desktop/java项目/test/README.txt"));
        System.out.println(myBlob.getKey());
        System.out.println(myBlob.getValue());
    }
}