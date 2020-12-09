import java.util.Scanner;

public class testBlob {
    //主函数
    public static void main(String[] args) throws Exception {
        System.out.println("请输入文件名：");
        Scanner input = new Scanner(System.in);
        String FileName = input.next();
        Blob myBlob = new Blob(FileName);
        System.out.println("请输入KEY:");
        Scanner input1 = new Scanner(System.in);
        String KEY = input1.next();
        System.out.println(myBlob.FindValue(KEY));
        input1.close();
        input.close();
    }
}
