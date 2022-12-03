package com.autotest.LiuMa.common.utils;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageUtils {

    public static void convertBase64ToImage(String base64Code, String path) throws IOException {
        BufferedImage image = null;
        byte[] imageByte = null;
        imageByte = DatatypeConverter.parseBase64Binary(base64Code);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        image = ImageIO.read(new ByteArrayInputStream(imageByte));
        bis.close();
        File outputfile = new File(path);
        if (!outputfile.getParentFile().exists()) { // 如果父目录不存在，创建父目录
            outputfile.getParentFile().mkdirs();
        }
        ImageIO.write(image, "png", outputfile);
    }
}
