package cn.lds.common.manager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.lds.common.file.OnDownloadListener;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.constants.Constants;
import cn.lds.common.utils.CacheHelper;
import cn.lds.widget.dialog.CircleProgressDialog;
import cn.lds.widget.dialog.VersionUpdateDialog;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 版本管理中心
 *
 * @author user
 */
public class VersionManager {
    private static VersionManager mInstance;//单利引用
    private Context mContext;

    private static String saveFileName;

    public static VersionManager getInstance() {
        VersionManager inst = mInstance;
        if (inst == null) {
            synchronized (VersionManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new VersionManager();
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    public VersionManager() {
        mContext = BaseApplication.getInstance().getApplicationContext();
    }


    /**
     * @param url
     *         下载连接
     * @param listener
     *         下载监听
     */
    public void downloadApk(final String url, final OnDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        RequestManager.getInstance().getmOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savepath = isExistDir(Constants.SYS_CONFIG_FILE_PATH);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savepath, getNameFromUrl(url));
                    saveFileName = file.getAbsolutePath();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    listener.onDownloadSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 是存在该文件路径
     *
     * @param saveDir
     * @return
     * @throws IOException
     *         判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * 获取文件名字
     *
     * @param url
     * @return 从下载连接中解析出文件名
     */
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 安装apk
     */
    public void installApp() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        } else {//Android7.0之后获取uri要用contentProvider
            Uri uri = FileProvider.getUriForFile(mContext,"leopaard.com.leopaardapp.file_provider",apkfile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
    /**
     * 获取本地软件版本号
     */
    public static int getLocalVersion(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 检查app版本，更新，安装
     * @param activity 上下文
     * @param serviceVersionCode 版本号
     * @param mustUpdate 是否强制更新
     * @param versionInfo 版本信息
     * @param downApkUrl apk下载地址
     */
    public static void checkAppVersion( final Activity activity, int serviceVersionCode, boolean mustUpdate, String versionInfo, final String downApkUrl) {
        int localVersionCode = getLocalVersion(activity);
        if(serviceVersionCode > localVersionCode){
            VersionUpdateDialog updateDialog = new VersionUpdateDialog(activity).setOnDialogClickListener(new OnDialogClickListener() {
                @Override
                public void onDialogClick( Dialog dialog, String clickPosition) {
                    dialog.dismiss();
                    switch (clickPosition) {
                        case ClickPosition.SUBMIT:

                            final CircleProgressDialog circleProgressDialog = new CircleProgressDialog(activity);
                            circleProgressDialog.show();
                            VersionManager.getInstance().downloadApk(downApkUrl, new OnDownloadListener() {
                                @Override
                                public void onDownloadSuccess() {
                                    circleProgressDialog.dismiss();
                                    VersionManager.getInstance().installApp();
                                }

                                @Override
                                public void onDownloading(final int progress) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            circleProgressDialog.setProgress(progress);
                                        }
                                    });
                                }

                                @Override
                                public void onDownloadFailed() {
                                    circleProgressDialog.dismiss();
                                }
                            });
                            break;
                    }
                }
            });
            if (mustUpdate) {//强制更新
                updateDialog.setMustUpdate(true);
            } else {
                updateDialog.setMustUpdate(false);
            }
            updateDialog.setTitle("版本更新");
            updateDialog.setRightButtonText("更新");
            updateDialog.setUpdateContent(versionInfo);
            updateDialog.show();
        }
    }

}
