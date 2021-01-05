import java.io.*;

/**
 * @calssname Branch
 * @description Git分支结构，由head文件中的路径确定当前所在分支，由Branch中分支名对应文件记录最新commit
 * */
public class Branch {
    static String name;
    static final String upPath = KeyValueObject.root+File.separator+"git";
    static final String path = upPath+File.separator + "Branch";
    static HEAD head;
    static String LastCommit;

    //构造函数1：第一次提交，只传入本次commit的key，在git/HEAD下创建文件名为分支名，value为commitkey的文件
    public Branch(String CommitKey){
        this.LastCommit = CommitKey;
        Util.generateFile(this.path,"main");
        Util.putValueIntoFile(this.path,"main",CommitKey);
    }


    //branch构造函数2:传入分支名称和当前最新的commitkey，以此次commit为交叉点构造另一个分支
    public Branch(String name, String CommitKey){
        this.LastCommit = CommitKey;
        Util.generateFile(this.path,name);
        Util.putValueIntoFile(this.path,name,CommitKey);
        this.head.keyOfCurCommit = CommitKey;
    }

    //显示当前分支
    public static void CurrentBranch(){
        File file = new File(HEAD.pathOfHEAD);
        Util.getTargetValue("Branch/",path);
        System.out.println();
    }

    //显示所有分支
    public static void AllBranch(){
        File[] ls = (new File(path)).listFiles();
        for(File f : ls) System.out.println(f.getName());
    }

    //切换分支,输入分支名
    public static void SwitchBranch(String BranchName) throws Exception {
        File file = new File(path+File.separator+BranchName);
        head.keyOfCurCommit = readKey(file);
        Util.putValueIntoFile(upPath,"HEAD",path+File.separator+BranchName);
        String key = head.keyOfCurCommit;
        //删除文件夹下的除git文件夹的所有内容
        deleteFiles(upPath);
//        String FilePath = upPath+File.separator+"Objects"+File.separator+"key";
//        File temp = new File(FilePath);
//        String tree = Util.getTargetValue("tree ",FilePath);
//        String blob = Util.getTargetValue("blob ",FilePath);
        //回滚到分支的最新commit对应的tree的结构
        Commit.reset(key);
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

    //递归删除文件夹下除了git文件夹的所有文件
    public static boolean deleteFiles(String path) {
        File file= new File(path);
        if(file.getPath() != upPath) {
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

    //合并分支
    public static boolean merge(String name1, String name2) throws Exception {
        if(Conflict(name1,name2)) return false;
        else {
            File f1 = new File(path + File.separator + name1);
            File f2 = new File(path + File.separator + name2);
            String CommitKey1 = readKey(f1);
            String CommitKey2 = readKey(f2);

            String tree1 = Util.getTargetValue("tree", path + File.separator + "Objects" + File.separator + CommitKey1);
            String tree2 = Util.getTargetValue("tree", path + File.separator + "Objects" + File.separator + CommitKey2);

            deleteFiles(upPath);
            Commit.reset(CommitKey1);
            Commit.reset(CommitKey2);
            return true;
        }
    }

    static boolean Conflict(String name1,String name2) throws Exception {
        File f1 = new File(path+File.separator+name1);
        File f2 = new File(path+File.separator+name2);
        String CommitKey1 = readKey(f1);
        String CommitKey2 = readKey(f2);

        String tree1 = Util.getTargetValue("tree",path+File.separator+"Objects"+File.separator+CommitKey1);
        String tree2 = Util.getTargetValue("tree",path+File.separator+"Objects"+File.separator+CommitKey2);

        deleteFiles(upPath);
        Commit.reset(CommitKey1);
        Commit.reset(CommitKey2);
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
