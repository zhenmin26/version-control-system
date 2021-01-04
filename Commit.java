
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import src.overload;

//commit value:
//    1.根目录的hash
//    2.上一次commit的key

/* Sample
commit 56fe26ad9101d8a15e2d1cb46417fbe38a9a9041 (HEAD -> tast_01_v3, origin/tast_01_v3)
Author: 陆甄敏 <zhenmin0226@163.com>
Date:   Wed Dec 9 14:33:01 2020 +0800

    Change Blob1 to Blob

commit 80abf20f197563ea14ca29f2d6b0945c483b2682
Author: 陆甄敏 <zhenmin0226@163.com>
Date:   Wed Dec 9 14:25:14 2020 +0800

    修改Blob1
 */
public class Commit extends KeyValueObject{
    private static final Object[] String = null;
	//数据域
    private String key;
    private String value;
    private String parent; //已有最新commit的key
    //public static HEAD myHEAD; //HEAD
    //public HEAD myHEAD; //HEAD
    public static String path = "Objects";
    public static String pathOfHEAD = path + File.separator + "HEAD"; //current HEAD
    public static String pathOfConfig = path + File.separator + "Config"; //user info
    private String author;
    private String email;
    private String committer;
    private String dateCreated;
    private String descOfCommit; //description of commit


    //函数
    public Commit(String keyOfRoot, String descOfCommit) throws Exception {
        this.dateCreated = (new Date()).toString();
        this.descOfCommit = descOfCommit;

        //判断文件Config是否存在，存在则读取author，email；否则调用Config创建Config文件
        if(!(new File(pathOfConfig).exists())) {
            new Config();
        }
        this.author = getTargetValue("username", pathOfConfig); //在Config文件中，author对应的是username
        this.email = getTargetValue("email", pathOfConfig);

        //set commit value
        this.value = "";
        //通过是否存在HEAD文件来判断，是否是第一次commit
        //如果HEAD文件不存在，说明是第一次commit
        if(!((new File(pathOfHEAD)).exists())){
            //commit value是根目录的tree key
            this.value += "tree " + keyOfRoot + "\n";
            this.value += "author " + author + "\n";
            this.value += "email " + email + "\n";
            this.value += "date " + dateCreated + "\n";
            this.value += "description " + descOfCommit + "\n";
            //生成commit key
            this.key = generateKey(value);
            //生成HEAD()
            //myHEAD = new HEAD(key);
            new HEAD(key); //在HEAD构造方法例，如果是第一次commit，会产生HEAD文件，并存入value
        }
        else { //commit value是根目录的tree key
            this.value += "tree " + keyOfRoot + "\n";
            this.value += "parent " + getParent() + "\n";
            this.value += "author " + author + "\n";
            this.value += "email " + email + "\n";
            this.value += "date " + dateCreated + "\n";
            this.value += "description " + descOfCommit + "\n";
            //生成commit key
            this.key = generateKey(value);
            //更新HEAD文件里的commit key
            //myHEAD.setValue(key);
            // 如果利用myHEAD对象来更新HEAD文件，程序运行结束之后myHEAD会被释放
            // 下一次运行程序的时候，并不会是第一次commit，就不会生成myHEAD对象了
            // 此时，myHEAD=null，会报错
            new HEAD(key); //现在是在每次commit，都会产生HEAD对象
            //在HEAD构造方法里，如果不是第一次commit，会更新HEAD文件的value
        }

        //生成commit的key-value文件
        generateFile(key);
        //向commit的key-value文件中写入value，value为tree key
        putValueIntoFile(key, value);
        String logcontent="commit: "+key+"\n"+"author " + author + "\n"+"date " + dateCreated + "\n"+descOfCommit+"\n";
        new Log(logcontent);//生成commit的同时，补充logs文件
    }
    //获得已有最新commit key
    public String getParent() throws IOException {
        this.parent = Files.readString(Paths.get(pathOfHEAD)); //从HEAD中获得上一次commit的key
        return parent;
    }

    public String returnParent(){
        return parent;
    }

    public String returnKey(){
        return key;
    }

    public String returnValue(){
        return value;
    }


    public static String getTargetValue(String target, String path){
        String targetValue = "";
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader in = new BufferedReader(fileReader);
            do{
                String line = in.readLine();
                if(line.matches(target + ".*")) {
                    targetValue = line.substring(target.length() + 1).trim();
                }
            }while(targetValue == "");
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

    @Override
    public String toString(){
        //返回commit到下面的格式
        return "commit " + key + "\n"
                + "parent " + parent + "\n"
                + "author " + author + "<" + email + ">" + "\n"
                + "date " + dateCreated + "\n";
/*   commit 80abf20f197563ea14ca29f2d6b0945c483b2682
     parent 80abf20f197563ea14ca29f2d6b0945c483b2682
     author username <email>
     date dateCreated
*/
    }
    /*回滚函数：给定希望回滚到的commit，
    1.找到commit中tree的内容，
    2.在工作路径中还原commit中的文件，首先判断工作路径中是否有想还原中没有的文件，有的话就删掉；
    然后逐行读取tree中文件的hash值，还原时首先与现有文件名比较，若名字相同，则比较文件内容，文件内容相同，文件保留，不同则删除现有的
    增加需要还原的。若文件名与现存工作区的不同，则直接补充上。
   3.更新HEAD文件和logs文件
   */
    public static void reset(String commitkey,String originpath)throws Exception {
    	String keyoftree=getTargetValue("tree","Objects"+ File.separator +commitkey);//得到tree的哈希值
    	writeback(keyoftree,originpath);//调用辅助函数负责还原文件
    	new HEAD(commitkey);//更新HEAD指针
    	Log.changelog(commitkey);//更新log文件，删除需要回滚的commit后面的commit
    }
    
    public static void reset(String commitkey)throws Exception {
    	String keyoftree=getTargetValue("tree","Objects"+ File.separator +commitkey);//得到tree的哈希值
    	String originpath="/Users/mayining/Desktop/test";
    	writeback(keyoftree,originpath);//调用辅助函数负责还原文件
    	new HEAD(commitkey);//更新HEAD指针
    	Log.changelog(commitkey);//更新log文件，删除需要回滚的commit后面的commit
    }
    	/*
    	 写回函数负责根据提供的tree的哈希值逐行扫描内容，如果是文件，则与工作区文件比较，再进行还原，如果是文件夹，则递归调用
    	 函数进行内部文件的比较还原。
    	 */
    public static void writeback(String key,String originpath)throws Exception{
    	File file = new File("Objects",key);
		InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
		BufferedReader bf = new BufferedReader(inputReader);
		// 将文件内容全部读取到str字符串中
		String str="";
		String strr="";
		strr=bf.readLine();
		while(strr!=null) {
    		str+=strr+"\n";  
    		strr=bf.readLine();
    	}
		//首先比较工作区文件夹中的文件是否在需要还原的tree里，如果不在，则删除
		deletenonexisting(str,originpath);
		//逐行读取str中内容
		String []ss=str.split("\n");
		for (int i=0;i<ss.length;i++){
			if(ss[i].startsWith("100644 blob ")) {//如果读入的hash是文件，则首先得到文件的哈希值，文件名和文件内容
				String keyofblob=ss[i].substring(12, 52);
				String filename=ss[i].substring(53,ss[i].length());
				String contentOfFile = Files.readString(Paths.get("Objects"+ File.separator +keyofblob));
				File fl = new File(originpath+ File.separator +filename);
				if(fl.exists()) {//与工作区文件比较，如果名称相同则进一步比较内容
					String origincontent=Files.readString(Paths.get(originpath+ File.separator +filename));
					if(contentOfFile.equals(origincontent)) {
						return;//内容相同，文件不做处理，直接返回
					}
					else {
			            FileWriter os = new FileWriter(fl);
			            os.write(contentOfFile);
			            os.flush();
			            os.close();//文件内容不同，覆盖写入文件
			}
				}
			   else {
					FileWriter os = new FileWriter(fl);
		            os.write(contentOfFile);
		            os.flush();
		            os.close();//文件不存在，直接写入文件
				}
			}
				
			if(ss[i].startsWith("040000 tree ")) {
				//如果读入的hash是文件夹，则
				//比较文件夹是否存在，不存在则创建文件夹，存在则直接进入，比较里面内容
				//递归文件夹中内容，直到所有文件都写入
				String keyofnexttree=ss[i].substring(12,52);
				String dicname=ss[i].substring(53,ss[i].length());
				File dic = new File(originpath+ File.separator +dicname);
				if (!dic.exists()) {
					//deletefiles(originpath+ File.separator +dicname);
					Path path = Paths.get(originpath+ File.separator +dicname);
					Path pathCreate = Files.createDirectory(path);//文件夹不存在，创建文件夹
				}
				key=keyofnexttree;//更新递归的下一层key
				originpath=originpath+ File.separator +dicname;//更新路径
				writeback(key,originpath);
				
			}
	
		}
		inputReader.close();
		bf.close();
    }
    /*
     删除不需要的文件函数
     将文件夹中包含的file用listfiles函数展示出来，再检查每个文件的名称是否出现在tree的value中，如果
     出现了，则不做处理，如果不存在，说明还原时不需要这个文件，则删除。
     */
    public static void deletenonexisting(String value,String path) throws IOException {
    	File current=new File(path);
    	File[] fs = current.listFiles();
        for(int i=0;i<fs.length;i++) {
        	String filename=fs[i].getName();
        	if(!value.contains(filename)) {
        		deletefiles(fs[i]);
        	}
        }
    }
    
    //递归删除文件夹
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
	
	@overload
	public static boolean deletefiles(File file) {
		if (!file.exists()) {
			System.out.println("file does not exist");
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
    	
    	
    	
    	
    	
    	
    	
    	
    
