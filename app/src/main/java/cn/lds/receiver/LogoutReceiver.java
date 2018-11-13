package cn.lds.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.jpush.android.api.JPushInterface;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.manager.ActivityManager;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.table.base.DBManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.LogHelper;
import cn.lds.ui.LoginActivity;
import io.realm.Realm;


/**
 * 注销
 *
 * @author user
 */
public class LogoutReceiver extends BroadcastReceiver {
    public static String _TAG = LogoutReceiver.class.getSimpleName();
    private Context context;
    private String account;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        account = CacheHelper.getAccount();
        LogHelper.d("数据库共通：收到注销通知！");
        // 清除通知栏
        JPushInterface.clearAllNotifications(context);
        JPushInterface.cleanTags(context, 100);
        // 向服务器提交注销通知
        RequestManager.getInstance().get(ModuleUrls.logout, HttpApiKey.logout);
        // 清除相关缓存
//        DBManager.getInstance().getRealm().executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                realm.deleteAll();
//            }
//        });
//        BaseApplication.getInstance().getCache().clear();
        // 清除API请求的cookie
//        RequestManager.getInstance().

        ActivityManager.fillActivity();
        android.app.NotificationManager manger = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manger.cancel(0);

        Intent i = new Intent(context, LoginActivity.class);
        i.setAction(intent.getStringExtra("filter"));
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("log", intent.getStringExtra("log"));
        context.startActivity(i);
    }


}
