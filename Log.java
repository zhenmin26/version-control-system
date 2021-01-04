package version_control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Log {
	private static String value; //record every commit
    public static String path = "logs";//存放log文件的路径：目前没有考虑branch情况，所以只有一个文件
    
    public Log(String commit) throws Exception{
    	if(!((new File(path)).exists())){
            (new File(path)).createNewFile();
            this.value = commit;
            putValueIntoFile(value);//第一次commit时，建立文件并把commit的key存入
    	}
    	else {
    		addValueIntoFile(commit);//将后来的commit的key添加到logs文件中
 
    	}
    	
    }
    public String showlogs() {
    	return this.value;//显示value
    }
    
    //在回滚之后删除logs中指定回滚之后的commit的key
    public static String deletecommit(String targetcommit)throws Exception {
    	value=Files.readString(Paths.get(path));
    	int index = value.indexOf(targetcommit);
    	value=value.substring(0,index+40);//截取指定commit之前部分的子串
    	putValueIntoFile(value);
    	return value;
    }
    //新建文件并写入
	public static void putValueIntoFile(String commit){ //把commit key放入logs文件
        try {
            File file = new File(path);
            FileWriter os = new FileWriter(file);
            os.write(commit+"\r\n");
            os.flush();
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
	
	//以补充的方式写入
	public void addValueIntoFile(String commit){ //把commit key放入logs文件
        try {
            File file = new File(path);
            FileWriter os = new FileWriter(file,true);
            os.write(commit+"\r\n");
            os.flush();
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
	
        }
	}
}
