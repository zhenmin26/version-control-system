import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
    /*回滚函数：给定希望回滚到的commit，
    1.找到commit中tree的内容，
    2.在工作路径中还原commit中的文件，首先判断工作路径中是否有想还原中没有的文件，有的话就删掉；
    然后逐行读取tree中文件的hash值，还原时首先与现有文件名比较，若名字相同，则比较文件内容，文件内容相同，文件保留，不同则删除现有的
    增加需要还原的。若文件名与现存工作区的不同，则直接补充上。
   3.更新HEAD文件和logs文件
   */
    public static void reset(String commitkey,String originpath)throws Exception {
        String keyoftree=Util.getTargetValue("tree","Objects"+ File.separator +commitkey);//得到tree的哈希值
        writeback(keyoftree,originpath);//调用辅助函数负责还原文件
        new HEAD(commitkey);//更新HEAD指针
        Log.changelog(commitkey);//更新log文件，删除需要回滚的commit后面的commit
    }

    public static void reset(String commitkey)throws Exception {
        String keyoftree=Util.getTargetValue("tree","Objects"+ File.separator +commitkey);//得到tree的哈希值
        String originpath="/Users/mayining/Desktop/test";
        writeback(keyoftree,originpath);//调用辅助函数负责还原文件
        new HEAD(commitkey);//更新HEAD指针
        Log.changelog(commitkey);//更新log文件，删除需要回滚的commit后面的commit
    }
    /*
     写回函数负责根据提供的tree的哈希值逐行扫描内容，如果是文件，则与工作区文件比较，再进行还原，如果是文件夹，则递归调用
     函数进行内部文件的比较还原。
     */
    public static void writeback(String key,String originpath)throws Exception{
        File file = new File("Objects",key);
        InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
        BufferedReader bf = new BufferedReader(inputReader);
        // 将文件内容全部读取到str字符串中
        String str="";
        String strr="";
        strr=bf.readLine();
        while(strr!=null) {
            str+=strr+"\n";
            strr=bf.readLine();
        }
        //首先比较工作区文件夹中的文件是否在需要还原的tree里，如果不在，则删除
        deletenonexisting(str,originpath);
        //逐行读取str中内容
        String []ss=str.split("\n");
        for (int i=0;i<ss.length;i++){
            if(ss[i].startsWith("100644 blob ")) {//如果读入的hash是文件，则首先得到文件的哈希值，文件名和文件内容
                String keyofblob=ss[i].substring(12, 52);
                String filename=ss[i].substring(53,ss[i].length());
                String contentOfFile = Files.readString(Paths.get("Objects"+ File.separator +keyofblob));
                File fl = new File(originpath+ File.separator +filename);
                if(fl.exists()) {//与工作区文件比较，如果名称相同则进一步比较内容
                    String origincontent=Files.readString(Paths.get(originpath+ File.separator +filename));
                    if(contentOfFile.equals(origincontent)) {
                        return;//内容相同，文件不做处理，直接返回
                    }
                    else {
                        FileWriter os = new FileWriter(fl);
                        os.write(contentOfFile);
                        os.flush();
                        os.close();//文件内容不同，覆盖写入文件
                    }
                }
                else {
                    FileWriter os = new FileWriter(fl);
                    os.write(contentOfFile);
                    os.flush();
                    os.close();//文件不存在，直接写入文件
                }
            }

            if(ss[i].startsWith("040000 tree ")) {
                //如果读入的hash是文件夹，则
                //比较文件夹是否存在，不存在则创建文件夹，存在则直接进入，比较里面内容
                //递归文件夹中内容，直到所有文件都写入
                String keyofnexttree=ss[i].substring(12,52);
                String dicname=ss[i].substring(53,ss[i].length());
                File dic = new File(originpath+ File.separator +dicname);
                if (!dic.exists()) {
                    //deletefiles(originpath+ File.separator +dicname);
                    Path path = Paths.get(originpath+ File.separator +dicname);
                    Path pathCreate = Files.createDirectory(path);//文件夹不存在，创建文件夹
                }
                key=keyofnexttree;//更新递归的下一层key
                originpath=originpath+ File.separator +dicname;//更新路径
                writeback(key,originpath);

            }

        }
        inputReader.close();
        bf.close();
    }
    /*
     删除不需要的文件函数
     将文件夹中包含的file用listfiles函数展示出来，再检查每个文件的名称是否出现在tree的value中，如果
     出现了，则不做处理，如果不存在，说明还原时不需要这个文件，则删除。
     */
    public static void deletenonexisting(String value,String path) throws IOException {
        File current=new File(path);
        File[] fs = current.listFiles();
        for(int i=0;i<fs.length;i++) {
            String filename=fs[i].getName();
            if(!value.contains(filename)) {
                deletefiles(fs[i]);
            }
        }
    }

    //递归删除文件夹
    public static boolean deletefiles(String path) {
        File file= new File(path);
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        } else {
            for (File f : file.listFiles()) {
                deletefiles(path+File.separator + f.getName());
            }
        }
        return file.delete();
    }

    public static boolean deletefiles(File file) {
        if (!file.exists()) {
            System.out.println("file does not exist");
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        } else {
            for (File f : file.listFiles()) {
                deletefiles(KeyValueObject.root+File.separator + f.getName());
            }
        }
        return file.delete();
    }
}