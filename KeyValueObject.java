import java.io.*;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @className: KeyValueObject
 * @description: blob, tree, commit的父类
 */
abstract class KeyValueObject{
    public static String root = "D:\\git1";
    /**
     * @methodName generateKey
     * @description given a value, return its hash code
     * @param value
     * @return String
     * @throws Exception
     */
    public String generateKey(String value) throws Exception {
        Hash myHash = new Hash(value);
        return myHash.getSHA1();
    }

    /**
     * @methodName getValue
     * @description given a key, return its value as a key-value object
     * @param path
     * @param key
     * @return String
     */
    public String getValue(String path, String key){ //给定key的值，返回value
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

    public static void setRoot(String root) {
        KeyValueObject.root = root;
    }
}
