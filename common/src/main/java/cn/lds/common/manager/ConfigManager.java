package cn.lds.common.manager;

import cn.lds.common.base.BaseApplication;
import cn.lds.common.data.ServerHostModel;
import cn.lds.common.utils.ToolsHelper;

/**
 * 环境配置管理中心
 * Created by leadingsoft on 2017/11/30.
 */

public class ConfigManager {
    private static ConfigManager mInstance;//单利引用

    public static ConfigManager getInstance() {
        ConfigManager inst = mInstance;
        if (inst == null) {
            synchronized (ConfigManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new ConfigManager();
                    mInstance = inst;
                }
            }
        }
        return inst;
    }


    private int current = 0;
    private ServerHostModel model;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public String getBaseUrl() {
        if (null != getModel())
            return getModel().getData().get(getCurrent()).getUrl();
        ToolsHelper.showInfo(BaseApplication.getInstance(), "获取环境信息失败");
        return "";
    }

    public ServerHostModel getModel() {
        return model;
    }

    public void setModel(ServerHostModel model) {
        this.model = model;
    }
}
