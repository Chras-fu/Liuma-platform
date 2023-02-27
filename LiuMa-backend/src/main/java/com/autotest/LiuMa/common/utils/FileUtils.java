package com.autotest.LiuMa.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.autotest.LiuMa.common.exception.FileUploadException;
import com.autotest.LiuMa.common.exception.LMException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;


public class FileUtils {

    public static String uploadFile(MultipartFile uploadFile, String path, String name) {
        if (uploadFile == null) {
            return null;
        }
        File testDir = new File(path);
        if (!testDir.exists()) {
            testDir.mkdirs();
        }
        String filePath = testDir + "/" + name;
        File file = new File(filePath);
        try {
            uploadFile.transferTo(file);
        } catch (IOException e) {
            throw new FileUploadException("文件上传失败");
        }
        return filePath;
    }

    public static void createJsonFile(JSONObject json, String filePath){
        String content = JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();
            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(content);
            write.flush();
            write.close();
        } catch (Exception e) {
            throw new LMException("json文件生成失败");
        }
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        String dir = path.substring(0, path.lastIndexOf("/"));
        File fileDir = new File(dir);
        if(fileDir.exists()){
            fileDir.delete();
        }
    }

    public static void deleteDir(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            String dir = path.length() == 0 ? "" : path + "/";
            //递归删除目录中的子目录下
            for (int i=0; i<files.length; i++) {
                deleteDir(dir + files[i].getName());
            }
        }
        file.delete();
    }

    public static byte[] fileToByte(File tradeFile) {
        byte[] buffer = null;
        try (FileInputStream fis = new FileInputStream(tradeFile);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (Exception ignored) {
        }
        return buffer;
    }

    public static String uploadTestFile(MultipartFile uploadFile, String path) {
        return uploadFile(uploadFile, path, uploadFile.getOriginalFilename());
    }

    public static void downloadFile(String path, HttpServletResponse response) {
        File file = new File(path);
        if (!file.isFile()) {
            throw new LMException("文件不存在");
        }
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
            FileCopyUtils.copy(bufferedInputStream, bufferedOutputStream);
        } catch(Exception e){
            throw new LMException("文件下载失败");
        }
    }

    public static ResponseEntity<byte[]> previewImage(String path) {
        File file = new File(path);
        byte[] fileByte= FileUtils.fileToByte(file);
        if(fileByte == null){
            return null;
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(fileByte);
    }

}
