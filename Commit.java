package version_control;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * @className: Commit
 * @description: class Commit designed for commits
 */
public class Commit extends KeyValueObject{
    //文件路径
    public String publicPath = "git";
    public String pathOfObjects = publicPath + File.separator + "Objects";
    public String pathOfHEAD = publicPath + File.separator + "HEAD"; //current HEAD
    public String pathOfConfig = publicPath + File.separator + "Config"; //user info

    //commit value
    private String key;
    private String value;
    private String parent; //已有最新commit的key

    //public static HEAD myHEAD; //HEAD
    //public HEAD myHEAD; //HEAD
    public String path = "objects";
    public String pathOfHEAD = path + File.separator + "HEAD";
    private String author;
    private String email;
    private String committer;
    private java.util.Date dateCreated;
    public static Log log;


    //函数
    public Commit(String keyOfRoot) throws Exception {
        this.value = "";
        //通过是否存在HEAD文件来判断，是否是第一次commit
        //如果HEAD文件不存在，说明是第一次commit
        if(!((new File(pathOfHEAD)).exists())){
            //commit value是根目录的tree key
            this.value += "tree " + keyOfRoot + "\n";
            //生成commit key
            this.key = generateKey(value);
            //生成HEAD()
            //myHEAD = new HEAD(key);
            new HEAD(key); //在HEAD构造方法例，如果是第一次commit，会产生HEAD文件，并存入value
        }
        else { //commit value是根目录的tree key
            this.value += "tree " + keyOfRoot + "\n";
            this.value += "parent " + getParent() + "\n";
            //生成commit key
            this.key = generateKey(value);
            //更新HEAD文件里的commit key
            //myHEAD.setValue(key);
            // 如果利用myHEAD对象来更新HEAD文件，程序运行结束之后myHEAD会被释放
            // 下一次运行程序的时候，并不会是第一次commit，就不会生成myHEAD对象了
            // 此时，myHEAD=null，会报错！
            new HEAD(key); //现在是在每次commit，都会产生HEAD对象
            //在HEAD构造方法里，如果不是第一次commit，会更新HEAD文件的value
    private String author;
    private String email;
    private String dateCreated;
    private String descOfCommit; //description of commit


    /**
     * @description 给定根目录的key和上一次commit的key，生成这一次commit的commit对象
     * @param keyOfRoot
     * @param descOfCommit
     * @throws Exception
     */
    public Commit(String keyOfRoot, String descOfCommit) throws Exception {
        this.dateCreated = (new Date()).toString();
        this.descOfCommit = descOfCommit;

        //判断文件Config是否存在，存在则读取author，email；否则调用Config创建Config文件
        if(!(new File(pathOfConfig).exists())) {
            new Config();
        }
        this.author = Util.getTargetValue("username", pathOfConfig); //在Config文件中，author对应的是username
        this.email = Util.getTargetValue("email", pathOfConfig);

        //initial commit value
        this.value = "";
        //set commit value
        this.value += "tree " + keyOfRoot + "\n";
        if(((new File(pathOfHEAD)).exists())){ //通过是否存在HEAD文件来判断，是否是第一次commit
            //如果HEAD文件存在，说明是不是第一次commit，则commit value要加入parent commit key
            this.value += "parent " + getParent() + "\n";
        }
        this.value += "author " + author + "\n";
        this.value += "email " + email + "\n";
        this.value += "date " + dateCreated + "\n";
        this.value += "description " + descOfCommit + "\n";

        //生成commit key
        this.key = generateKey(value);
        //生成HEAD()
        new HEAD(key); //在HEAD构造方法例，如果是第一次commit，会产生HEAD文件，并存入value
        //生成commit的key-value文件
        Util.generateFile(pathOfObjects, key);
        //向commit的key-value文件中写入value，value为tree key

        putValueIntoFile(key, value);
        dateCreated=new java.util.Date();
        new Log(key);//生成commit的同时，补充logs文件
    }
    //获得已有最新commit key

        Util.putValueIntoFile(pathOfObjects, key, value);
    }

    /**
     * @description 获得已有最新commit key
     * @return
     * @throws IOException
     */

    public String getParent() throws IOException {
        this.parent = Files.readString(Paths.get(pathOfHEAD)); //从HEAD中获得上一次commit的key
        return parent;
    }

    //返回parent commit key
    public String returnParent(){
        return parent;
    }

    //返回commit key
    public String returnKey(){
        return key;
    }

    //返回commit value
    public String returnValue(){
        return value;
    }



    public static String getTargetValue(String target, String path){
        String targetValue = "";
        try {
            String contentOfFile = Files.readString(Paths.get(path));
            int index = contentOfFile.indexOf(target);
            System.out.println(index);
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
    //回滚函数：给定希望回滚到的commit，
    //1.找到commit中tree的内容，
    //2.在工作路径中还原commit中的文件，这里是将commit中所有文件替换掉原始的文件夹，还没有考虑只更换差异文件的优化方式。
    //3.更新HEAD文件和logs文件
    public static void reset(String commitkey,String originpath)throws Exception {
    	String keyoftree=getTargetValue("tree","Objects"+"/"+commitkey);
    	writeback(keyoftree,originpath);
    	new HEAD(commitkey);
    	Log.deletecommit(commitkey);
    }
    	
    public static void writeback(String key,String originpath)throws Exception{
    	File file = new File("Objects",key);
		InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
		BufferedReader bf = new BufferedReader(inputReader);
		// 按行读取字符串
		String str;
		while ((str = bf.readLine()) != null) {
			if(str.startsWith("100644 blob ")) {//如果读入的hash是文件，则直接以覆盖的形式写入文件
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
				//如果读入的hash是文件夹，则
				//首先删除工作目录的该文件夹，
				//再创建新的文件夹
				//递归文件夹中内容，直到所有文件都写入
				String keyofnexttree=str.substring(12,52);
				String dicname=str.substring(53,str.length());
				File dic = new File(originpath+"/"+dicname);
				if (dic.exists()) {
					deletefiles(originpath+"/"+dicname);
				}
				Path path = Paths.get(originpath+"/"+dicname);
				Path pathCreate = Files.createDirectory(path);
				key=keyofnexttree;//更新递归的下一层key
				originpath=originpath+"/"+dicname;//更新路径
				writeback(key,originpath);
				
			}
	
		}
		inputReader.close();
		bf.close();
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
}
    	
    	
    	
    	
    	
    	
    	
    	
    

                + "date " + dateCreated + "\n"
                + "description " + descOfCommit;
    }
}

