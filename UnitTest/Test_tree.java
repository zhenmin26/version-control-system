
import java.lang.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;

public class Test_tree implements Fileoperation {
	//在测试之前先创建包含一个文件（test1）和一个文件夹（testt：包含test2，test3两个文件）的文件夹用来测试
	@Before
	public void creatdirectory() {
		creatd("object","test");
		creatd("object/test","testt");
		creatf("object/test","test1","hello world!");
		creatf("object/test/testt","test2","123");
		creatf("object/test/testt","test3","456");
	}
	
	
	//第一个需要测试的函数，返回key函数，检查key的长度是否为40位
	@Test
	public void checkkey() throws Exception{
		Tree myTree = new Tree("object/test");
		String key=myTree.returnKey();
    	if (key.length()==40) {
    		System.out.println("key长度正确");
        //System.out.println(myTree.getKey());
        //System.out.println(myTree.getValue());
        //System.out.println(myTree.getValue("9f24c63b2cf04decadf6ee44bda33a3f2a4e6569"));
    }
	}
	//第二个需要测试的函数，返回内容函数，目前先用打印出来的方法检测内容是否正确
	@Test
	public void checkvalue() throws Exception{
		Tree myTree = new Tree("object/test");
		String value=myTree.returnValue();
		System.out.println(value);
		}
	
	 //在每次测试后，删掉这次创建的文件夹，不占用空间   
	@After
	public void deletefile() {
		deletefiles("object/test");
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
}