import java.io.File;

/**
 * @className Blob
 * @description class Blob designed for files
 */
public class Blob extends KeyValueObject{
    public String publicPath = KeyValueObject.root+File.separator+"git";
    public String pathOfObjects = publicPath + File.separator + "Objects";
    private String key;
    private String value;

    /**
     * @description 给定文件路径，生成blob对象，即key-value文件，并将其存入对应存储路径
     * @param path
     * @throws Exception
     */
    public Blob(String path) throws Exception {
        this.value = Util.getValue(new File(path)); //获取blob value，即文件的文件内容
        this.key = generateKey(value); //对blob value生成blob key
        Util.generateFile(pathOfObjects, key); //生成key-value文件
        Util.putValueIntoFile(pathOfObjects, key, value); //向key-value文件中传入value
    }

    //返回blob key
    public String returnKey(){
        return key;
    }

    //返回blob value
    public String returnValue(){
        return value;
    }
}
