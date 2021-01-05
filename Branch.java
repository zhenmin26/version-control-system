import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
        FileInputStream in = new FileInputStream(file);
        head.keyOfCurCommit = "";
        int len = 0;
        byte[] buf = new byte[1024];
        while((len=in.read(buf))!=-1){
            head.keyOfCurCommit += new String(buf,0,len);
        }
        String key = head.keyOfCurCommit;
        String FilePath = upPath+File.separator+"Objects"+File.separator+"key";
        File temp = new File(FilePath);
        String tree = Util.getTargetValue("tree ",FilePath);
        String blob = Util.getTargetValue("blob ",FilePath);
        
    }

    //合并分支
    public static boolean merge(String name1, String name2){

        return true;
    }

}
