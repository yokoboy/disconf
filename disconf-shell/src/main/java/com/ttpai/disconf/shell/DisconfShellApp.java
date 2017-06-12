package com.ttpai.disconf.shell;

import com.ttpai.disconf.shell.service.ConfigService;
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
            throw new RuntimeException("目标路径不能为空“--targetPath=”");
        }


        Map<String, String> configFiles = configService.selectConfigFileByAppAndEnv(app, env);

        Set<Map.Entry<String, String>> entries = configFiles.entrySet();
        File targetDir = Paths.get(targetPath).toFile();
        if (targetDir.exists()) {
            if (!targetDir.isDirectory()) {
                throw new RuntimeException("“--targetPath=”必须是一个文件夹");
            }
            if (rmDir) {
                // 删除文件夹
                Files.walkFileTree(Paths.get(targetPath), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        System.out.println("rm -> " + file + (file.toFile().delete()));
                        return super.visitFile(file, attrs);
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        System.out.println("rm -> " + dir + (dir.toFile().delete()));
                        return super.postVisitDirectory(dir, exc);
                    }
                });
            }
        }

        if (!targetDir.exists()) {
            System.out.println("mkdir -> " + targetDir);
            boolean newFile = targetDir.mkdirs();
            if (!newFile) {
                throw new RuntimeException("创建文件夹失败：" + targetPath);
            }
        }

        for (Map.Entry<String, String> entry : entries) {
            Path path = Paths.get(targetPath, entry.getKey());
            System.out.println("mv -> " + path);

            Files.write(path, entry.getValue().getBytes(Charset.forName("utf-8")));
        }
    }


    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(args));
        argsList.add("--app=boss");
        argsList.add("--env=dev");
        argsList.add("--targetPath=d:/__test/boss/dev");

        SpringApplication application = new SpringApplication(DisconfShellApp.class);
        application.setWebEnvironment(false); // 非web环境
        application.setBannerMode(Banner.Mode.OFF); // 关闭banner
        application.addInitializers();

        application.run(argsList.toArray(new String[argsList.size()]));
    }


}
