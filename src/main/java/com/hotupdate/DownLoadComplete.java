package com.hotupdate;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hotupdate.basebuild.baseaction.FullQuantityMoudle;
import com.hotupdate.basebuild.baseaction.IncrementMoudle;

/**
 * Created by tangdi on 12/13/17.
 */

public class DownLoadComplete {

    public void downLoadComplete(Context context, Intent intent, FullQuantityMoudle fullQuantityMoudle, IncrementMoudle incrementMoudle){
        long completeId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
        SharedPreferences sharedPreferences = context.getSharedPreferences("download", context.MODE_PRIVATE);
        String version = sharedPreferences.getString(ConstantFile.UPDATE_VERSION + completeId, "0");
        String baseVersion = sharedPreferences.getString(ConstantFile.BASE_VERSION + completeId, "0");
        boolean isAll = false;
        if(baseVersion.equals("ALL")){
            isAll = true;
        }else{
            isAll = false;
        }
        long mDownLoadId = sharedPreferences.getLong(ConstantFile.HOT_UPDATE_ID + completeId, 0);
        if(completeId == mDownLoadId) {
            if(!version.equals("0")){
                if(isAll){
                    fullQuantityMoudle.fullQuantityAction(context, completeId);
                }else{
                    if(!baseVersion.equals("0")){
                        incrementMoudle.increment(context, completeId);
                    }
                }
            }
        }
    }


}
