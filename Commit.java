import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

//commit value:
//    1.根目录的hash
//    2.上一次commit的key

/* Sample
commit 56fe26ad9101d8a15e2d1cb46417fbe38a9a9041 (HEAD -> tast_01_v3, origin/tast_01_v3)
Author: 陆甄敏 <zhenmin0226@163.com>
Date:   Wed Dec 9 14:33:01 2020 +0800

    Change Blob1 to Blob

commit 80abf20f197563ea14ca29f2d6b0945c483b2682
Author: 陆甄敏 <zhenmin0226@163.com>
Date:   Wed Dec 9 14:25:14 2020 +0800

    修改Blob1
 */

public class Commit extends KeyValueObject{
    //数据域
    private String key;
    private String value;
    private String parent; //已有最新commit的key
    public static HEAD myHEAD; //HEAD
    public String path = "Objects";
    public String pathOfHEAD = path + File.separator + "HEAD";
    private String author;
    private String email;
    private String committer;
    private String dateCreated;


    //函数
    public Commit(String keyOfRoot) throws Exception {
        this.value = "";
        //如果HEAD文件不存在，说明是第一次commit
        if(!((new File(pathOfHEAD)).exists())){
            //commit value是根目录的tree key
            this.value += "tree " + keyOfRoot + "\n";
            //生成commit key
            this.key = generateKey(value);
            //生成HEAD()
            myHEAD = new HEAD(key);
        }
        else { //commit value是根目录的tree key
            this.value += "tree " + keyOfRoot + "\n";
            this.value += "parent " + getParent() + "\n";
            //生成commit key
            this.key = generateKey(value);
            //更新HEAD文件里的commit key
            myHEAD.setValue(key);
        }

        //生成commit的key-value文件
        generateFile(key);
        //向commit的key-value文件中写入value，value为tree key
        putValueIntoFile(key, value);
    }

    //获得已有最新commit key
    public String getParent() throws IOException {
        this.parent = Files.readString(Paths.get(pathOfHEAD)); //从HEAD中获得上一次commit的key
        return parent;
    }

    public String returnParent(){
        return parent;
    }

    public String returnKey(){
        return key;
    }

    public String returnValue(){
        return value;
    }


    public String getTargetValue(String target, String path){
        String targetValue = "";
        try {
            String contentOfFile = Files.readString(Paths.get(path));
            if(contentOfFile.matches(target + ".*")){
                //因为是以"target "的形式存储的，所以要取字串需要从target.length()+2开始，要该行的最后
                targetValue =  (contentOfFile.substring(target.length()+1)).trim(); //trim()去除"\n"符号
            }
            else{
                System.out.println("No " + target); //如果文件中没有找到目标值
            }
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

    @Override
    public String toString(){
        //返回commit到下面的格式
        return "commit " + key + "\n"
                + "parent " + parent + "\n"
                + "author " + author + "<" + email + ">" + "\n"
                + "date " + dateCreated + "\n";
/*   commit 80abf20f197563ea14ca29f2d6b0945c483b2682
     parent 80abf20f197563ea14ca29f2d6b0945c483b2682
     author username <email>
     date dateCreated
*/
    }
}




/*

    public Commit(String keyOfRoot) throws Exception { //keyOfTree是根目录的key值
        this.value = "";

        if(!(new File(HEAD).exists())){ //如果是第一次commit，则value仅是根目录的key
            this.value += "tree " + keyOfRoot;
        }
        else{ //如果不是第一次commit，则value是根目录的key和上一次commit的key
            //比较根目录的key有没有发生改变，如果改变了，就进行更新
            this.value += "tree " + keyOfRoot;
            this.value += "parent " + getParent();
        }
        this.key = generateKey(value); //生成commit的key
        generateFile(key, value); //生成commit对应的文件
        generateFile("HEAD", key); //将最新的commit的key放入HEAD文件

        //提交commit的账户和email
        this.author = getTargetValue("author",path);
        this.email = getTargetValue("email", path);
        //生成commit的时间
        this.dateCreated = (new Date()).toString();
    }

    //获取提交commit的用户的用户名
    public void getAuthor() throws Exception {
        try {
            Stream<String> lines = Files.lines(Paths.get(path));
            lines.forEach(ele -> {
                if(ele.matches("username.*")){
                    this.author = (ele.substring(9)).trim();
                }
                else{
                    System.out.println("No username");
                }
            });
        }
        catch(FileNotFoundException ex){
            //如果没有找到config文件，则说明进行配置
            new Config();
            ex.getStackTrace();
        }
        catch(IOException ex){
            ex.getStackTrace();
        }
    }

    //获取提交commit的用户的email
    public void getEmail(){
        try {
            Stream<String> lines = Files.lines(Paths.get(path));
            lines.forEach(ele -> {
                if(ele.matches("email.*")){
                    this.email = (ele.substring(8)).trim();
                }
                else{
                    System.out.println("No email");
                }
            });
        }
        catch(FileNotFoundException ex){
            //如果没有找到config文件，则说明进行配置
            new Config();
            ex.getStackTrace();
        }
        catch(IOException ex){
            ex.getStackTrace();
        }
    }
 */