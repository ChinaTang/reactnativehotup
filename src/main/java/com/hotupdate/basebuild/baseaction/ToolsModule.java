package com.hotupdate.basebuild.baseaction;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.hotupdate.ConstantFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tangdi on 6/8/18.
 */

public class ToolsModule {
    /**
     * 复制一个目录及其子目录、文件到另外一个目录
     * @param src
     * @param dest
     * @throws IOException
     */
    protected static void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdirs();
            }
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // 递归复制
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    /**
     * 拷贝文件
     * @param path 拷贝的文件
     * @param jsPath  最终文件夹
     */
    public static void copyResource(String path, String jsPath){
        File file1=new File(path);
        File[] fs=file1.listFiles();
        File file2=new File(jsPath);
        if(!file2.exists()){
            file2.mkdirs();
        }
        for (File f : fs) {
            if(f.isFile()){
                fileCopy(f.getPath(),jsPath+ File.separator +f.getName()); //调用文件拷贝的方法
            }else if(f.isDirectory()){
                copyResource(f.getPath(),jsPath+ File.separator +f.getName());
            }
        }
    }

    /**
     * 文件拷贝的方法
     */
    private static void fileCopy(String src, String des) {
        File desFile = new File(des);
        if(desFile.exists()){
            desFile.delete();
            try {
                desFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        InputStream br=null;
        OutputStream ps=null;

        try {
            br=new FileInputStream(new File(src));
            ps=new FileOutputStream(new File(des));
            int lenght = 0;
            byte[] bytes = new byte[1024];
            while((lenght = br.read(bytes)) > 0){
                ps.write(bytes, 0, lenght);
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{

            try {
                if(br!=null)  br.close();
                if(ps!=null)  ps.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }


    }

    /**
     * 删除文件
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.exists()) {//判断文件是否存在
            if (file.isFile()) {//判断是否是文件
                file.delete();//删除文件
            } else if (file.isDirectory()) {//否则如果它是一个目录
                File[] files = file.listFiles();//声明目录下所有的文件 files[];
                for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件
                    deleteFile(files[i]);//把每个文件用这个方法进行迭代
                }
                file.delete();//删除文件夹
            }
        } else {
            System.out.println("所删除的文件不存在");
        }
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
     * 权限控制
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    /**
     *
     * @param jsPath
     * @param detail
     * @return
     * @throws FileNotFoundException
     */
    public static boolean CheckDetail(String jsPath, String detail) throws FileNotFoundException {

        if(!jsPath.endsWith(File.separator)){
            jsPath = jsPath + File.separator;
        }


        InputStream is = new FileInputStream(detail);
        InputStreamReader streamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(streamReader);
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            //Bundle文件验证
            String md5 = MD5Util.getMD5(jsPath + ConstantFile.JS_ENTY);
            if(!jsonObject.getString("Bundle").equals(md5)){
                return false;
            }
            JSONObject resource = jsonObject.getJSONObject("Resource");
            JSONArray drawablehdpi = resource.getJSONArray("drawable-hdpi");
            JSONArray drawablemdpi = resource.getJSONArray("drawable-mdpi");
            JSONArray drawablexhdpi = resource.getJSONArray("drawable-xhdpi");
            JSONArray drawablexxhdpi = resource.getJSONArray("drawable-xxhdpi");
            JSONArray drawablexxxhdpi = resource.getJSONArray("drawable-xxxhdpi");
            if(checkResource(drawablehdpi, jsPath + "drawable-hdpi")
                    && checkResource(drawablemdpi, jsPath + "drawable-mdpi")
                    && checkResource(drawablexhdpi, jsPath + "drawable-xhdpi")
                    && checkResource(drawablexxhdpi, jsPath + "drawable-xxhdpi")
                    && checkResource(drawablexxxhdpi, jsPath + "drawable-xxxhdpi")){
                return true;
            }else{
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean checkResource(JSONArray jsonArray, String path) throws JSONException {
        File filedrawablehdpi = new File(path);
        List<String> drawablehdpilist = Arrays.asList(filedrawablehdpi.list());

        for(int i = 0; i < jsonArray.length(); i++){
            if(!drawablehdpilist.contains(jsonArray.getString(i))){
                return false;
            }
        }
        return true;
    }
}
