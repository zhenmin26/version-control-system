
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



public class test_commit {
	    
	@Before
	public void creatdirectory() throws Exception{
		creatd("/Users/mayining/Desktop","test");
		creatd("/Users/mayining/Desktop/test","testt");
		creatf("/Users/mayining/Desktop/test","test1","hello world!");
		creatf("/Users/mayining/Desktop/test/testt","test2","123");
		creatf("/Users/mayining/Desktop/test/testt","test3","456");
	}
	
	@Test
	//测试当head文件不存在，即第一次提交时的结果，如果能正确打印出当前tree，commit即上次交的哈希值，则通过测试
	public void firstcommit() throws Exception {
            Commit Commit1 = new Commit("this is the first commit");
            System.out.println("First Commit -- create File HEAD");
	}
	
	@Test
	//测试得到上次commit中原tree的哈希值，若能正确打印出，则通过测试
	public void checkgettargetvalue()throws Exception{
		String publicPath = KeyValueObject.root+File.separator+"git";
	    String pathOfHead = publicPath + File.separator + "HEAD";
		String head=Files.readString(Paths.get(pathOfHead));
        String branchname=head.substring(head.lastIndexOf("/")+1);
        String  keyOfParent = publicPath + File.separator + "Branch"+ File.separator + branchname;
        String keyOfParentRoot = Files.readString(Paths.get(keyOfParent));
        System.out.println(keyOfParentRoot);
	}
	
	
	@Test
	//测试改变原文件内容从而产生新的commit的函数，若能打印出此次tree的哈希值，commit哈希值和上次提交哈希值，则通过测试
	public void followingcommit() throws Exception{
		Util.deleteFiles("/Users/mayining/Desktop/test/test1");
    	Commit Commit1 = new Commit("delete file test1");
    	System.out.println(Commit1.toString());
	}
	
	
	@After
	
	//检查完毕后，删除生成的文件夹
	public void deletefiles()throws Exception{
		Util.deleteFiles("/Users/mayining/Desktop/test");
	}

	//生成文件夹函数
		public void creatd(String path, String name) {
			File test = new File(path, name);
		       if (test.mkdirs()) {
		           System.out.println("多级层文件夹创建成功！创建后的文件目录为：" + test.getPath() + ",上级文件为:" + test.getParent());
		       }
		}
		//生成文件函数
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
