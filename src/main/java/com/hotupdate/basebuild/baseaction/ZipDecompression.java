package com.hotupdate.basebuild.baseaction;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by tangdi on 6/8/18.
 */

public class ZipDecompression implements Decompression {

    /**
     *
     * @param path
     */
    @Override
    public void decompression(String path, String destPath, Context context) {

        File file = new File(destPath);
        if(!file.exists()){
            file.mkdir();
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ZipInputStream inZip = new ZipInputStream(fileInputStream);
            ZipEntry zipEntry;
            String szName;
            try {
                while((zipEntry = inZip.getNextEntry()) != null) {

                    szName = zipEntry.getName();
                    if(zipEntry.isDirectory()) {
                        szName = szName.substring(0, szName.length() - 1);
                        File folder = new File(destPath + File.separator + szName);
                        if(!folder.exists()){
                            folder.mkdirs();
                        }
                    }else{
                        File file1 = new File(destPath + File.separator + szName);
                        if(!file1.getParentFile().exists()){
                            file1.getParentFile().mkdirs();
                        }
                        boolean s = file1.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file1);
                        int len;
                        byte[] buffer = new byte[1024];

                        while((len = inZip.read(buffer)) != -1) {
                            fos.write(buffer, 0 , len);
                            fos.flush();
                        }
                        fos.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            inZip.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证ZipMD5
     * @param checkCode
     * @param zipPath
     * @return
     */
    @Override
    public boolean checkZip(String checkCode, String zipPath) {
        try {
            return checkCode.equals(MD5Util.getMD5(zipPath));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }




}
