package cn.lds.common.manager;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import cn.lds.common.enums.DeviceType;

/**
 * 友盟统计管理中心
 * Created by leadingsoft on 17/12/7.
 */

public class UMengManager {


    private static UMengManager mInstance;//单利引用
    private Context mContext;

    public static UMengManager getInstance() {
        UMengManager inst = mInstance;
        if (inst == null) {
            synchronized (UMengManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new UMengManager();
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    /**
     * 初始化友盟
     *
     * @param context
     *         上下文
     * @param debug
     *         是否启动debug模式
     */
    public void initUment(Context context, boolean debug) {
        UMConfigure.init(context, "5ac490a7a40fa327e40001a3", "Channel", DeviceType.PHONE.getValue(), "");
        UMConfigure.setLogEnabled(debug);
        mContext = context;
    }

    /**
     * 统计登录账号
     */
    public void profileSignIn(String userId) {
        MobclickAgent.onProfileSignIn(userId);
    }

    /**
     * 界面停留时间统计 start
     */
    public void onResumeSession(Context context) {
        MobclickAgent.onResume(context);
    }

    public void onPauseSession(Context context) {
        MobclickAgent.onPause(context);
    }


    public void onResumePage(String className) {
        MobclickAgent.onPageStart(className);
    }

    public void onPausePage(String className) {
        MobclickAgent.onPageEnd(className);
    }

    /**
     *界面停留时间统计 end
     */

    /**
     * 计数事件
     *
     * @param eventId
     *         自定义事件
     */
    public void onClick(String eventId) {
        MobclickAgent.onEvent(mContext, eventId);
    }

}
