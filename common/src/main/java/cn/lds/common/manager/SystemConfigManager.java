package cn.lds.common.manager;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.data.LoginModel;
import cn.lds.common.data.SystemConfigModel;
import cn.lds.common.enums.DeviceType;
import cn.lds.common.enums.OsType;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.table.CarsTable;
import cn.lds.common.table.base.DBManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.DeviceHelper;
import cn.lds.common.utils.LogHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import io.realm.Realm;

/**
 * 系统配置参数获取
 * Created by leadingsoft on 2017/11/30.
 */

public class SystemConfigManager {
    private static final String TAG = SystemConfigManager.class.getSimpleName();
    private SystemConfigModel systemConfigModel = null;

    /**
     * 内部类实现单例模式
     * 延迟加载，减少内存开销
     *
     * @author leadingsoft
     */
    private static class AccountHolder {
        private static SystemConfigManager instance = new SystemConfigManager();
    }

    public static SystemConfigManager getInstance() {
        try {
            if (!EventBus.getDefault().isRegistered(AccountHolder.instance))
                EventBus.getDefault().register(AccountHolder.instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AccountHolder.instance;
    }

    public void getSystemConfig( Context context) {
        String url = ModuleUrls.systemConfig.replace("{ver}", VersionManager.getLocalVersionName(context));
        RequestManager.getInstance().get(url, HttpApiKey.systemConfig);
    }

    public SystemConfigModel getSystemConfigModel() {
        return systemConfigModel;
    }
}
