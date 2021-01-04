
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Log {
    public static String path = "logs";//存放log文件的路径：目前没有考虑branch情况，所以只有一个文件
    
    public Log(String commit) throws Exception{
    	if(!new File(path).exists()) {
    		File file=new File(path);
    		file.mkdir();
    	}
    	String head=Files.readString(Paths.get("objects"+ File.separator+"HEAD1"));
    	String branchname=head.substring(head.lastIndexOf("/")+1);
    	if(!((new File(path+File.separator+branchname)).exists())){
            (new File(path)).createNewFile();
            putValueIntoFile(commit,path+File.separator+branchname);//第一次commit时，建立文件并把commit的key存入
    	}
    	else {
    		putValueIntoFile(commit,path+File.separator+branchname);
    	}
    }
    
    /*在回滚之后修改logs对应文件中内容为目标commit的呢绒
      只保存commit的key并读取commit文件中除了tree和parent部分的内容
      读到目标commit时则停止循环，直接再向下读取三行的内容后关闭bufferreader和filereader停止读入
     */
    
    public static void changelog(String targetcommit)throws Exception{
    	String head=Files.readString(Paths.get("objects"+ File.separator+"HEAD1"));
    	String branchname=head.substring(head.lastIndexOf("/")+1);
    	FileReader fileReader = new FileReader("objects"+File.separator+targetcommit);
        BufferedReader in = new BufferedReader(fileReader);
        String line = in.readLine();
        String value="commit "+targetcommit+"\n";
        while(line!=null) {
        		if(!line.startsWith("tree")&&(!line.startsWith("parent"))) {
		        	value+=line+"\n";
        		}
		        line=in.readLine();
        }
        in.close();
    	fileReader.close();
    	putValueIntoFile(value,path+File.separator+branchname);
    }
    //新建文件并写入
    public static void showlogs() throws Exception {
    	String curcommit=Files.readString(Paths.get("objects"+ File.separator+"HEAD"));
        String content="";
 	    while(!curcommit.equals("")) {
 	    	content+="commit "+curcommit+"\n";
        	content+="author "+Commit.getTargetValue("author", "objects"+ File.separator+curcommit)+"\n";
        	content+="date "+Commit.getTargetValue("date", "objects"+ File.separator+curcommit)+"\n";
        	content+=Commit.getTargetValue("description","objects"+ File.separator+curcommit)+"\r\n";
        	String value=Files.readString(Paths.get("objects"+ File.separator+curcommit));
        	if(value.contains("parent")) {
        		String nextcommit=Commit.getTargetValue("parent","objects"+ File.separator+curcommit);
        		curcommit=nextcommit;
        	}
        	else curcommit="";
 	    }
    	System.out.println(content);
    }
	
	public static void putValueIntoFile(String commit,String logpath){ //把commit key放入logs文件
        try {
            File file = new File(logpath);
            FileWriter os = new FileWriter(file);
            os.write(commit+"\r\n");
            os.flush();
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
	public static void main(String[] args) throws Exception {
		showlogs();
	}
}
