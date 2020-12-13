import java.io.FileInputStream;
import java.security.MessageDigest;
import java.io.File;

public class Hash{
    //数据域
    private String sha1;

    public Hash(String value) throws Exception {
        this.sha1 = toHex(SHA1Checksum(value));
    }

//    public Hash(File file) throws Exception{
//        this.sha1 = toHex(SHA1Checksum(file));
//    }

    //函数
    public String getSHA1(){
        return sha1;
    }

    //生成value的hash
    public static byte[] SHA1Checksum(String value) throws Exception{
        MessageDigest sha1code = MessageDigest.getInstance("sha1");
        sha1code.update(value.getBytes());
        byte[] complete = sha1code.digest();
        return complete;
    }

    //生成文件的hash
    public static byte[] SHA1Checksum(File file) throws Exception {
        byte[] buffer = new byte[1024];
        MessageDigest sha1code = MessageDigest.getInstance("sha1");
        int numRead = 0;
        FileInputStream is = new FileInputStream(file);
        do{
            numRead = is.read(buffer);
            if(numRead>0){
                sha1code.update(buffer, 0, numRead);
            }
        }while(numRead != -1);
        is.close();
        byte[] complete = sha1code.digest();
        return complete;
    }

    //将哈希值转化为十六进制
    public static String toHex(byte[] complete){
        String sha1 = "";
        for(int i=0; i<complete.length; i++){
            sha1 += Integer.toString((complete[i] & 0xff) + 0x100, 16).substring(1);
        }
        return sha1;
    }
}
