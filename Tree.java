import java.io.File;

/**
 * @className Tree
 * @description class Tree designed for directories
 */
public class Tree extends KeyValueObject {
    public String publicPath = "git";
    public String pathOfObjects = publicPath + File.separator + "Objects";
    private String key;
    private String value;

    /**
     * @description 给定文件夹路径，生成tree对象
     * @param path
     * @throws Exception
     */
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
        Util.generateFile(pathOfObjects, key); //生成key-value文件
        Util.putValueIntoFile(pathOfObjects, key, value); //将value写入key-value文件
    }

    //返回tree key
    public String returnKey() { return key; }

    //返回tree value
    public String returnValue() { return value; }
}




