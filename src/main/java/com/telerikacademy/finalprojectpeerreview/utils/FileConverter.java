package com.telerikacademy.finalprojectpeerreview.utils;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@Component
public class FileConverter {

    public byte[] convertToBytes(String photoPath) throws IOException {
        ByteArrayOutputStream baos = null;
        byte[] imageInByte;
        try {
            BufferedImage originalImage = ImageIO.read(new File(photoPath));
            baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
        } finally {
            assert baos != null;
            baos.close();
        }
        return imageInByte;
    }
}
