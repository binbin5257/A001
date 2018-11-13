package cn.lds.common.base;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import cn.lds.common.constants.Constants;


/**
 * Created by leadingsoft on 2017/11/29.
 */

public class BaseApplication extends Application {
    private static final String TAG = BaseApplication.class.getSimpleName();

    public static BaseApplication instance = null;

    public static String jbgsn = "";
    private Handler handler;
    // 准备退出
    public static boolean isExiting = false;

    public synchronized static BaseApplication getInstance() {
        if (instance == null) {
            instance = new BaseApplication();
        }
        return instance;
    }

    public BaseApplication() {
        instance = this;
        handler = new Handler();
    }


    // 缓存
    private ACache aCache = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 获取cache实例
     *
     * @return
     */
    public ACache getCache() {

        if (null == aCache)
            aCache = ACache.get(this);

        return aCache;
    }

    /**
     * handler 主线程post
     *
     * @param runnable
     *         异步线程
     */
    public void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    /**
     * handler 延长主线程post
     *
     * @param runnable
     *         异步线程
     * @param delay
     *         延长时间
     */
    public void runOnUiThreadDelay(Runnable runnable, long delay) {
        handler.postDelayed(runnable, delay);
    }

    /**
     * handler
     *
     * @return handler
     */
    public Handler getHandler() {
        return handler;
    }


    /**
     * 2秒内再次按下返回键，退出app
     */
    public void exitApp() {
        isExiting = true;
        runOnUiThreadDelay(new Runnable() {
            @Override
            public void run() {
                isExiting = false;
            }
        }, 2000);
    }

    /**
     * 发送退出应用的广播
     */
    public void sendLogoutBroadcast(String filter, String log) {
        Intent intent = new Intent(filter);
        intent.putExtra("log", log);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().sendBroadcast(intent);
    }
}
