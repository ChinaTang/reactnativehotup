package com.hotupdate.basebuild.baseaction;

import android.content.Context;

/**
 * Created by tangdi on 6/8/18.
 */

public interface Decompression {
    /**
     * 解压接口，默认实现zip
     * @param zipPath zip包地址
     * @param destPath 目标地址
     * @param context
     */
    void decompression(String zipPath, String destPath, Context context);

    /**
     * 默认校验MD5
     * @param checkCode
     * @param zipPath
     * @return
     */
    boolean checkZip(String checkCode, String zipPath);
}
