import java.lang.*;
import java.io.File;

public class testTree {
    public static void main(String args[]) throws Exception {
        String path = "/Users/luzhenmin/Desktop/java项目/test";
        Tree myTree = new Tree(new File(path));
        //System.out.println(myTree.getKey());
        //System.out.println(myTree.getValue());
        System.out.println(myTree.getValue("7016f5bebdd1acb22cd507c83ff1b9c5b7a72eac"));
    }
}
