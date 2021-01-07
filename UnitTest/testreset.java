

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;


public class testreset {
	//增加新的commit并展示commit的历史
    @Before	
	public void addcommit()throws Exception{
		creatd("/Users/mayining/Desktop/test","testtt");
        Commit Commit1 = new Commit("add testtt directory");
        Log.showlogs();
	}
	
    //增加新的commit
	@Before
	public void addsecondcommit()throws Exception{
		creatf("/Users/mayining/Desktop/test/testt","test4","hello yining!");
        Commit Commit1 = new Commit("add test4 into testtt");             
	}
	@Test
	public void reset_test() throws Exception{
		System.out.println("请输入需要回滚到的commit的key");
		Scanner input=new Scanner(System.in);
		String targetcommit=input.next();//读取输入返回的commit
		Log.reset(targetcommit,"/Users/mayining/Desktop/test");
	}
	
	@After
	public void show2()throws Exception{
		Log.showlogs();		
	}
	
	//根据关键字找寻上次提交中tree的哈希值函数
	public String getTargetValue(String target, String path){
        String targetValue = "";
        try {
            String contentOfFile = Files.readString(Paths.get(path));
            int index = contentOfFile.indexOf(target);
            targetValue = contentOfFile.substring(index + target.length() + 1, index + target.length() + 1 + 40);
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
	public void creatd(String path, String name) {
		File test = new File(path, name);
	       if (test.mkdirs()) {
	           System.out.println("多级层文件夹创建成功！创建后的文件目录为：" + test.getPath() + ",上级文件为:" + test.getParent());
	       }
	}
	public void creatf(String path, String name,String content) {
		File test1 = new File(path,name);
	    try {
	        if (test1.createNewFile()) {
	            System.out.println("多级层文件夹下文件创建成功！创建了的文件为:" + test1.getAbsolutePath() + ",上级文件为:" + test1.getParent());
	            FileWriter os = new FileWriter(test1);
	            os.write(content);
	            os.flush();
	            os.close();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        }
	    }
		
}