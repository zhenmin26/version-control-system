package version_control;

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
	@Before
	public void show()throws Exception{
		System.out.println(Files.readString(Paths.get("logs")));
	}
	
	
	@Test
	public void testreset() throws Exception{
		Scanner input=new Scanner(System.in);
		String targetcommit=input.next();//读取输入返回的commit
		Commit.reset(targetcommit,"/Users/mayining/Desktop/test");
	}
	
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
	
	@After
	public void show2()throws Exception{
		System.out.println("删除后"+Files.readString(Paths.get("logs")));
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
    
	public void reset(String commitkey,String originpath)throws Exception {
    	String keyoftree=getTargetValue("tree","Objects"+"/"+commitkey);
    	System.out.println(keyoftree);
    	writeback(keyoftree,originpath);
    	new HEAD(commitkey);
	}
	
	public void writeback(String key,String originpath)throws Exception{
    	File file = new File("Objects",key);
		InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
		BufferedReader bf = new BufferedReader(inputReader);
		// 按行读取字符串
		String str;
		while ((str = bf.readLine()) != null) {
			if(str.startsWith("100644 blob ")) {
				
	            //targetValue = contentOfFile.substring(12, 52);
				String keyofblob=str.substring(12, 52);
				String filename=str.substring(53,str.length());
				String contentOfFile = Files.readString(Paths.get("Objects"+"/"+keyofblob));
				File fl = new File(originpath+"/"+filename);
	            FileWriter os = new FileWriter(fl);
	            os.write(contentOfFile);
	            os.flush();
	            os.close();
			}
			if(str.startsWith("040000 tree ")) {
				String keyofnexttree=str.substring(12,52);
				String dicname=str.substring(53,str.length());
				//String keyofnexttree=str.substring("040000 tree ".length(),  "040000 tree ".length() + 1 + 40);
				//String dicname=str.substring("040000 tree ".length() + 1 + 40,str.length()-1);
				File dic = new File(originpath+"/"+dicname);
				if (dic.exists()) {
					deletefiles(originpath+"/"+dicname);
				}
				Path path = Paths.get(originpath+"/"+dicname);
				Path pathCreate = Files.createDirectory(path);
				key=keyofnexttree;
				System.out.println(key);
				originpath=originpath+"/"+dicname;
				writeback(key,originpath);
			}
		}
		
		inputReader.close();
		bf.close();
    }
}


