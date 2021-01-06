import java.io.File;
import java.util.Scanner;

/**
 * @className Config
 * @descripton 配置信息（用户名及用户邮箱
 */
public class Config {
    private String username;
    private String email;
    public String publicPath = KeyValueObject.root+ File.separator +"git";

    /**
     * @methodName Config
     * @description 获取用户名及用户邮箱，生成Config文件
     */
    public Config(){
        System.out.println("Your name and email have not been set...");
        setUsername();
        setEmail();
        Util.generateFile(publicPath, "Config");
        Util.putValueIntoFile(publicPath, "Config", "username" + " " + username + "\n"
                + "email " + email + "\n");
    }

    /**
     * @methodName Config
     * @description 给定用户名及用户邮箱，生成Config文件
     */
    public Config(String username, String email){
        this.username = username;
        this.email = email;
        Util.generateFile(publicPath, "Config");
        Util.putValueIntoFile(publicPath, "Config", "username" + " " + username + "\n"
                + "email " + email + "\n");
    }

    /**
     * @description 获取用户名
     */
    public void setUsername(){
        System.out.print("Please enter your username:");
        Scanner input = new Scanner(System.in);
		this.username = input.next();
    }

    /**
     * @description 获取用户邮箱
     */
    public void setEmail(){
        System.out.print("Please enter your email:");
        Scanner input = new Scanner(System.in);
        this.email = input.next();
    }

    //返回用户名
    public String returnUsername(){
        return username;
    }

    //返回用户邮箱
    public String returnEmail(){
        return email;
    }
}
