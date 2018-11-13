package cn.lds.common.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import cn.lds.common.base.BaseApplication;

/**
 * Created by leadingsoft on 2017/11/29.
 */

public class DeviceHelper {
    /**
     * 获取设备编号
     *
     * @return
     */
    public static String getDeviceId() {
        String imei = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) BaseApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(BaseApplication.getInstance(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            imei = telephonyManager.getDeviceId();
            if (imei == null) {
                // android pad
                imei = Settings.Secure.getString(BaseApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } catch (Exception ex) {
            LogHelper.e("获取设备编号", ex);
        }
        return ToolsHelper.toString(imei);
    }

    /**
     * 获取设备操作系统版本
     *
     * @return
     */
    public static String getOsVer() {
        try {
            return android.os.Build.VERSION.RELEASE;
        } catch (Exception ex) {
            LogHelper.e("获取设备操作系统版本", ex);
        }
        return "";
    }


    /**
     * 获取当前版本号
     *
     * @return
     */
    public static int getAppVersionCode() {
        int version = 0;
        try {
            PackageManager packageManager = BaseApplication.getInstance().getBaseContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(BaseApplication.getInstance().getPackageName(), 0);
            version = packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

}
