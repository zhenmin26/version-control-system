
import java.io.*;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract class KeyValueObject{
    public String path = "Objects";//定义生成文件的存储地址
    //生成hash值的函数
    public String generateKey(File file) throws Exception {
        Hash myHash = new Hash(file);//调用hash类对文件生成hash值
        return myHash.getSHA1();
    }
    
    //生成hash值方法重载，若输入为一个字符串，则对字符串进行生成hash值的操作
    public String generateKey(String value) throws Exception {
        Hash myHash = new Hash(value);
        return myHash.getSHA1();
    }
    
    //查找hash值对应文件value的函数
    public String getValue(String key) throws IOException {
        String value = "";
        Path targetFilePath = Paths.get(path + File.separator + key);
        value = Files.readString(targetFilePath);
        return value;
    }
    //查找文件所对应文件value的函数
    public String getValue(File file) throws Exception {
        FileInputStream is = new FileInputStream(file);
        String value = "";
        int numRead;
        byte[] buffer = new byte[1024];
        do{
            numRead = is.read(buffer);
            value += new String(buffer);
        }while(numRead != -1);
        is.close();

        return value;
    }

    //生成key-value文件
    public void generateFile(String key, String value){
        try {
            File file = new File(path, key);
            file.createNewFile();
            FileWriter os = new FileWriter(file);
            os.write(value);
            os.flush();
            os.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
}

