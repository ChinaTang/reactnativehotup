package com.hotupdate.basebuild.baseaction;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.hotupdate.ConstantFile;

import java.io.File;

/**
 * Created by tangdi on 6/8/18.
 */

public class DownloadBundleModule {

    private String jsDownLoadPath;

    private Context context;

    /**
     * 更新包的名字，后缀将决定解压的方式，默认解压方式为zip
     */
    private String updateVersion;

    private String downUrl;

    /**
     * 校验码， 默认校验方式为MD5, 若为空字符串，则不进行校验
     */
    private String checkCode;

    /**
     * 基于版本进行增量更新，若是全量更新传入"ALL"
     */
    private String baseVersion;

    protected void setJsDownLoadPath(String jsDownLoadPath) {
        this.jsDownLoadPath = jsDownLoadPath;
    }

    protected void setContext(Context context) {
        this.context = context;
    }

    protected void setUpdateVersion(String updateVersion) {
        this.updateVersion = updateVersion;
    }

    protected void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    protected void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    protected void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }

    protected DownloadBundleModule(){

    }

    public Long downLoadBundle() {
        File zipfile;
        // 1.检查是否存在pat压缩包,存在则删除
        zipfile = new File(jsDownLoadPath + updateVersion);
        if (zipfile != null && zipfile.exists()) {
            zipfile.delete();
        }
        // 2.下载
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager
                .Request(Uri.parse(downUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setDestinationUri(Uri.parse("file://" + jsDownLoadPath));
        SharedPreferences.Editor editor = context.getSharedPreferences("download", context.MODE_PRIVATE).edit();
        long id = downloadManager.enqueue(request);
        editor.putLong(ConstantFile.HOT_UPDATE_ID + id, id);
        editor.putString(ConstantFile.UPDATE_VERSION + id, updateVersion);
        editor.putString(ConstantFile.BASE_VERSION + id, baseVersion);
        editor.putString(ConstantFile.ZIP_MD5 + id, checkCode);
        editor.putString(ConstantFile.DOWNLOAD_PATH, jsDownLoadPath);
        editor.apply();
        //query(downloadManager);
        return id;
    }
}
