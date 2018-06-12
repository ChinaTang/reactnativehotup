package com.hotupdate.basebuild.baseaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hotupdate.ConstantFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

/**
 * Created by tangdi on 6/8/18.
 */

public class IncrementMoudle {

    private static final String TAG = "IncrementMoudle";

    private Decompression decompression;

    private String downloadPath;

    private String decompressionPath;

    /**
     * bundle文件已经解压完成， 将会被拷贝到另外的文件夹中，建议是用户无权限的目录
     */
    private String copyPath;



    /**
     * 所有操作的临时目录，不能和目标目录相同，在所有操作完成的之前，Bundle 和资源文件将存放在这里。
     */
    private String tmpFile;

    /**
     * 差异文件名称
     */
    private String patFileName;

    private String detailName;

    private String baseVersionPath;

    public void setBaseVersionPath(String baseVersionPath) {
        this.baseVersionPath = baseVersionPath;
    }

    public void setPatFileName(String patFileName) {
        this.patFileName = patFileName;
    }

    public void setTmpFile(String tmpFile) {
        if(!tmpFile.endsWith(File.separator)){
            tmpFile += File.separator;
        }
        this.tmpFile = tmpFile;
    }

    protected IncrementMoudle(){

    }

    public void setDecompression(Decompression decompression) {
        this.decompression = decompression;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public void setDecompressionPath(String decompressionPath) {
        if(!decompressionPath.endsWith(File.separator)){
            decompressionPath += File.separator;
        }
        this.decompressionPath = decompressionPath;
    }

    public void setDetailName(String detailName) {
        this.detailName = detailName;
    }

    public void setCopyPath(String copyPath) {
        if(!copyPath.endsWith(File.separator)){
            copyPath += File.separator;
        }
        this.copyPath = copyPath;
    }

    public  void increment(final Context context, final long completeId){

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(decompression == null){
                    decompression = new ZipDecompression();
                }
                SharedPreferences sharedPreferences = context.getSharedPreferences("download", context.MODE_PRIVATE);
                String jsVersion = sharedPreferences.getString(ConstantFile.UPDATE_VERSION + completeId, "0");
                String checkCode = sharedPreferences.getString(ConstantFile.ZIP_MD5 + completeId, "0");
                String baseVersion = sharedPreferences.getString(ConstantFile.BASE_VERSION + completeId, "0");

                SharedPreferences.Editor editor = sharedPreferences.edit();

                Log.d(TAG, "===================================\n" + jsVersion + "增量下载完成\n" + "===================================");

                if(decompression.checkZip(checkCode, downloadPath)){
                    Log.d(TAG, "===================================\n" + jsVersion + "zip增量MD5校验完成\n" + "===================================");

                    decompression.decompression(downloadPath, decompressionPath, context);
                    Log.d(TAG, "===================================\n" + jsVersion + "zip增量解压完成\n" + "===================================");

                    File src = new File(baseVersionPath);
                    File dest = new File(tmpFile + "tmp" + jsVersion);
                    try {
                        ToolsModule.copyFolder(src, dest);
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                    mergePatAndAsset(tmpFile + "tmp" + jsVersion + File.separator, decompressionPath + jsVersion + File.separator);
                    File file = new File(tmpFile + "tmp"  + jsVersion);
                    //重命名文件
                    try {
                        if(!ToolsModule.CheckDetail(tmpFile + "tmp" + jsVersion + File.separator, tmpFile + "tmp" + jsVersion + File.separator + detailName)){
                            ToolsModule.deleteFile(file);
                            Log.d(TAG, "===================================\n" + jsVersion + "Detail文件校验失败\n" + "===================================");

                        }else{
                            boolean isSucess = file.renameTo(new File(copyPath + jsVersion));
                            if(isSucess){
                                Log.d(TAG, "===================================\n" + jsVersion + "重命名完成\n" + "===================================");
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d(TAG, "===================================\n" + jsVersion + e.getMessage() + "===================================");
                        ToolsModule.deleteFile(file);

                    }
                }
                File file = new File(downloadPath);
                file.delete();
                editor.remove(ConstantFile.UPDATE_VERSION + completeId);
                editor.remove(ConstantFile.ZIP_MD5 + completeId);
                editor.remove(ConstantFile.HOT_UPDATE_ID + completeId);
                editor.remove(ConstantFile.BASE_VERSION + completeId);
                editor.apply();

            }
        }).start();
    }

    /**
     * 读取文件
     * @param fileName
     * @return
     */
    public static String readContentFromFile(String fileName) {
        StringBuilder strPatchCnt = new StringBuilder();

        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
            String tempStr = "";
            while ((tempStr = bufferedReader.readLine()) != null) {

                strPatchCnt.append(tempStr);
                strPatchCnt.append("\n");
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return strPatchCnt.toString();
    }

    /**
     * 合并代码
     * @param scrPath， 差异文件地址
     * @param jsPath， 基础版本目录
     */
    private void mergePatAndAsset(String scrPath, String jsPath) {

        File file1 = new File(jsPath);
        if(file1.exists()){
            String assetsBundle = readContentFromFile(scrPath + ConstantFile.JS_ENTY);
            String patcheStr = readContentFromFile(jsPath + patFileName);
            // 3.初始化 dmp
            diff_match_patch dmp = new diff_match_patch();
            // 4.转换pat
            LinkedList<diff_match_patch.Patch> pathes = (LinkedList<diff_match_patch.Patch>) dmp.patch_fromText(patcheStr);
            // 5.与assets目录下的bundle合并，生成新的bundle
            Object[] bundleArray = dmp.patch_apply(pathes,assetsBundle);
            // 6.保存新的bundle

            BufferedWriter bufferedWriter;
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(new File(jsPath + ConstantFile.JS_ENTY)));
                bufferedWriter.write(bundleArray[0].toString());
                bufferedWriter.close();
                //更新完毕，删除更新文件
                File file = new File(jsPath + patFileName);
                file.delete();

                String md5 = MD5Util.getMD5(jsPath + ConstantFile.JS_ENTY);
                Log.d("TAG", md5);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        ToolsModule.copyResource(jsPath, scrPath);
        //全部操作完成删除临时文件
        File file = new File(jsPath);
        ToolsModule.deleteFile(file);
    }

}
