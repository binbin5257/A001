package cn.lds.common.manager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.data.LoginModel;
import cn.lds.common.enums.DeviceType;
import cn.lds.common.enums.OsType;
import cn.lds.common.file.FileUploadComplete;
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
 * Created by leadingsoft on 2017/11/30.
 * <p>
 * 消息tag列表需要登录鉴权之后才能够获取，因此自动登录代码，注释，与1期保持一直
 */

public class AccountManager {
    private static final String TAG = AccountManager.class.getSimpleName();
    public static int updatePersonFlag = 0;
    public static final int updateNickName = 1;
    public static final int updateMobile = 2;
    public static final int updateContacts = 3;
    public static final int updateAvatar = 4;
    public static final int updateContactPhone = 5;



    /**
     * 内部类实现单例模式
     * 延迟加载，减少内存开销
     *
     * @author leadingsoft
     */
    private static class AccountHolder {
        private static AccountManager instance = new AccountManager();
    }

    public static AccountManager getInstance() {
        try {
            if (!EventBus.getDefault().isRegistered(AccountHolder.instance))
                EventBus.getDefault().register(AccountHolder.instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AccountHolder.instance;
    }

    /*
    *
    * @param userName 用户名
    * @param password 密码
    * @param jpushRegistrationID 极光注册id
    * */
    public void login(String userName, String password, String jpushRegistrationID) {
        JSONObject json = new JSONObject();

        try {
            json.put("accountId", userName);
            json.put("password", password);
            json.put("registrationId", jpushRegistrationID);
        } catch (JSONException e) {
            LogHelper.e(TAG, e);
        }
        login(json);
    }

    /**
     * 个人信息详情
     */
    public void getPesionInfo() {
        RequestManager.getInstance().get(ModuleUrls.getPersonalInfo, HttpApiKey.getPersonalInfo);
    }

    /**
     * 修改个人信息
     */
    public void putPersonInfo(String updateContent, int flag) {
        updatePersonFlag = flag;
        JSONObject json = new JSONObject();
        try {
            switch (flag) {
                case updateNickName:
                    json.put("nickname", updateContent);
                    break;
                case updateMobile: //  手机号不可更改
//                    json.put("nickname",updateContent);
                    break;
                case updateContacts:
                    json.put("secEmLinkerName", updateContent);
                    break;
                case updateContactPhone:
                    json.put("secEmLinkerMobile", updateContent);
                    break;
                case updateAvatar:
                    json.put("avatarFileRecordNo", updateContent);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestManager.getInstance().put(ModuleUrls.updatePersonalInfo, HttpApiKey.updatePersonalInfo, json.toString());
    }

//    /*
//       *
//       * @param nonceToken 一次性token
//       * */
//    public void autoLogin() {
//        JSONObject json = new JSONObject();
//
//        try {
//            json.put("nonceToken", CacheHelper.getToken());
//        } catch (JSONException e) {
//            LogHelper.e(TAG, e);
//        }
//        login(json);
//        CacheHelper.setToken("");
//    }

//    public void autoLogin(String actionUrl, String apiNo, Map<String, String> extras, HttpType type) {
//        if (extras == null) {
//            extras = new HashMap<>();
//        }
//        extras.put("actionUrl", actionUrl);
//        extras.put("apiNo", apiNo);
//        extras.put("type", type.name());
//        JSONObject json = new JSONObject();
//        try {
//            json.put("nonceToken", CacheHelper.getToken());
//            json.put("appId", "testId");
//            json.put("deviceId", DeviceHelper.getDeviceId());
//            json.put("deviceType", DeviceType.PHONE.getValue());
//            json.put("osType", OsType.ANDROID.getValue());
//            json.put("osVersion", DeviceHelper.getOsVer());
//            json.put("registrationId", CacheHelper.getJpushregisrationid());
//        } catch (JSONException e) {
//            LogHelper.e(TAG, e);
//        }
//        CacheHelper.setToken("");
//        RequestManager.getInstance().post(ModuleUrls.login, HttpApiKey.login, json.toString(), extras);
//    }

    private void login(JSONObject json) {
        try {
            json.put("appId", "testId");
            json.put("deviceId", DeviceHelper.getDeviceId());
            json.put("deviceType", DeviceType.PHONE.getValue());
            json.put("osType", OsType.ANDROID.getValue());
            json.put("osVersion", DeviceHelper.getOsVer());
        } catch (JSONException e) {
            LogHelper.e(TAG, e);
        }
        RequestManager.getInstance().post(ModuleUrls.login, HttpApiKey.login, json.toString());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void requestLogin(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!HttpApiKey.login.equals(apiNo))
            return;
        LoginModel model = GsonImplHelp.get().toObject(httpResult.getResult(), LoginModel.class);
        String lastAccount = CacheHelper.getAccount();
        boolean isChangeUser = false;
        if (ToolsHelper.isNull(lastAccount) || !lastAccount.equals(model.getData().getPrincipal())) {
            isChangeUser = true;
        }
        final LoginModel.DataBean.DetailsBean detailsBean = model.getData().getDetails();
//        if (model.getData().getDetails().getOnceToken() != null) {
//            CacheHelper.setToken(model.getData().getDetails().getOnceToken());
//        }
        CacheHelper.setAccount(model.getData().getPrincipal());
        CacheHelper.setIsDemo(model.getData().getDetails().isDemo());
        if(CacheHelper.getIsFirstLogin()){
            if(!model.getData().getDetails().isDemo()){
                CacheHelper.setIsFirstLogin(false);
            }else{
                CacheHelper.setPassword("");
            }
        }
//        creatOrUpdateUser(model.getData().get);





//        CacheHelper.setVin("");
        //更新所有车辆
        updateCar(model.getData().getVehicle(), isChangeUser);

        //友盟用户统计
        UMengManager.getInstance().profileSignIn(model.getData().getPrincipal());

        BaseApplication.getInstance().runOnUiThreadDelay(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(detailsBean);
            }
        }, 500);
    }


    /**
     * @param vehicle
     *         车辆列表
     * @param isChangeUser
     *         是否切换用户
     */
    private void updateCar(final List<LoginModel.DataBean.VehicleBean> vehicle, boolean isChangeUser) {
        Realm realm = DBManager.getInstance().getRealm();
        if (null == vehicle || vehicle.isEmpty()) {
            CacheHelper.setUsualcar(null);
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.delete(CarsTable.class);
                    for (int i = 0; i < vehicle.size(); i++) {
                        LoginModel.DataBean.VehicleBean vehicleBean = vehicle.get(i);
                        CarsTable carsTable = new CarsTable();
                        carsTable.setMode(vehicleBean.getMode());
                        carsTable.setColor(vehicleBean.getColor());
                        carsTable.setVin(vehicleBean.getVin());
                        carsTable.setLicensePlate(vehicleBean.getLicensePlate());
                        carsTable.setFuelType(vehicleBean.getFuelType());
                        carsTable.setYear(vehicleBean.getYear());
                        carsTable.setImage(vehicleBean.getImage());
                        carsTable.setAccount(CacheHelper.getAccount());
                        carsTable.setModelColor(vehicleBean.getModelColor());
                        realm.copyToRealmOrUpdate(carsTable);
                    }
                }
            });
            realm.close();
            CarsTable usualcar = CacheHelper.getUsualcar();
            if (isChangeUser || vehicle.size() == 1 || null == usualcar) {
                LoginModel.DataBean.VehicleBean vehicleBean = vehicle.get(0);
                CarsTable carsTable = new CarsTable();
                carsTable.setColor(vehicleBean.getColor());
                carsTable.setVin(vehicleBean.getVin());
                carsTable.setLicensePlate(vehicleBean.getLicensePlate());
                carsTable.setFuelType(vehicleBean.getFuelType());
                carsTable.setYear(vehicleBean.getYear());
                carsTable.setImage(vehicleBean.getImage());
                carsTable.setMode(vehicleBean.getMode());
                carsTable.setAccount(CacheHelper.getAccount());
                CacheHelper.setUsualcar(carsTable);
            }
        }
    }

    /**
     * 上传头像
     *
     * @param url
     * @param key
     */

    public void uploadAvatar(String url, String key) {
        Map<String, String> extras = new HashMap<>();
        extras.put("filePath", url);
        extras.put("owner", key);
        FilesManager.getInstance().upload(url);

    }

    /**
     * 头像上传成功
     *
     * @param fileUploadComplete
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void uploadAvatarSuccess(FileUploadComplete fileUploadComplete) {
        if (fileUploadComplete != null && fileUploadComplete.getFilesBeanList() != null && fileUploadComplete.getFilesBeanList().size() > 0) {
            putPersonInfo(fileUploadComplete.getFilesBeanList().get(0).getNo(), updateAvatar);
        }

    }

    /**
     * 修改密码
     *
     * @param newPasswordStr
     *         新密码
     * @param orginalPasswordStr
     *         原密码
     */
    public void updatePassword(String newPasswordStr, String orginalPasswordStr) {

        try {
            JSONObject json = new JSONObject();
            json.put("newPassword", newPasswordStr);
            json.put("password", orginalPasswordStr);
            RequestManager.getInstance().post(ModuleUrls.modifyPassword, HttpApiKey.modifyPassword, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取验证码
     *
     * @param mobile
     *         手机号
     */
    public void getVerificationCode(String mobile) {
        String url = ModuleUrls.getVerificationCode;
        url = url.replace("{mobile}", mobile);
        RequestManager.getInstance().get(url, HttpApiKey.getVerificationCode);
    }

    /**
     * 更新pin码
     * @param mPassword 账户密码
     * @param mPinCode pin码
     */
    public void updatePin( String mPassword, String mPinCode ) {


        try {
            String url = ModuleUrls.updatePin;
            JSONObject json = new JSONObject();
            json.put("password", mPassword);
            json.put("pin", mPinCode);
            RequestManager.getInstance().post(url, HttpApiKey.updatePin,json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * 校验验证码
     * @param mobile
     * @param verificationCode
     */
    public void checkVerificationCode(String mobile,String verificationCode){

        try {
            String url = ModuleUrls.checkVerificationCode;
            JSONObject json = new JSONObject();
            json.put("mobile", mobile);
            json.put("captcha", verificationCode);
            RequestManager.getInstance().post(url, HttpApiKey.checkVerificationCode,json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 重置登录密码
     *
     * @param mobile
     *         手机号
     * @param captcha
     *         验证码
     * @param password
     *         新密码
     */
    public void putRestPassword(String mobile, String captcha, String password) {

        try {
            JSONObject json = new JSONObject();
            json.put("captcha", captcha);
            json.put("mobile", mobile);
            json.put("password", password);
            RequestManager.getInstance().put(ModuleUrls.resetPassword, HttpApiKey.resetPassword, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 校验账户密码
     * @param password 账户密码
     */
    public void checkLoginPassword(String password){
        try {
            JSONObject json = new JSONObject();
            json.put("password", password);
            RequestManager.getInstance().post(ModuleUrls.checkPassword, HttpApiKey.checkPassword, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新
     */

}
