import java.io.File;

public class Blob extends KeyValueObject{//blob类型继承自keyvalueobject父类
	//数据域
    private String key;
    private String value;

    public Blob(File file) throws Exception {
        this.key = generateKey(file);//调用对文件生成哈希值的函数生成此文件的key
        this.value = getValue(file);//调用返回文件内容的函数得到此文件的value
        generateFile(key, value);//在指定路径下生成以key命名，内容为value的文件
    }
    //访问文件哈希值
    public String getKey(){
        return key;
    }
    //访问文件内容
    public String getValue(){
        return value;
    }
}