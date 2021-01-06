import java.io.File;
import java.util.Scanner;

public class Command {
    public static String publicPath = "git";

    /**
     * @description 从命令行传入命令语句，判断命令语句并执行相应的命令
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
//        for(int i=0; i<args.length; i++) {
//            System.out.println(args[i]);
//        }
        //System.out.println("Enter \"git help\" for more information...");
        //判断命令是否合理
        if(args.length<2){
            System.out.println("Invalid command");
        }
        //匹配并调用相应的命令块
        else{
            if (!args[0].equals("git")) {
                System.out.println("Invalid command");
            } else {
                switch (args[1]) {
                    case "init":
                        Util.Init();
                        break;

                    case "commit":
                        doCommit(args);
                        break;

                    case "branch", "checkout":
                        doBranch(args);
                        break;
                    case "log", "reset":
                        doReset(args);
                        break;
                    case "merge":
                        doMerge(args);
                        break;
                    default:
                        System.out.println("Invalid command");
                }
            }
        }
    }

    /**
     * @description 执行git commit
     * @param args
     * @throws Exception
     */
    public static void doCommit(String[] args) throws Exception {
        if(args.length != 3){
            System.out.print("More information about this commit is requires");
        }else{
            new Commit(args[2]);
        }
    }

    /**
     * @description 执行branch相关的命令语句
     * @param args
     * @throws Exception
     */
    public static void doBranch(String[] args) throws Exception {
        if(args.length == 2 && args[1].equals("branch")){ //git branch
            Branch.AllBranch();
        }
        if(args.length == 3){
            if(args[1].equals("branch")){ //git branch branchName
                new Branch(args[2], true);
            }
            if(args[1].equals("checkout")){ //git checkout branchName
                Branch.SwitchBranch(args[2]);
            }
        }
        if(args.length == 4) { //git checkout -b branchName
            if (args[1].equals("checkout") && args[2].equals("-b")){
                Branch.NewSwitch(args[3]);
            }
        }
    }

    /**
     * @description 执行reset相关的命令语句
     * @param args
     * @throws Exception
     */
    public static void doReset(String[] args) throws Exception {
        if(args[1].equals("log")){
            //log接口
            Log.showlogs();
        }
        if(args[1].equals("reset")){
            //reset接口
            Log.reset(args[2]);
        }
    }

    public static void doMerge(String[] args) throws Exception {
        if(args[1].equals("merge")){
            Branch.merge(args[2], args[3]);
        }
    }
}
