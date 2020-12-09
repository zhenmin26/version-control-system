import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

//Blob类
public class Blob {
	public String KEY; //根据输入文件的内容生成的hash值作为blob的文件名
	public String Value; //文件的内容
	public static String path = "Objects"; //存储生成的key-value文件的文件夹

	//构造函数，构造过程为：根据文件内容产生hash值，在指定文件夹下生成以hash为文件名的文件,再将原来文件的内容写入KEY文件中
	public Blob(String FileName) throws Exception {
		KEY = GenerateKEY(FileName); //生成KEY
		GenerateFile(KEY);			 //创建以KEY为文件名的文件
		PutValueIntoFile(FileName,KEY);//将原来文件的内容写入到KEY文件中
	}

	//根据文件的内容生成对应哈希值，返回该sha1值作为KEY
	public String GenerateKEY(String FileName) throws Exception {
		File file = new File(FileName);
		FileInputStream is = new FileInputStream(file);
		byte[] sha1 = SHA1Checksum(is);

		KEY = "";
		for(int i=0; i<sha1.length; i++) {
			KEY += Integer.toString(sha1[i]&0xFF,16);
		}

		System.out.println("KEY生成成功，KEY:" + KEY);
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
		}while(numRead != -1);
		is.close();
		return complete.digest();
	}

	//在工作路径下的Object文件夹下创建以KEY为文件名的文件
	public void GenerateFile(String KEY) {
		File file=new File(path, KEY);
		try {
			file.createNewFile();
			System.out.println("Blob文件生成成功");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//将原来文件的内容输入到对应key的文件中,如果文件重复则直接覆盖
	//根据git的底层原理，当git add文件内容相同的文件时，不会产生文件
	public void PutValueIntoFile(String FileName,String KEY)throws Exception {
		FileInputStream in=new FileInputStream(FileName);
		FileOutputStream out=new FileOutputStream(path+File.separator+KEY);
		byte[] buffer = new byte[1024];
		int readLength;
		Value = "";
		while((readLength=in.read(buffer))>0){//这里的in.read(buffer);就是把输入流中的东西，写入到内存中（buffer）。
			Value += new String(buffer,0,readLength);//这里顺利将字节数组转化为了字符串
			out.write(buffer);//这里就是把内存中（buffer)的内容写出到输出流中，也就写出到了指定文件中
		}
		System.out.println("Value写入成功");
		in.close();
		out.close();
	}

	//根据KEY值查找对应文件中的value
	public String FindValue(String KEY) throws Exception {
		FileInputStream in=new FileInputStream(path+File.separator+KEY);
		byte[] buffer = new byte[1024];
		int readLength;
		String Value = "";
		while((readLength = in.read(buffer))>0){
			Value += new String(buffer,0,readLength);
		}
		in.close();
		return Value;
	}

	//判断文件/文件夹是否为Blob

}
