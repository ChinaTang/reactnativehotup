package com.hotupdate.basebuild.baseaction;

/**
 * Created by tangdi on 6/8/18.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hotupdate.ConstantFile;

import java.io.File;
import java.io.IOException;

/**
 * 全量操作
 */
public class FullQuantityMoudle {


    private static final String TAG = "FullQuantityMoudle";

    private Decompression decompression;

    private String downloadPath;

    private String decompressionPath;

    private String detailName;

    /**
     * bundle文件已经解压完成， 将会被拷贝到另外的文件夹中，建议是用户无权限的目录
     */
    private String copyPath;

    private String checkCode;

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    protected FullQuantityMoudle(){}

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

    public void setCopyPath(String copyPath) {
        if(!copyPath.endsWith(File.separator)){
            copyPath = copyPath + File.separator;
        }
        this.copyPath = copyPath;
    }

    public void setDetailName(String detailName) {
        this.detailName = detailName;
    }

    public void fullQuantityAction(final Context context, final long completeId){
        new Thread(new Runnable() {
            @Override
            public void run() {

                if(decompression == null){
                    decompression = new ZipDecompression();
                }

                SharedPreferences sharedPreferences = context.getSharedPreferences("download", context.MODE_PRIVATE);
                String jsVersion = sharedPreferences.getString(ConstantFile.UPDATE_VERSION + completeId, "0");
                String checkCode = sharedPreferences.getString(ConstantFile.ZIP_MD5 + completeId, "0");
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Log.d(TAG, "===================================\n" + jsVersion + "下载完成\n" + "===================================");

                if(decompression.checkZip(checkCode, downloadPath)){
                    Log.d(TAG, "===================================\n" + jsVersion + "zip校验完成\n" + "===================================");

                    decompression.decompression(downloadPath, decompressionPath, context);
                    File src = new File(decompressionPath + jsVersion);
                    File dest = new File(copyPath + jsVersion);
                    try {
                        ToolsModule.copyFolder(src, dest);
                        if(!ToolsModule.CheckDetail(copyPath + jsVersion, copyPath  + jsVersion + File.separator + detailName)){
                            ToolsModule.deleteFile(new File(copyPath + jsVersion));
                            Log.d(TAG, "===================================\n" + jsVersion + "清单校验完成\n" + "===================================");

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "===================================\n" + jsVersion + "出错了\n" + e.getMessage() + "\n" + "===================================");

                        ToolsModule.deleteFile(new File(copyPath + jsVersion));
                        ToolsModule.deleteFile(src);
                    }
                    ToolsModule.deleteFile(src);
                }else{

                }
                File file = new File(downloadPath);
                file.delete();
                editor.remove(ConstantFile.UPDATE_VERSION + completeId);
                editor.remove(ConstantFile.ZIP_MD5 + completeId);
                editor.remove(ConstantFile.BASE_VERSION + completeId);
                editor.remove(ConstantFile.HOT_UPDATE_ID + completeId);
                editor.apply();

            }
        }).start();
    }
}
