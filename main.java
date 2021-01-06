import java.io.File;

public class main {
    public static void main(String[] args) throws Exception {
        Util.Init();
        Commit c = new Commit("第1次提交");
        Commit c1 = new Commit("第2次提交");
        Commit c2 = new Commit("第3次提交");
        Commit c3 = new Commit("第4次提交");
        Branch b = new Branch("分支2",true);
        Branch b1 = new Branch("分支3",true);
        Branch.CurrentBranch();
        Branch.AllBranch();
        Branch.SwitchBranch("main");
        Branch.SwitchBranch("分支2");
        Branch.NewSwitch("分支2");
        Branch.CurrentBranch();
        Log.showlogs();
        Branch.merge("分支2","main");
    }
}
