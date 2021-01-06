import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Log {
    public static String publicPath = KeyValueObject.root+File.separator+"git";
    public static String pathOfObjects = publicPath + File.separator + "Objects";
    public static String path = publicPath+ File.separator+"logs";//存放log文件的路径

    public Log(String commit) throws Exception{
        if(!new File(path).exists()) {
            File file=new File(path);
            file.mkdir();//在第一次commit的时候新建logs文件夹
        }
        String head=Files.readString(Paths.get(publicPath+ File.separator+"HEAD"));
        String branchname=head.substring(head.lastIndexOf(File.separator)+1);//找到当前所在branch的名字
        if(!((new File(path+File.separator+branchname)).exists())){
            Util.generateFile(path,branchname);
            Util.putValueIntoFile(path,branchname,commit);
            //该分支的第一次commit时，创建文件，并将commit需要存储的内容写入
        }
        else {
            Util.putValueIntoFile(path,branchname,commit);//以后的提交以覆盖的形式修改logs文件
        }
    }

    /*
     在回滚之后修改logs对应文件中内容为目标commit的内容
     只保存commit的key并读取commit文件中除了tree和parent部分的内容
     */
    public static void changelog(String targetcommit)throws Exception{
        String head=Files.readString(Paths.get(publicPath+ File.separator+"HEAD"));
        String branchname=head.substring(head.lastIndexOf(File.separator)+1);
        FileReader fileReader = new FileReader(pathOfObjects+File.separator+targetcommit);//读入需要返回的commit文件中内容
        BufferedReader in = new BufferedReader(fileReader);
        String line = in.readLine();
        String value="commit "+targetcommit+"\n";//value用来保存需要写入logs文件的信息
        while(line!=null) {
            if(!line.startsWith("tree")&&(!line.startsWith("parent"))) {//logs文件中不需要tree和parent的内容，其他部分写入
                value+=line+"\n";
            }
            line=in.readLine();
        }
        in.close();
        fileReader.close();
        Util.putValueIntoFile(path,branchname,value);
    }

    /*
    首先找到当前分支所在的最后一次commit
    找到对应commit的内容并存储
    迭代从commit内容中parent的信息找到上一次commit直到该分支的第一次commit
    将每个commit的内容打印出来
    */
    public static void showlogs() throws Exception {
        String head=Files.readString(Paths.get(publicPath+ File.separator+"HEAD"));
        String branchname=head.substring(head.lastIndexOf(File.separator)+1);//
        String curcommit=Files.readString(Paths.get(publicPath+ File.separator+"Branch"+ File.separator+branchname));
        String content="";
        while(!curcommit.equals("")) {
            content+="commit "+curcommit+"\n";
            content+="author "+Util.getTargetValue("author", pathOfObjects+ File.separator+curcommit)+"\n";
            content+="date "+Util.getTargetValue("date", pathOfObjects+ File.separator+curcommit)+"\n";
            content+=Util.getTargetValue("description",pathOfObjects+ File.separator+curcommit)+"\r\n";
            String value=Files.readString(Paths.get(pathOfObjects+ File.separator+curcommit));
            if(value.contains("parent")) {
                String nextcommit=Util.getTargetValue("parent",pathOfObjects+ File.separator+curcommit);
                curcommit=nextcommit;
            }
            else curcommit="";
        }
        System.out.println(content);
    }
    /*回滚函数：给定希望回滚到的commit，
    1.找到commit中tree的内容，
    2.在工作路径中还原commit中的文件，首先判断工作路径中是否有想还原中没有的文件，有的话就删掉；
    然后逐行读取tree中文件的hash值，还原时首先与现有文件名比较，若名字相同，则比较文件内容，文件内容相同，文件保留，不同则删除现有的
    增加需要还原的。若文件名与现存工作区的不同，则直接补充上。
   3.更新HEAD文件和logs文件
   */

    public static void reset(String commitkey,String originpath)throws Exception {
        String keyoftree=Util.getTargetValue("tree",pathOfObjects+ File.separator +commitkey);//得到tree的哈希值
        writeback(keyoftree,originpath);//调用辅助函数负责还原文件
        new HEAD(commitkey);//更新HEAD指针
        changelog(commitkey);//更新log文件，删除需要回滚的commit后面的commit
    }

    public static void reset(String commitkey)throws Exception {
        String keyoftree=Util.getTargetValue("tree",pathOfObjects+ File.separator +commitkey);//得到tree的哈希值
        String originpath=KeyValueObject.root;
        writeback(keyoftree,originpath);//调用辅助函数负责还原文件
        new HEAD(commitkey);//更新HEAD指针
        Log.changelog(commitkey);//更新log文件，删除需要回滚的commit后面的commit
    }

    /*
     写回函数负责根据提供的tree的哈希值逐行扫描内容，如果是文件，则与工作区文件比较，再进行还原，如果是文件夹，则递归调用
     函数进行内部文件的比较还原。
     */
    public static void writeback(String key,String originpath)throws Exception{
        File file = new File(pathOfObjects,key);
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
        String[] ss=str.split("\n");
        for (int i=0;i<ss.length;i++){
            if(ss[i].startsWith("100644 blob ")) {//如果读入的hash是文件，则首先得到文件的哈希值，文件名和文件内容
                String keyofblob=ss[i].substring(12, 52);
                String filename=ss[i].substring(53,ss[i].length());
                String contentOfFile = Files.readString(Paths.get(pathOfObjects+ File.separator +keyofblob));
                File fl = new File(originpath+ File.separator +filename);
                if(fl.exists()) {//与工作区文件比较，如果名称相同则进一步比较内容
                    String origincontent=readKey(new File(originpath+ File.separator +filename));
                    //String origincontent=Files.readString(Paths.get(originpath+ File.separator +filename));
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
                String originpath1=originpath+ File.separator +dicname;//更新路径
                writeback(key,originpath1);

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
            if(!fs[i].getPath().equals(publicPath)) {
                String filename=fs[i].getName();
                if(!value.contains(filename)) {
                    Util.deleteFiles(fs[i]);
                }
            }
        }
    }
    public static String readKey(File file){
        String result = "";
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){
                result += s;
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
}