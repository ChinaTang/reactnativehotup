package com.hotupdate.basebuild.baseaction;

import android.content.Context;

/**
 * Created by tangdi on 6/8/18.
 */

public class HotUpdateBuidler {

    private static HotUpdateBuidler hotUpdateBuidler = null;

    private DownloadBundleModule downloadBundleModule;

    private IncrementMoudle incrementMoudle;

    private FullQuantityMoudle fullQuantityMoudle;


    private HotUpdateBuidler(Context context){
        downloadBundleModule = new DownloadBundleModule();
        downloadBundleModule.setContext(context);
        fullQuantityMoudle = new FullQuantityMoudle();
        incrementMoudle = new IncrementMoudle();
    }

    public static HotUpdateBuidler getInstance(Context context){
        if(hotUpdateBuidler == null){
            hotUpdateBuidler = new HotUpdateBuidler(context);
        }
        return hotUpdateBuidler;
    }



    public HotUpdateBuidler setJsDownLoadPath(String jsDownLoadPath) {
        downloadBundleModule.setJsDownLoadPath(jsDownLoadPath);
        fullQuantityMoudle.setDownloadPath(jsDownLoadPath);
        incrementMoudle.setDownloadPath(jsDownLoadPath);
        return this;
    }

    public HotUpdateBuidler setCheckCode(String checkCode) {
        downloadBundleModule.setCheckCode(checkCode);
        fullQuantityMoudle.setCheckCode(checkCode);
        incrementMoudle.setDownloadPath(checkCode);
        return this;
    }



    public class DownloadBundleModuleBuild{
        private DownloadBundleModuleBuild(){}
        public DownloadBundleModuleBuild setBaseVersion(String baseVersion) {
            downloadBundleModule.setBaseVersion(baseVersion);
            return this;
        }

        public DownloadBundleModuleBuild setDownUrl(String downUrl) {
            downloadBundleModule.setDownUrl(downUrl);
            return this;
        }
        public DownloadBundleModuleBuild setUpdateVersion(String updateVersion) {
            downloadBundleModule.setUpdateVersion(updateVersion);
            return this;
        }

        public DownloadBundleModule build(){
            return downloadBundleModule;
        }
    }



    public class FullQuantityMoudleBuild{

        private FullQuantityMoudleBuild(){}

        public FullQuantityMoudleBuild setDecompression(Decompression decompression){
            fullQuantityMoudle.setDecompression(decompression);
            return this;
        }


        public FullQuantityMoudleBuild setDecompressionPath(String decompressionPath){
            fullQuantityMoudle.setDecompressionPath(decompressionPath);
            return this;
        }


        public FullQuantityMoudleBuild setCopyPath(String copyPath){
            fullQuantityMoudle.setCopyPath(copyPath);
            return this;
        }

        public FullQuantityMoudleBuild setDetailName(String detailName){
            fullQuantityMoudle.setDetailName(detailName);
            return this;
        }

        public FullQuantityMoudle build(){
            return fullQuantityMoudle;
        }
    }

    public class IncrementMoudleBuild{

        private IncrementMoudleBuild(){}

        public IncrementMoudleBuild setDecompression(Decompression decompression){
            incrementMoudle.setDecompression(decompression);
            return this;
        }


        public IncrementMoudleBuild setDecompressionPath(String decompressionPath){
            incrementMoudle.setDecompressionPath(decompressionPath);
            return this;
        }


        public IncrementMoudleBuild setCopyPath(String copyPath){
            incrementMoudle.setCopyPath(copyPath);
            return this;
        }

        public IncrementMoudleBuild setTmpFile(String filepath){
            incrementMoudle.setTmpFile(filepath);
            return this;
        }

        public IncrementMoudleBuild setPatFileName(String patName){
            incrementMoudle.setPatFileName(patName);
            return this;
        }

        public IncrementMoudleBuild setDetailName(String detailName){
            incrementMoudle.setDetailName(detailName);
            return this;
        }

        public IncrementMoudleBuild setBaseVersionPath(String baseVersionPath){
            incrementMoudle.setBaseVersionPath(baseVersionPath);
            return this;
        }

        public IncrementMoudle build(){
            return incrementMoudle;
        }


    }


    public DownloadBundleModuleBuild downloadBundleModuleBuild(){
        return new DownloadBundleModuleBuild();
    }

    public FullQuantityMoudleBuild fullQuantityMoudleBuild(){
        return new FullQuantityMoudleBuild();
    }

    public IncrementMoudleBuild incrementMoudleBuild(){
        return new IncrementMoudleBuild();
    }

}
