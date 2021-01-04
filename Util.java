import java.io.*;

/**
 * @className Util
 * @description 工具类
 */
public class Util {
    /**
     * @description 在指定路径下生成制定名称的文件
     * @param path
     * @param filename
     */
    public static void generateFile(String path, String filename) {
        File file=new File(path, filename);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @description 读取指定路径下的制定文件内容
     * @param path
     * @param fileName
     * @return String
     * @throws Exception
     */
    public static String getValue(String path, String fileName) throws Exception {
        File file = new File(path + File.separator + fileName);
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

    /**
     * @description 给定文件，返回文件内容
     * @param file
     * @return String
     * @throws Exception
     */
    public static String getValue(File file) throws Exception {
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

    /**
     * @description 给定文件路径及文件名，向文件写入内容
     * @param path
     * @param fileName
     * @param value
     */
    public static void putValueIntoFile(String path, String fileName,String value) {
        try{
            File file = new File(path,fileName);
            FileWriter os = new FileWriter(file);
            os.write(value);
            os.flush();
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @description 从制定文件中读取指定内容
     * @param target
     * @param path
     * @return
     */
    public static String getTargetValue(String target, String path){
        String targetValue = "";
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader in = new BufferedReader(fileReader);
            do{
                String line = in.readLine();
                //int index = line.indexOf(target);
                if(line.matches(target + ".*")) {
                    //targetValue = line.substring(index + target.length() + 1).trim();
                    targetValue = line.substring(target.length() + 1).trim();
                }
            }while(targetValue == "");
        }
        catch(FileNotFoundException ex){
            System.out.println("No such file -- " + (new File(path)).getName());
            ex.getStackTrace();
        }
        catch(IOException ex){
            System.out.println("Target value \"" + target + "\" not found");
            ex.getStackTrace();
        }
        return targetValue;
    }
}
