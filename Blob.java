import java.io.File;

public class Blob extends KeyValueObject{
    private String key;
    private String value;

    public Blob(String path) throws Exception {
        this.value = getValue(new File(path)); //获取blob value，即文件的文件内容
        this.key = generateKey(value); //对blob value生成blob key
        generateFile(key); //生成key-value文件
        putValueIntoFile(key, value); //向key-value文件中传入value
    }

    public String returnKey(){
        return key;
    } //返回key

    public String returnValue(){
        return value;
    } //返回value
}
