package com.telerikacademy.finalprojectpeerreview.utils;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class FileConverter {
/*
    private static final String url = "jdbc:mysql://localhost:3306/peerreview";
    private static final String user = "root";
    private static final String password = "root";*/

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
/*
    public File convertToInt(String documentPath) throws FileNotFoundException, SQLException {
        File file = new File(documentPath);
        int length = (int)file.length();
        FileInputStream fis = new FileInputStream(file);
        Connection connection = DriverManager.getConnection(url, user, password);
        PreparedStatement pstmt = connection.prepareStatement();
        pstmt.setBinaryStream(3, fis,length);
        int i = pstmt.executeUpdate();
    }*/
}
