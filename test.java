import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class test {
    public String branch = "main";
    public String keyOfCurCommit; //key of current commit
    public static final String publicPath = KeyValueObject.root + File.separator + "git";
    public static final String pathOfHEAD = KeyValueObject.root + File.separator + "git" + File.separator + "HEAD";

    public static void main(String[] args) throws Exception {
        //KeyValueObject.setRoot("/Users/luzhenmin/Desktop/test");
//        Util.Init();
//        String path = "/Users/luzhenmin/Desktop/test";
//        new Commit("first commit");
//        new Commit("second commit");
//        Log.showlogs();
//        new Commit("third commit");
//        Log.showlogs();
        //Log.reset("288a9f386c83ed2db8fa9f5073e703380b1d075e");
        Branch.AllBranch();
    }
}
