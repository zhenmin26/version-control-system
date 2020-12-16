import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Config {
    private String username;
    private String email;
    public static String path = "Objects";

    //生成配置文件
    public Config(){
        setUsername();
        setEmail();
        generateFile();

    }
    //生成配置文件（如果传入了参数username和email）
    public Config(String username, String email){
        this.username = username;
        this.email = email;
        generateFile();
    }

    //如果没有传入参数，就要求用户输入username
    public void setUsername(){
        System.out.print("Please enter your username:");
        Scanner input = new Scanner(System.in);
		this.username = input.next();
    }

    //如果没有传入参数，就要求用户输入email
    public void setEmail(){
        System.out.print("Please enter your email:");
        Scanner input = new Scanner(System.in);
        this.email = input.next();
    }

    public String returnUsername(){
        return username;
    }

    public String returnEmail(){
        return email;
    }

    //生成config文件，内容是username和email
    public void generateFile() {
        try {
            File file = new File(path, "Config");
            file.createNewFile();
            FileWriter os = new FileWriter(file);
            os.write("username" + " " + username + "\n");
            os.write("email" + " " + email + "\n");
            os.flush();
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
