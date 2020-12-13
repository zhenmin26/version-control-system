import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class HEAD {
    private String keyOfCurCommit; //key of current commit
    public String path = "Objects";
    public String pathOfHEAD = "Objects" + File.separator + "HEAD";


    public HEAD(String keyOfCommit) throws IOException {
        this.keyOfCurCommit = keyOfCommit;
        //在Objects文件夹中生成HEAD文件，用来存放commit key
        (new File(path, "HEAD")).createNewFile();
        putValueIntoFile(keyOfCurCommit); //把最新的commit key放到HEAD文件里
    }

    public void setValue(String keyOfCommit){
        this.keyOfCurCommit = keyOfCommit;
        putValueIntoFile(keyOfCommit);
    }

    public String returnValue(){
        return keyOfCurCommit;
    }

    public void putValueIntoFile(String keyOfCommit){ //把commit key放入HEAD文件
        try {
            File file = new File(pathOfHEAD);
            FileWriter os = new FileWriter(file);
            os.write(keyOfCommit);
            os.flush();
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
