import java.io.*;
import java.lang.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class Blob{
    //数据域
    //1. key
    private String key;
    //2. 存放所有生成文件的仓库的路径
    private String sharedpath="/Users/luzhenmin/Desktop/test";

    //方法
    //1. 生成blob create
    public Blob(String value) throws Exception{
        //生成vlue对应key
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        sha1.update(value.getBytes());
        byte[] result = sha1.digest();

        //对key进行赋值
        key="";

        //buye[] result转化为string
        for(int i=0; i<result.length; i++){
            key += Integer.toString((result[i]&0xff)+0x100,16).substring(1);
        }

        //生成文件
        try {
            File file = new File(sharedpath, key);
            if(!file.exists()) {
                file.createNewFile();
                System.out.println("Blob created!");
                System.out.println("Key is " + key);
            }
            else{
                System.out.println("blob already exits");
            }
            FileWriter os = new FileWriter(file);
            os.write(value);
            os.flush();
            os.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }

    }

    //2. 返回value值 getValue
    public void getValue(String key){
    	String targetFilePath = sharedpath + File.separator + key;
    	File targetFile = new File(targetFilePath);
    	FileInputStream is =null;
    	String value="";
    	try{
    		is=new FileInputStream(targetFile);
    	    String encoding="GBK";
    	    int numread=0;
    	    int size=is.available();
            byte buff[] = new byte[size];
            int length = is.read(buff);
            value = new String(buff,encoding);
        } catch (IOException ex) {
        	ex.printStackTrace();
           }finally {
        	   if (is!=null) {
        		   try {
        			   is.close();
                   } catch (IOException e) {
                	   e.printStackTrace();
                   }
        	   }
           }
    	 System.out.println("value of blob \"" + key +".txt\"" + " is " +value);
    }

    //3. 获得blob的key值
    public String getKey(){
        return key;
    }
}
