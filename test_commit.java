
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		Tree Tree1 = new Tree("/Users/mayining/Desktop/test");
		String keyOfRoot = Tree1.returnKey();
		if(!((new File("Objects"+File.separator+"HEAD")).exists())){
            Commit Commit1 = new Commit(keyOfRoot);
            System.out.println("First Commit -- create File HEAD");
            System.out.println("keyOfRoot: " + keyOfRoot); //根目录的tree key
            System.out.println("keyOfCommit: " + Commit1.returnKey()); //commit1的commit key
            System.out.println("keyOfParent: " + Commit1.returnParent()); //parent commit key
        }
	}
	
	@Test
	//测试得到上次commit中原tree的哈希值，若能正确打印出，则通过测试
	public void checkgettargetvalue()throws Exception{
        Path targetFilePath;
        String keyOfParent;
        String keyOfParentRoot;
        targetFilePath = Paths.get("Objects/HEAD");
        keyOfParent = Files.readString(targetFilePath);
        keyOfParentRoot = getTargetValue("tree", "Objects" + File.separator + keyOfParent);
        System.out.println(keyOfParentRoot);
	}
	
	@Test
	//测试改变原文件内容从而产生新的commit的函数，若能打印出此次tree的哈希值，commit哈希值和上次提交哈希值，则通过测试
	public void followingcommit() throws Exception{
		deletefiles("/Users/mayining/Desktop/test/test1");
		Tree Tree1 = new Tree("/Users/mayining/Desktop/test");
		String keyOfRoot = Tree1.returnKey();
        Path targetFilePath;
        String keyOfParent;
        String keyOfParentRoot;
        targetFilePath = Paths.get("Objects" + File.separator + "HEAD");
        keyOfParent = Files.readString(targetFilePath);
        keyOfParentRoot = getTargetValue("tree", "Objects" + File.separator + keyOfParent);
        if(!(keyOfRoot.equals(keyOfParentRoot))) {
        	Commit Commit1 = new Commit(keyOfRoot);
            System.out.println("keyOfRoot: " + keyOfRoot); //更新后的根目录的tree key
            System.out.println("keyOfCommit: " + Commit1.returnKey()); //commit2的commit key
            System.out.println("keyOfParent: " + Commit1.returnParent()); //commit2 的parent commit key --commit1的key
            }
        else{
           System.out.println("Nothing has been changed.");
            }
	}
	@After
	//检查完毕后，删除生成的文件夹
	public void deletefiles()throws Exception{
		deletefiles("/Users/mayining/Desktop/test");
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
			
			//删除文件函数
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
	}
