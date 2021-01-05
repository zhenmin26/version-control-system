import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * @className: Commit
 * @description: class Commit designed for commits
 */
public class Commit extends KeyValueObject{
    //文件路径
    public String publicPath = KeyValueObject.root+File.separator+"git";
    public String pathOfObjects = publicPath + File.separator + "Objects";
    public String pathOfHEAD = publicPath + File.separator + "HEAD"; //current HEAD
    public String pathOfConfig = publicPath + File.separator + "Config"; //user info

    //commit value
    private String key;
    private String value;
    private String parent; //已有最新commit的key
    private String author;
    private String email;
    private String dateCreated;
    private String descOfCommit; //description of commit


    /**
     * @description 给定根目录的key和上一次commit的key，生成这一次commit的commit对象
     * @param descOfCommit
     * @throws Exception
     */
    public Commit(String descOfCommit) throws Exception {
        this.dateCreated = (new Date()).toString();
        this.descOfCommit = descOfCommit;

        String RootKEY = (new Tree(KeyValueObject.root)).returnKey();

        //判断文件Config是否存在，存在则读取author，email；否则调用Config创建Config文件
        if(!(new File(pathOfConfig).exists())) {
            new Config();
        }
        this.author = Util.getTargetValue("username", pathOfConfig); //在Config文件中，author对应的是username
        this.email = Util.getTargetValue("email", pathOfConfig);

        //initial commit value
        this.value = "";
        //set commit value
        this.value += "tree " + RootKEY + "\n";
        if(((new File(pathOfHEAD)).exists())){ //通过是否存在HEAD文件来判断，是否是第一次commit
            //如果HEAD文件存在，说明是不是第一次commit，则commit value要加入parent commit key
            this.value += "parent " + getParent() + "\n";
        }
        this.value += "author " + author + "\n";
        this.value += "email " + email + "\n";
        this.value += "date " + dateCreated + "\n";
        this.value += "description " + descOfCommit + "\n";

        //生成commit key
        this.key = generateKey(value);
        //生成HEAD()
        new HEAD(key); //在HEAD构造方法例，如果是第一次commit，会产生HEAD文件，并存入value
        //生成commit的key-value文件
        Util.generateFile(pathOfObjects, key);
        //向commit的key-value文件中写入value，value为tree key
        Util.putValueIntoFile(pathOfObjects, key, value);
    }

    /**
     * @description 获得已有最新commit key
     * @return
     * @throws IOException
     */
    public String getParent() throws IOException {
        this.parent = Files.readString(Paths.get(pathOfHEAD)); //从HEAD中获得上一次commit的key
        return parent;
    }

    //返回parent commit key
    public String returnParent(){
        return parent;
    }

    //返回commit key
    public String returnKey(){
        return key;
    }

    //返回commit value
    public String returnValue(){
        return value;
    }

    @Override
    public String toString(){
        //返回commit到下面的格式
        return "commit " + key + "\n"
                + "parent " + parent + "\n"
                + "author " + author + "<" + email + ">" + "\n"
                + "date " + dateCreated + "\n"
                + "description " + descOfCommit;
    }
}