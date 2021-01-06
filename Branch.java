import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Calssname Branch
 * @Description Git分支结构，由git/HEAD文件中的路径确定当前所在分支
 *              由Branch中分支名对应文件记录该分支下最新commit
 * */
public class Branch {
    static String name;
    static final String upPath = KeyValueObject.root+File.separator+"git";
    static final String path = upPath+File.separator + "Branch";
    static final String pathOfObjects = upPath + File.separator + "Objects";

    /**构造函数1：第一次提交，只传入本次commit的key
     * 在git/Branch/下创建文件名为分支名，value为commitkey的文件
     * @param CommitKey
     */
    public Branch(String CommitKey){
        String BranchPath = readKey(new File((upPath+File.separator+"HEAD")));
        File file = new File(BranchPath);
        //System.out.println(BranchPath);
        Util.putValueIntoFile(this.path,file.getName(),CommitKey);
    }


    /**branch构造函数2:传入分支名称和当前最新的commitkey
     * 以此次commit为分叉点构造另一个分支
     * @param name
     * @param flag
     */
    public Branch(String name,boolean flag){

        String BranchPath = readKey(new File((upPath+File.separator+"HEAD")));
        //System.out.println(BranchPath);
        String CommitKey = readKey(new File(BranchPath));
        //System.out.println(CommitKey);
        Util.generateFile(this.path,name);
        Util.putValueIntoFile(this.path,name,CommitKey);
    }


    /**显示当前分支*/
    public static void CurrentBranch(){
        File file = new File(upPath+File.separator+"HEAD");
        System.out.println((new File(readKey(file))).getName());
    }

    /**显示所有分支*/
    public static void AllBranch(){
        File[] ls = (new File(path)).listFiles();
        for(File f : ls) System.out.println(f.getName());
    }

    /**切换分支,输入分支名
     * @param BranchName
     * @throws Exception
     */
    public static void SwitchBranch(String BranchName) throws Exception {
        File file = new File(path+File.separator+BranchName);
        String key = readKey(file);
        //回滚到分支的最新commit对应的tree的结构
        //得到tree的哈希值
        String treeKey=Util.getTargetValue("tree",pathOfObjects+ File.separator +key);
        Diffwriteback(treeKey,KeyValueObject.root);
        Util.putValueIntoFile(upPath,"HEAD",path+File.separator+BranchName);
    }

    /**创建并切换到新分支
     * @param name
     * @throws Exception
     */
    public static void NewSwitch(String name) throws Exception {
        Branch branch = new Branch(name,true);
        SwitchBranch(name);
    }

    //读取CommitKey
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

    /**合并分支
     * @param name1
     * @param name2
     * @throws Exception
     */
    public static void merge(String name1, String name2) throws Exception {
        SwitchBranch(name1);
        String key = readKey(new File(path+File.separator+name2));
        Diffwriteback(key,path);
    }

    //将对应tree目录下所有文件从Objects写回工作区
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

                    Util.putValueIntoFile(originpath,filename,contentOfFile);
                }
                else {
                    Util.generateFile(originpath,filename);
                    Util.putValueIntoFile(originpath,filename,contentOfFile);
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
                    dic.mkdir();
                }
                key=keyofnexttree;//更新递归的下一层key
                String originpath1=originpath+ File.separator +dicname;//更新路径
                writeback(keyofnexttree,originpath1);

            }

        }
        inputReader.close();
        bf.close();
    }
    /**
     删除不需要的文件函数
     将文件夹中包含的file用listfiles函数展示出来，再检查每个文件的名称是否出现在tree的value中，如果
     出现了，则不做处理，如果不存在，说明还原时不需要这个文件，则删除。
     */
    public static void deletenonexisting(String value,String path) throws IOException {
        File current=new File(path);
        File[] fs = current.listFiles();
        for(int i=0;i<fs.length;i++) {
            if(!fs[i].getPath().equals(upPath)) {
                String filename=fs[i].getName();
                if(!value.contains(filename)) {
                    Util.deleteFiles(fs[i]);
                }
            }
        }
    }

    //递归删除文件夹下除了git文件夹的所有文件
    public static boolean deleteFiles(String path) {
        File file= new File(path);
        if(file.getPath().equals(upPath)) {
            if (!file.exists()) {
                return false;
            }
            if (file.isFile()) {
                return file.delete();
            } else {
                for (File f : file.listFiles()) {
                    deleteFiles(path + File.separator + f.getName());
                }
            }
            return file.delete();
        }
        return true;
    }


    public static void Diffwriteback(String key,String originpath)throws Exception{
        File file = new File(pathOfObjects,key);
        InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
        BufferedReader bf = new BufferedReader(inputReader);
        /**将文件内容全部读取到str字符串中*/
        String str="";
        String strr="";
        strr=bf.readLine();
        while(strr!=null) {
            str+=strr+"\n";
            strr=bf.readLine();
        }
        /**首先比较工作区文件夹中的文件是否在需要还原的tree里，如果不在，则删除*/
        deletenonexisting(str,originpath);
        /**逐行读取str中内容*/
        String[] ss=str.split("\n");
        for (int i=0;i<ss.length;i++){
            if(ss[i].startsWith("100644 blob ")) {/**如果读入的hash是文件，则首先得到文件的哈希值，文件名和文件内容*/
                String keyofblob=ss[i].substring(12, 52);
                String filename=ss[i].substring(53,ss[i].length());
                String contentOfFile = Files.readString(Paths.get(pathOfObjects+ File.separator +keyofblob));
                File fl = new File(originpath+ File.separator +filename);
                if(fl.exists()) {/**与工作区文件比较，如果名称相同则进一步比较内容*/
                    /**diff myers算法产查看文件改变内容*/
                    System.out.println(fl.getName());
                    Diff.diff(contentOfFile,readKey(new File(originpath+File.separator+filename)));
                    Util.putValueIntoFile(originpath,filename,contentOfFile);
                }
                else {
                    Util.generateFile(originpath,filename);
                    Util.putValueIntoFile(originpath,filename,contentOfFile);
                }
            }
            if(ss[i].startsWith("040000 tree ")) {
                /**如果读入的hash是文件夹，则
                比较文件夹是否存在，不存在则创建文件夹，存在则直接进入，比较里面内容
                递归文件夹中内容，直到所有文件都写入*/
                String keyofnexttree=ss[i].substring(12,52);
                String dicname=ss[i].substring(53,ss[i].length());
                File dic = new File(originpath+ File.separator +dicname);
                if (!dic.exists()) {
                    dic.mkdir();
                }
                key=keyofnexttree;//更新递归的下一层key
                String originpath1=originpath+ File.separator +dicname;//更新路径
                writeback(keyofnexttree,originpath1);
            }
        }
        inputReader.close();
        bf.close();
    }

    //调用diff函数
    public static void fileDiff(File f1,File f2){
        String tpl = readKey(f1);
        String str = readKey(f2);
        Diff.diff(tpl,str);
    }


    //判断两个分支是否冲突
    static boolean Conflict(String name1,String name2) throws Exception {
        File f1 = new File(path+File.separator+name1);
        File f2 = new File(path+File.separator+name2);
        String CommitKey1 = readKey(f1);
        String CommitKey2 = readKey(f2);

        String tree1 = Util.getTargetValue("tree",path+File.separator+"Objects"+File.separator+CommitKey1);
        String tree2 = Util.getTargetValue("tree",path+File.separator+"Objects"+File.separator+CommitKey2);

        deleteFiles(upPath);
        Log.reset(CommitKey1);
        Log.reset(CommitKey2);
        return true;
    }

    //比较两个文件是否相同
    private void compareFile(String firFile, String secFile) {
        try {
            BufferedInputStream fir = new BufferedInputStream(new FileInputStream(firFile));
            BufferedInputStream sec = new BufferedInputStream(new FileInputStream(secFile));
            //比较文件的长度是否一样
            if (fir.available() == sec.available()) {
                while (fir.read() != -1 && sec.read() != -1) {
                    if (fir.read() != sec.read()) {
                        System.out.println("Files not same!");
                        break;
                    }
                }
                System.out.println("two files are same!");
            } else {
                System.out.println("two files are different!");
            }
            fir.close();
            sec.close();
            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取文件名
    private static String inputFileName() {
        BufferedReader buffRead = new BufferedReader(new InputStreamReader(System.in));
        String fileName = null;
        try {
            fileName = buffRead.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
