
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;

public class Test_blob {
	//测试前构造一个文件夹（test）包含一个文件（test1）用来测试
	@Before
	public void creatdictory() {
		creatd("objects","test");
	}
	@Before
	public void creatfile() {
		creatf("objects/test","test1","hello world!");
	}
	
	//第一个测试函数，返回key值函数，检查key的位数是否为40位
	@Test
    public void checkkey()throws Exception {
		Blob myBlob = new Blob("objects/test/test1");
		String key=myBlob.returnKey();
    	if (key.length()==40) {
    		System.out.println("key长度正确");
    	}
    }
	//第二个测试函数，返回value函数，检查生成文件中的value和原文件中存储的内容是否一致
	@Test
    public void checkvalue()throws Exception {
		Blob myBlob = new Blob("objects/test/test1");
		String value=myBlob.returnValue();
    	String str="";
    	FileInputStream in=new FileInputStream("objects/test/test1");
		byte[]buffer=new byte[1024];
		int readLength;
		while((readLength=in.read(buffer))>0){
			str=new String(buffer,0,readLength);
		}
		Assert.assertEquals(value, str);
    }
	//检查完毕后，删除生成的文件夹
	@After
    public void deletefile() {
    	deletefiles("objects/test");
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
