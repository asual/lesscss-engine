package com.asual.lesscss;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LessUtils {
    
    private static final Log logger = LogFactory.getLog(LessUtils.class);
    
    public static byte[] readURL(URL source) throws IOException {
        
        URLConnection urlc = source.openConnection();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            InputStream input = urlc.getInputStream();
            try {
                byte[] buffer = new byte[1024];
                int bytesRead = -1;
                while ((bytesRead = input.read(buffer)) != -1) {
                    byteStream.write(buffer, 0, bytesRead);
                }
            } finally {
                input.close();
            }
        } catch (IOException e) {
            logger.error("Can't read '" + source.getFile() + "'.");
            throw e;
        }
        return byteStream.toByteArray();        
    }
    
    public static byte[] readFile(File source) throws IOException {
        
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            FileInputStream input = new FileInputStream(source);
            try {
                byte[] buffer = new byte[1024];
                int bytesRead = -1;
                while ((bytesRead = input.read(buffer)) != -1) {
                    byteStream.write(buffer, 0, bytesRead);
                }
            } finally {
                input.close();
            }
        } catch (IOException e) {
            logger.error("Can't read '" + source.getAbsolutePath() + "'.");
            throw e;
        }
        return byteStream.toByteArray();
    }    
}
