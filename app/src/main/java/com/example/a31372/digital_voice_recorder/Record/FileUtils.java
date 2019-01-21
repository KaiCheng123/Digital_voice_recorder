package com.example.a31372.digital_voice_recorder.Record;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;
import java.util.Date;

public class FileUtils {
    private String SDPATH ;
    private String path;

    public static String getWavFileAbsolutePath(String fileName) {
        String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record_test/"+fileName+".wav";
        return destinationPath;
    }

    public String getSDPATH(){
        return SDPATH;
    }

    public String getFilePath(){
        return SDPATH + path;
    }


    public FileUtils(){
        //得到当前外部存储设备的目录( /SDCARD )
        SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        path = "record_test";
    }

    public static String getPcmFileAbsolutePath(String currentFileName) {
        String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record_test/"+currentFileName+".pcm";
        return destinationPath;
    }

    /**
     * 在SD卡上创建目录
     * @param dirName
     * @return
     */
    public void createSDDir(String dirName){
        File dir = new File(SDPATH + dirName);
        if (!dir.exists()){
            dir.mkdir();
            Log.e("aaa","文件夹建立成功"+SDPATH + dirName);
        }else {
            Log.e("bbb","文件夹建立不成功"+SDPATH + dirName);
        }
    }

    /**
     * 遍历文件夹的内容
     * @return files
     */
    public File[] fileslist(){
        File specItemDir = new File(getFilePath());
        if(!specItemDir.exists()){
            specItemDir.mkdir();
        }
        final File[] files = specItemDir.listFiles();
        return files;
    }

}
