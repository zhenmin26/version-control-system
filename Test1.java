
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Test1 {
	public static void main(String[] args) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		FileOutputStream fos = null;
		PrintWriter pw = null;
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		Set<String> set3 = new HashSet<String>();
		Set<String> set4 = new HashSet<String>();
		try {
			String str = "";
			Scanner sc = new Scanner(System.in);
			System.out.println("请输入文件1的名称:");
			String txt1 = sc.nextLine();

			fis = new FileInputStream(txt1);// FileInputStream
			// 从文件系统中的某个文件中获取字节
			isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
			br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new
											// InputStreamReader的对象
			while ((str = br.readLine()) != null) {
				set1.add(str);

			}
			System.out.println(set1.size());// 打印出str1
			// t1读取完毕，读取t2
			System.out.println("t1读取完毕，请输入文件2的名称:");
			String txt2 = sc.nextLine();
			fis = new FileInputStream(txt2);// FileInputStream
			// 从文件系统中的某个文件中获取字节
			isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
			br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new
											// InputStreamReader的对象
			while ((str = br.readLine()) != null) {
				set2.add(str);
			}
			System.out.println(set2.size());// 打印出str1

			// 循环比较set1和set2的值
			for (String string1 : set1) {
				boolean isEqual = false;
				for (String string2 : set2) {
					if (string1.equals(string2)) {
						isEqual = true;
					}
				}
				if (!isEqual) {
					// t1中存在，但t2中不存在的值
					set3.add(string1);
				}
			}
			for (String string2 : set2) {
				boolean isEqual = false;
				for (String string1 : set1) {
					if (string2.equals(string1)) {
						isEqual = true;
					}
				}
				if (!isEqual) {
					// t2中存在，但t1中不存在的值
					set4.add(string2);
				}
			}
			// 遍历set3和set4的值
			System.out.println("文件1中存在，但文件2中不存在的值");
			System.out.println("请输入完整保存路径:");

			String txt3 = sc.nextLine();
			File file = new File(txt3);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			System.out.println("文件1中存在，但文件2中不存在的值");
			for (String string : set4) {
				pw.write("'"+string+"'," + "\n");
				System.out.println(string);
			}
			pw.flush();
			System.out.println("文件2中存在，但文件1中不存在的值");
			System.out.println("请输入保存路径:");
			String txt4 = sc.nextLine();
			file = new File(txt4);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			System.out.println("文件2中存在，但文件1中不存在的值");
			for (String string : set3) {
				pw.write("'"+string+"'," + "\n");
				System.out.println(string);
			}
			pw.flush();
		} catch (FileNotFoundException e) {
			System.out.println("找不到指定文件");
		} catch (IOException e) {
			System.out.println("读取文件失败");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
				fos.close();
				pw.close();
				// 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
