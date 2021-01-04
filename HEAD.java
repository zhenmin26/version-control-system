import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @className HEAD
 * @desription 指向commit的head类
 */
public class HEAD {
    private String keyOfCurCommit; //key of current commit
    public String publicPath = "git";
    public String pathOfHEAD = "git" + File.separator + "HEAD";

    /**
     * @description 传入head指向的commit的key，生成head对象
     * @param keyOfCommit
     * @throws IOException
     */
    public HEAD(String keyOfCommit) throws IOException {
        //if 1st commit, HEAD does not exist
        if(!((new File(pathOfHEAD)).exists())){
            (new File(publicPath, "HEAD")).createNewFile();
        }
        setValue(keyOfCommit);
    }

    /**
     * @description 向head文件中传入commit key
     * @param keyOfCommit
     */
    public void setValue(String keyOfCommit){
        this.keyOfCurCommit = keyOfCommit;
        Util.putValueIntoFile(publicPath, "HEAD", keyOfCommit);
    }

    //返回当前commit key
    public String returnValue(){
        return keyOfCurCommit;
    }
}
