import java.io.*;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract class KeyValueObject{
    public String path = "Objects";

    public String generateKey(String value) throws Exception {
        Hash myHash = new Hash(value);
        return myHash.getSHA1();
    }
    
    public String getValue(String key){ //给定key的值，返回value
        String value = "";
        try{
            Path targetFilePath = Paths.get(path + File.separator + key);
            value = Files.readString(targetFilePath);
        }
        catch(FileNotFoundException ex){
            System.out.println("File does not exist.");
            ex.getStackTrace();
        }
        catch(IOException ex){
            ex.getStackTrace();
        }
        return value;
    }

    public String getValue(File file) throws Exception { //给定文件，得到文件内容
        String value = "";
        FileInputStream is = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int readLength;
        while((readLength=is.read(buffer))>0){
            value += new String(buffer,0,readLength);
        }
        is.close();
        return value;
    }

    //生成key-value文件
    public void generateFile(String key) {
        File file=new File(path, key);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //将value存入key文件
    public void putValueIntoFile(String key,String value) {
        try {
            File file = new File(path, key);
            FileWriter os = new FileWriter(file);
            os.write(value);
            os.flush();
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
