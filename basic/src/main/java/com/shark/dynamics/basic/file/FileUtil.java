package com.shark.dynamics.basic.file;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtil {

    public static String getSuffix(String path) {
        return path.substring(path.lastIndexOf('.') + 1);
    }

    public static String getPrefix(String path) {
        return path.substring(0, path.lastIndexOf('/'));
    }

    public static String getFileName(String path) {
        if (!path.contains("/")) {
            return path;
        }
        return path.substring(path.lastIndexOf('/' ) + 1);
    }

    public static String getFileNameNoSuffix(String path) {
        if (!path.contains("/")) {
            return path.substring(0, path.lastIndexOf('.'));
        }
        return path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
    }

    public static void copyFileByChannel(File source, File dest) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            if (!source.exists()) return;
            dest.delete();

            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputChannel != null) {
                try {
                    inputChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputChannel != null) {
                try {
                    outputChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean exist(String path) {
        return new File(path).exists();
    }

    public static boolean isFile(String path) {
        return new File(path).isFile();
    }

    public static boolean delete(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    public static boolean delete(File file) {
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    public static boolean deleteFolder(String path){
        File file = new File(path);
        if(!file.exists()){
            return false;
        }
        if(file.isFile()){
            return file.delete();
        }
        File[] files = file.listFiles();
        for (File f : files) {
            if(f.isFile()){
                if(!f.delete()){
                    return false;
                }
            }else{
                if(!delete(f.getAbsolutePath())) {
                    return false;
                }
            }
        }
        return file.delete();
    }

}
