package cn.lds.common.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.lds.common.constants.Constants;

public class FileHelper {

    public static final String rootPathName = "com.cusc.lieparrdcar";// 根目录

    public static final String downloadPathName = "Download";// 下载文件存放根目录

    public static final String avatarPathName = "avatar";// 头像文件存放本地目录

    public static final String avatarName = "temp_photo.jpg";// 头像文件存放本地目录

    public static final String carPathName = "car";// 车辆文件存放本地目录

    public static final String carName = "car_photo.jpg";// 车辆文件存放本地目录

    public static final String RTF_IMAGE_JPG = ".jpg";// 图片文件后缀


    /**
     * 检查内存卡是否可用
     *
     * @return
     */
    public static boolean existSDCard() {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }
    /**
     * 调用系统相机头像图片存储位置 生成path
     *
     * @return
     */
    public static String getCropPath() {
        StringBuffer stbPath = new StringBuffer();
        if (existSDCard()) {

            stbPath.append(Environment.getExternalStorageDirectory().getPath());
            stbPath.append(File.separator);
        }
        stbPath.append(rootPathName);
        stbPath.append(File.separator);
        stbPath.append(CacheHelper.getAccount());
        stbPath.append(File.separator);
        stbPath.append(avatarPathName);
        stbPath.append(File.separator);
        return stbPath.toString() + getAvatarName(CacheHelper.getLoginId());
    }
    /**
     * 调用系统相机车辆图片存储位置 生成path
     *
     * @return
     */
    public static String getCarCropPath() {
        StringBuffer stbPath = new StringBuffer();
        if (existSDCard()) {

            stbPath.append(Environment.getExternalStorageDirectory().getPath());
            stbPath.append(File.separator);
        }
        stbPath.append(rootPathName);
        stbPath.append(File.separator);
        stbPath.append(CacheHelper.getAccount());
        stbPath.append(File.separator);
        stbPath.append(carPathName);
        stbPath.append(File.separator);
        return stbPath.toString() + getpicname();
    }
    /**
     * 随机生成图片文件名 文件名是当前时间加六位随机数
     *
     * @return
     */
    public static String getAvatarName(String loginId) {

//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
//        String str = formatter.format(curDate);
//        int i = (int) (Math.random() * (999999 - 100000) + 100000);

        return loginId + RTF_IMAGE_JPG;
    }
    /**
     * 随机生成图片文件名 文件名是当前时间加六位随机数
     *
     * @return
     */
    public static String getpicname() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        int i = (int) (Math.random() * (999999 - 100000) + 100000);

        return str + String.valueOf(i) + RTF_IMAGE_JPG;
    }
    /**
     * 车辆图片存放地址
     *
     * @return
     */
    public static String getTakeCarPath() {
        StringBuffer stbPath = new StringBuffer();
        if (existSDCard()) {
            stbPath.append(Environment.getExternalStorageDirectory().getPath());
            stbPath.append(File.separator);
        }
        stbPath.append(rootPathName);
        stbPath.append(File.separator);
        stbPath.append(downloadPathName);
        stbPath.append(File.separator);
        stbPath.append(carPathName);
        stbPath.append(File.separator);
        stbPath.append(carName);
        return stbPath.toString();
    }
    /**
     * 头像存放地址
     *
     * @return
     */
    public static String getTakeAvatarPath() {
        StringBuffer stbPath = new StringBuffer();
        if (existSDCard()) {
            stbPath.append(Environment.getExternalStorageDirectory().getPath());
            stbPath.append(File.separator);
        }
        stbPath.append(rootPathName);
        stbPath.append(File.separator);
        stbPath.append(downloadPathName);
        stbPath.append(File.separator);
        stbPath.append(avatarPathName);
        stbPath.append(File.separator);
        stbPath.append(avatarName);
        return stbPath.toString();
    }
    /**
     * 车辆图片存放地址
     *
     * @return
     */
    public static String getCarIconPath() {
        StringBuffer stbPath = new StringBuffer();
        if (existSDCard()) {
            stbPath.append(Environment.getExternalStorageDirectory().getPath());
            stbPath.append(File.separator);
        }
        stbPath.append(rootPathName);
        stbPath.append(File.separator);
        stbPath.append(downloadPathName);
        stbPath.append(File.separator);
        stbPath.append(carPathName);
        stbPath.append(File.separator);
        stbPath.append(carName);
        return stbPath.toString();
    }
    /**
     * 获取车辆图片压缩路径
     * @return
     */
    public static File getCarTemps(){
        File temps = new File(getCarCropPath());
        if (!temps.getParentFile().exists())
            temps.getParentFile().mkdirs();
        return temps;
    }
    /**
     * 获取头像图片压缩路径
     * @return
     */
    public static File getTemps(){
        File temps = new File(getCropPath());
        if (!temps.getParentFile().exists())
            temps.getParentFile().mkdirs();
        return temps;
    }

    public static void copyAssets( Context context, String assetDir, String dir) {
        String[] files;
        try {
            files = context.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return;
        }
        File mWorkingPath = new File(dir);
        // if this directory does not exists, make one.
        if (!mWorkingPath.exists()) {
            if (!mWorkingPath.mkdirs()) {

            }
        }

        for (int i = 0; i < files.length; i++) {
            try {
                String fileName = files[i];
                // we make sure file name not contains '.' to be a folder.
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        copyAssets(context, fileName, dir + fileName + "/");
                    } else {
                        copyAssets(context, assetDir + "/" + fileName, dir+ fileName + "/");
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists())
                    outFile.delete();
                InputStream in = null;
                if (0 != assetDir.length())
                    in = context.getAssets().open(assetDir + "/" + fileName);
                else
                    in = context.getAssets().open(fileName);
                OutputStream out = new FileOutputStream(outFile);

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyAssetFile( Context context, String path, String destinationPath ) {
        AssetManager assetManager = context.getAssets();
        String assets[] = null;
        try {
            Log.i("tag", "copyFileOrDir() "+path);
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(context,path,destinationPath);
            } else {
                String fullPath =  destinationPath + path;
                Log.i("tag", "path="+fullPath);
                File dir = new File(fullPath);
                if (!dir.exists() && !path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                    if (!dir.mkdirs())
                        Log.i("tag", "could not create dir "+fullPath);
                for (int i = 0; i < assets.length; ++i) {
                    String p;
                    if (path.equals(""))
                        p = "";
                    else
                        p = path + "/";
//                    if (!path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit"))
//                        copyAssetFileOrDir( p + assets[i],destinationPath);
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }
    private static void copyFile(Context context,String filename,String destinationPath) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        String newFileName = null;
        try {
            Log.i("tag", "copyFile() "+filename);
            in = assetManager.open(filename);
            newFileName = destinationPath;
            out = new FileOutputStream(newFileName);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of "+newFileName);
            Log.e("tag", "Exception in copyFile() "+e.toString());
        }
    }


}
