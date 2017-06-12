package com.ttpai.disconf.shell;

import com.ttpai.disconf.shell.service.ConfigService;
import com.ttpai.disconf.shell.util.R;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kail on 2017/6/12.
 */
@SpringBootApplication
public class DisconfShellApp implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DisconfShellApp.class);

    @Autowired
    private ConfigService configService;

    @Value("${app:}")
    String app; // 应用
    @Value("${env:}")
    String env; // 环境
    @Value("${targetPath:}")
    String targetPath; // 配置文件拷贝到那个路径下
    @Value("${rmDir:true}")
    Boolean rmDir; // 拷贝前是否删除原路径


    @Override
    public void run(String... args) throws Exception {
        if (StringUtils.isBlank(targetPath)) {
            R.Serr.interrupt("目标路径不能为空'--targetPath='");
        }


        Map<String, String> configFiles = configService.selectConfigFileByAppAndEnv(app, env);

        Set<Map.Entry<String, String>> entries = configFiles.entrySet();
        File targetDir = Paths.get(targetPath).toFile();
        if (targetDir.exists()) {
            if (!targetDir.isDirectory()) {
                R.Serr.interrupt("'--targetPath='必须是一个文件夹");
            }
            if (rmDir) {
                // 删除文件夹
                Files.walkFileTree(Paths.get(targetPath), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        R.Sout.println("rm -> " + file + (file.toFile().delete()));
                        return super.visitFile(file, attrs);
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        R.Sout.println("rm -> " + dir + (dir.toFile().delete()));
                        return super.postVisitDirectory(dir, exc);
                    }
                });
            }
        }

        if (!targetDir.exists()) {
            R.Sout.println("mkdir -> " + targetDir);
            boolean newFile = targetDir.mkdirs();
            if (!newFile) {
                R.Serr.interrupt("创建文件夹失败：" + targetPath);
            }
        }

        for (Map.Entry<String, String> entry : entries) {
            Path path = Paths.get(targetPath, entry.getKey());
            R.Sout.println("mv -> " + path);

            Files.write(path, entry.getValue().getBytes(Charset.forName("utf-8")));
        }
    }

    private static void printHelp(ArrayList<String> argsList) {
        if (argsList.isEmpty() || argsList.contains("-h") || argsList.contains("-?") || argsList.contains("-help") || argsList.contains("--help")) {
            R.Sout.println("--app=<appName>         （必填）应用名，例如： --app=boss ");
            R.Sout.println("--env=<envName>         （必填）环境名，例如： --env=dev");
            R.Sout.println("--targetPath=<dir>      （必填）配置文件的保存路径，例如：--targetPath=/opt/config   (注意：必须是路径，不能是文件)");
            R.Sout.println("--rmDir=<true/false>     拷贝配置文件前是否删除目标路径（targetPath），默认是 true");
            R.Sout.println("");
            R.Sout.println("--db.ip=<ip>             disconf 数据库ip，默认是10.1.1.84");
            R.Sout.println("--db.port=<port>         disconf 数据库端口，默认是 3306");
            R.Sout.println("--db.user=<username>     disconf 数据库用户名，默认是 disconf");
            R.Sout.println("--db.passwd=<passwd>     disconf 数据库密码，默认是 disconf");
            R.Sout.println("");
            R.Sout.println("注意：");
            R.Sout.println("1. 应用名必须唯一");
            R.Sout.println("2. 应用下的环境名必须唯一");
            R.Sout.println("3. 不支持二进制的配置文件");

            System.exit(0);
        }
    }


    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
        printHelp(argsList); // 打印帮助信息

//        argsList.add("--app=boss");
//        argsList.add("--env=dev");
//        argsList.add("--targetPath=d:/__test/boss/dev");
//        argsList.add("--rmDir=true");
//        argsList.add("--db.passwd=disconf");

        SpringApplication application = new SpringApplication(DisconfShellApp.class);
        application.setWebEnvironment(false); // 非web环境
        application.setBannerMode(Banner.Mode.OFF); // 关闭banner
        application.addInitializers();

        application.run(argsList.toArray(new String[argsList.size()]));
    }


}
