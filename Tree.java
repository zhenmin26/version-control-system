import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//tree value:
//    1.文件夹内的文件的名称+子文件夹的名称
//    2.每个子文件的blob的key
//    3.每个子文件夹tree的key

public class Tree extends KeyValueObject {
    private String key;
    private String value;

    public Tree(String path) throws Exception {
        File file = new File(path);
        File[] fs = file.listFiles();
        value = "";
        //获得tree value
        for(int i=0; i<fs.length; i++){
            if(fs[i].isFile()){
                this.value += "100644 blob " + (new Blob(fs[i].getPath())).returnKey() + " " + fs[i].getName() + "\n";
            }
            if(fs[i].isDirectory()){
                this.value += "040000 tree " + (new Tree(fs[i].getPath())).returnKey() + " " + fs[i].getName() + "\n";
            }
        }
        this.key = generateKey(value); //生成tree key
        generateFile(key); //生成key-value文件
        putValueIntoFile(key, value); //将value写入key-value文件
    }

    //返回tree value
    public String returnKey() { return key; }

    //返回tree key
    public String returnValue() { return value; }
}




