import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Scanner;

import javax.swing.JToolBar.Separator;

//blob类
public class Blob1 {
	public String KEY; //根据输入文件的内容生成的hash值作为blob的文件名
	
	//构造函数，构造过程为：根据文件内容产生hash值，在指定文件夹下生成以hash为文件名的文件,再将原来文件的内容写入KEY文件中
	public Blob1(String FileName) throws Exception {
		KEY = GenerateKEY(FileName);//生成KEY
		GenerateFile(KEY);			//创建以KEY为文件名的文件
		PutValueIntoFile(FileName,KEY);//将原来文件的内容写入到KEY文件中
	}
	
	//根据文件的内容生成对应哈希值，返回该sha1值作为KEY
	public String GenerateKEY(String FileName) throws Exception {
			File file = new File(FileName);
			FileInputStream is = new FileInputStream(file);
			byte[] sha1 = SHA1Checksum(is);
			
			KEY = "";
			for(int i = 0; i < sha1.length; i++) {
				KEY +=Integer.toString(sha1[i]&0xFF,16);
			}
			
			System.out.println("KEY生成成功，KEY:"+KEY);
			return KEY;
	}
		
	//生成SHA1值
	public static byte[] SHA1Checksum(InputStream is)throws Exception{
		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("SHA-1");
		int numRead = 0;
		do {
			numRead = is.read(buffer);
			if(numRead > 0) {
				complete.update(buffer,0,numRead);
			}
		}while(numRead !=-1);
		is.close();
		return complete.digest();
	}
	
	//在工作路径下的Object文件夹下创建以KEY为文件名的文件
	public void GenerateFile(String KEY) {
		File file=new File("Objects\\"+KEY);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Blob文件生成成功");
    }

	//将原来文件的内容输入到对应key的文件中,如果文件重复则直接覆盖
	//根据git的底层原理，当git add文件内容相同的文件时，不会产生文件
	public void PutValueIntoFile(String FileName,String KEY)throws Exception {
		FileInputStream in=new FileInputStream(FileName);
		FileOutputStream out=new FileOutputStream("Objects"+File.separator+KEY);
		byte[]buffer=new byte[1024];
		int readLength;
		System.out.print("Value写入成功,Value:");
		while((readLength=in.read(buffer))>0){//这里的in.read(buffer);就是把输入流中的东西，写入到内存中（buffer）。
			System.out.println(new String(buffer,0,readLength));//这里顺利将字节数组转化为了字符串
			out.write(buffer);//这里就是把内存中（buffer)的内容写出到输出流中，也就写出到了指定文件中
		} 
		in.close();
		out.close();
	}
	
	//根据KEY值查找对应文件中的value
	public static void FindValue(String KEY) throws Exception {
		
		FileInputStream in=new FileInputStream("Objects"+File.separator+KEY);
		byte[]buffer=new byte[1024];
		int readLength;
		while((readLength=in.read(buffer))>0){
			System.out.println(new String(buffer,0,readLength));
		} 
		in.close();
	}
	
	//判断文件/文件夹是否为Blob
	
	
	
	//主函数
	public static void main(String[] args) throws Exception {
		System.out.println("请输入文件名：");
		Scanner input = new Scanner(System.in);
		String FileName = input.next();
		new Blob1(FileName);
		System.out.println("请输入KEY:");
		Scanner input1 = new Scanner(System.in);
		String KEY = input1.next();
		FindValue(KEY);
		input1.close();
		input.close();
	}
}
