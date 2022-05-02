package com.autotest.LiuMa.common.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static void compress(String resourcesPath, String targetPath, String targetName) throws Exception {
        //源文件
        File resourcesFile = new File(resourcesPath);
        //目的文件夹
        File targetFile = new File(targetPath);
        if (!targetFile.exists()) { // 如果父目录不存在，创建父目录
            targetFile.mkdirs();
        }
        FileOutputStream outputStream = new FileOutputStream(targetPath+"/"+targetName+".zip");

        CheckedOutputStream cos = new CheckedOutputStream(outputStream, new CRC32());

        ZipOutputStream out = new ZipOutputStream(cos);

        createCompressedFile(out, resourcesFile, "/"+targetName);

        out.close();
    }

    public static void createCompressedFile(ZipOutputStream out, File file, String dir) throws Exception {
        if (file.isDirectory()) {   //如果当前的是文件夹，则进行进一步处理
            //得到文件列表信息
            File[] files = file.listFiles();
            //将文件夹添加到下一级打包目录
            out.putNextEntry(new ZipEntry(dir + "/"));
            dir = dir.length() == 0 ? "" : dir + "/";
            //循环将文件夹中的文件打包
            for (int i = 0; i < files.length; i++) {
                //递归处理
                createCompressedFile(out, files[i], dir + files[i].getName());
            }
        } else {
            //当前的是文件，打包处理
            //文件输入流
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(dir);
            out.putNextEntry(entry);
            int j = 0;
            byte[] buffer = new byte[1024];

            while ((j = bis.read(buffer)) > 0) {
                out.write(buffer, 0, j);
            }
            //关闭输入流
            bis.close();
        }
    }
}
