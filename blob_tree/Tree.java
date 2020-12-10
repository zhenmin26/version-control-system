import java.io.File;

//tree value:
//    1.文件夹内的文件的名称+子文件夹的名称
//    2.每个子文件的blob的key
//    3.每个子文件夹tree的key

public class Tree extends KeyValueObject {
	//数据域
    private String key;
    private String value;
   //通过遍历在tree需要生成的文件内容中写入tree中包含的文件及文件夹的类型，哈希值及名称内容
    public Tree(File file) throws Exception {
        File[] fs = file.listFiles();
        value = "";
        for(int i=0; i<fs.length; i++){
            if(fs[i].isFile()){
                this.value += "100644 blob " + (new Blob(fs[i])).getKey() + " " + fs[i].getName() + "\n";
            }
            if(fs[i].isDirectory()){
                this.value += "040000 tree " + (new Tree(fs[i])).getKey() + " " + fs[i].getName() + "\n";
            }
        }
        this.key = generateKey(value);//对更新完的value生成哈希值
        generateFile(key, value);//在指定路径下生成文件
    }
    //访问哈希值
    public String getKey(){
        return key;
    }
    //访问value
    public String getValue(){
        return value;
    }

}