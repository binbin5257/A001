package cn.lds.ui;

import java.io.File;

import cn.lds.common.base.BaseActivity;


/**
 * Created by quwei on 2016/4/15.
 */
public class BaseWebviewActivity extends BaseActivity {

    private String _TAG = BaseWebviewActivity.class.getSimpleName();
    private static final String APP_CACAHE_DIRNAME = "/webcache";

    /**
     * 清除WebView缓存
     */
    public void clearWebViewCache() {
        //清理Webview缓存数据库
        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
            //WebView 缓存文件
            File appCacheDir = new File(getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME);
            File webviewCacheDir = new File(getCacheDir().getAbsolutePath() + "/webviewCache");

            //删除webview 缓存目录
            if (webviewCacheDir.exists()) {
                deleteFile(webviewCacheDir);
            }
            //删除webview 缓存 缓存目录
            if (appCacheDir.exists()) {
                deleteFile(appCacheDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }
}
