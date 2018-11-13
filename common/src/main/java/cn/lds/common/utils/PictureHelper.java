package cn.lds.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sibinbin on 17-12-14.
 */

public class PictureHelper {

    private static final String TAG = "PictureHelper";
    /**
     * 进入相册
     */
    public static void enterAlbum(Activity context,int flag) {
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        context.startActivityForResult(intent,flag);
    }

    /**
     * 启动相机照相
     * @param context 上下文对象
     * @param flag 启动标示
     * @param path 照片存储路径
     *             兼容7.0
     */
    public static void takePhoto(Activity context,int flag,String path){

        //启动相机
        // 判断存储卡是否可以用，可用进行存储
        if (FileHelper.existSDCard()) {
            File file = new File(path);
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs(); // 如果不存在则创建
            if (file.exists()) {
                file.delete();
            }
            if (Build.VERSION.SDK_INT >= 24) {
                 StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                 StrictMode.setVmPolicy(builder.build());
                 builder.detectFileUriExposure();
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            context.startActivityForResult(intent, flag);
        }

    }
    /**
     * 剪切图片
     *
     * @param uri 图片Uri
     * @param temps
     */
    public static void crop(Activity context, int flag, Uri uri, File temps) {
        Intent intents = new Intent("com.android.camera.action.CROP");
        intents.setDataAndType(uri, "image/*");
        intents.putExtra("output", Uri.fromFile(temps));
        intents.putExtra("crop", "true");
        intents.putExtra("aspectX", 1);// 裁剪框比例
        intents.putExtra("aspectY", 1);
        intents.putExtra("outputX", 240);// 输出图片大小
        intents.putExtra("outputY", 240);
        intents.putExtra("outputFormat", "JPEG"); // 输入文件格式
        context.startActivityForResult(intents, flag);
    }

    /**
     * 剪切图片
     *
     * @param uri 图片Uri
     * @param temps
     */
    public static void cropCar(Activity context, int flag, Uri uri, File temps) {
        Intent intents = new Intent("com.android.camera.action.CROP");
        intents.setDataAndType(uri, "image/*");
        intents.putExtra("output", Uri.fromFile(temps));
        intents.putExtra("crop", "true");
        intents.putExtra("aspectX", 4);// 裁剪框比例
        intents.putExtra("aspectY", 3);
        intents.putExtra("outputX", 320);// 输出图片大小
        intents.putExtra("outputY", 240);
        intents.putExtra("outputFormat", "JPEG"); // 输入文件格式
        context.startActivityForResult(intents, flag);
    }

    /**
     * @param path    路径
     * @param maxSize 图片的大小（kb）
     */
    public static String compressPic(String path, int maxSize) {
        String newPath = FileHelper.getCarCropPath();
        Bitmap bitmap = null;
        try {
            if(path.contains("raw")){
                String[] raws = path.split("raw");
                path = raws[1];
            }
            bitmap = compressImage(path, 480);
            if (bitmap == null) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 40;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            while (baos.toByteArray().length / 1024 > maxSize) {
                // 重置baos即清空baos
                baos.reset();
                // 每次都减少10
                options -= 10;
                // 这里压缩options%，把压缩后的数据存放到baos中
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }

            File myFile = new File(newPath);
            if (!myFile.getParentFile().exists()) {
                myFile.getParentFile().mkdirs();
            }

            FileOutputStream fOut = null;
            fOut = new FileOutputStream(myFile);
            fOut.write(baos.toByteArray());
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.d(TAG, "compressPic: ");
        }
        return newPath;
    }
    /**
     * 压缩图片。
     *
     * @param path 图片路径
     * @param size 图片最大尺寸
     * @return 压缩后的图片
     * @throws java.io.IOException
     */
    public static Bitmap compressImage(String path, int size) throws IOException {
        Bitmap bitmap = null;
        // 取得图片
        InputStream is = new FileInputStream(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 这个参数代表，不为bitmap分配内存空间，只记录一些该图片的信息（例如图片大小），说白了就是为了内存优化
        options.inJustDecodeBounds = true;
        // 通过创建图片的方式，取得options的内容（这里就是利用了java的地址传递来赋值）
        BitmapFactory.decodeStream(is, null, options);
        // 关闭流
        is.close();


        int scalesize = 1;

        if (options.outWidth < size && options.outHeight < size) {

        } else {
            if (options.outWidth >= options.outHeight) {
                scalesize = (int) (options.outWidth / size);
            } else {
                scalesize = (int) (options.outHeight / size);
            }
        }

        // 重新取得流，注意：这里一定要再次加载，不能二次使用之前的流！
        is = new FileInputStream(path);
        // 这个参数表示 新生成的图片为原始图片的几分之一。
        options.inSampleSize = scalesize;
        // 这里之前设置为了true，所以要改为false，否则就创建不出图片
        options.inJustDecodeBounds = false;
        // 使用RGB565，否则会OOM
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // 同时设置才会有效
        options.inPurgeable = true;
        // 当系统内存不够时候图片自动被回收
        options.inInputShareable = true;
        // 创建Bitmap
        bitmap = BitmapFactory.decodeStream(is, null, options);
        return bitmap;
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public static String compressImageAndBase64EnCode(String h5PhotoPath, int maxSize) {
        String afterCompressPath = compressPic(h5PhotoPath, maxSize);
        return encode(afterCompressPath);


    }
    private static String encode(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
        return new String(encode);
    }
}
