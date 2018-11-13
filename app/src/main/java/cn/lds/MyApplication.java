package cn.lds;


import android.content.Context;
import android.content.IntentFilter;
import android.support.multidex.MultiDex;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import cn.jpush.android.api.JPushInterface;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.constants.Constants;
import cn.lds.common.data.ServerHostModel;
import cn.lds.common.manager.ConfigManager;
import cn.lds.common.manager.ImageManager;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.manager.UMengManager;
import cn.lds.common.manager.WeChatShare;
import cn.lds.common.table.base.DBManager;
import cn.lds.common.table.base.MyMigration;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.receiver.LogoutReceiver;

/**
 * Created by leadingsoft on 2017/11/29.
 */
@ReportsCrashes(
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "http://47.95.230.159:5984/acra-leo/_design/acra-storage/_update/report",
        formUriBasicAuthLogin = "leo",
        formUriBasicAuthPassword = "rd123456"
        //maven { url 'http://repo2.maven.org/maven2/' }
)
public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ConfigManager.getInstance().setModel(GsonImplHelp.get().toObject(BuildConfig.SERVER_HOST, ServerHostModel.class));

        ImageManager.getInstance().initFresco(getApplicationContext(), RequestManager.getInstance().getmOkHttpClient());//自定义 httpclient 保持session cookie 一致
        DBManager.getInstance().initDB(getApplicationContext(), "cn.lds_database", 8, new MyMigration());
        //jpush 初始化
        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());
        //umeng 初始化
        UMengManager.getInstance().initUment(getApplicationContext(), true);
        //过滤器
        IntentFilter mIntentFilter = new IntentFilter(Constants.SYS_CONFIG_LOGOUT_FLITER);
        //创建广播接收者的对象
        LogoutReceiver mMyBroadcastRecvier =  new LogoutReceiver();
        //注册广播接收者的对象
        registerReceiver(mMyBroadcastRecvier, mIntentFilter);
        //注册微信分享
        registerWeChat(this);

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        ACRA.init(this);





    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void registerWeChat(Context context) {   //向微信注册app
        WeChatShare.regToWx(context);
    }


}
