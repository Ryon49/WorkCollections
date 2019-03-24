package com.wdong.basic;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

public class CreateFile {

    @Test
    public void create() {
//        try {
            System.out.println(Paths.get("performanceLog").toAbsolutePath().toString());
//            ClassLoader classLoader = getClass().getClassLoader();
//            File file = new File(classLoader.getResource(".").getFile() + "/log/temp.txt");
//            System.out.println(file.getAbsoluteFile().toString());
////            File file = ResourceUtils.getFile("classpath:/log/temp.txt");
//            System.out.println(file.exists());
//            if (file.createNewFile()) {
//                System.out.println("File is created!");
//            } else {
//                System.out.println("File already exists.");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
