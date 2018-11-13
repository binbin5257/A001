package cn.lds.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.lds.MyApplication;
import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.base.IPermission;
import cn.lds.common.constants.Constants;
import cn.lds.common.data.SystemConfigModel;
import cn.lds.common.file.OnDownloadListener;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.AccountManager;
import cn.lds.common.manager.SystemConfigManager;
import cn.lds.common.manager.UMengManager;
import cn.lds.common.manager.VersionManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.FileHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityLoginNewBinding;
import cn.lds.widget.captcha.Utils;
import cn.lds.widget.dialog.CircleProgressDialog;
import cn.lds.widget.dialog.LoadingDialogUtils;
import cn.lds.widget.dialog.VersionUpdateDialog;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;

/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int GET_UNKNOWN_APP_SOURCES = 1000;

    private ActivityLoginNewBinding binding;

    private boolean rememberPassword = false;
    private Drawable remeberPaswDrawable;
    private Drawable unRemeberPaswDrawable;
    boolean isOpenEye = false;
    private boolean isDownNewVersion = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UMengManager.getInstance().onResumePage(getClass().getSimpleName());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_new);
        initView();
        initListener();
        uploadAvatar();
        //获取系统配置
        SystemConfigManager.getInstance().getSystemConfig(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMengManager.getInstance().onPausePage(getClass().getSimpleName());
    }

    public void initView() {
        remeberPaswDrawable = mContext.getResources().getDrawable(
                R.drawable.bg_remeber_pas_select);
        unRemeberPaswDrawable = mContext.getResources().getDrawable(
                R.drawable.bg_remeber_pas_unselect);
        remeberPaswDrawable.setBounds(0, 0, remeberPaswDrawable.getMinimumWidth(), remeberPaswDrawable.getMinimumHeight());
        unRemeberPaswDrawable.setBounds(0, 0, unRemeberPaswDrawable.getMinimumWidth(), unRemeberPaswDrawable.getMinimumHeight());
        if(isFirstLogin()){
            binding.ivWelcome.setImageResource(R.drawable.bg_login_welcome_app);
        }else{
            binding.ivWelcome.setImageResource(R.drawable.bg_login_welcome);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ToolsHelper.isNull(CacheHelper.getLoginId()) && !"13333333334".equals(CacheHelper.getLoginId())){
            binding.accountUsername.setText(CacheHelper.getLoginId());
            binding.accountUsername.setSelection( CacheHelper.getLoginId().length() );

        }
        
        if(!ToolsHelper.isNull(CacheHelper.getPassworld())&& !"13333333334".equals(CacheHelper.getLoginId())){
            rememberPassword = true;
            binding.accountPassword.setText(CacheHelper.getPassworld());
            binding.remeberPassword.setCompoundDrawables(remeberPaswDrawable,null,null,null);
        }else{
            rememberPassword = false;
            binding.remeberPassword.setCompoundDrawables(unRemeberPaswDrawable,null,null,null);

        }
        binding.remeberPassword.setCompoundDrawablePadding(Utils.dp2px(mContext,8));
        if(!TextUtils.isEmpty(binding.accountUsername.getText())){
            binding.llClear.setVisibility(View.VISIBLE);
        }else{
            binding.llClear.setVisibility(View.GONE);
        }
    }

    @Override
    public void initListener() {
        binding.next.setOnClickListener(this);
        binding.tvForgetPassword.setOnClickListener(this);
        binding.userRule.setOnClickListener(this);
        binding.remeberPassword.setOnClickListener(this);
        binding.testMode.setOnClickListener(this);
        binding.llEye.setOnClickListener(this);
        binding.llClear.setOnClickListener(this);
        binding.accountUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

            }

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count ) {
                if(!TextUtils.isEmpty(binding.accountPassword.getText())){
                    binding.accountPassword.setText("");
                }
            }

            @Override
            public void afterTextChanged( Editable s ) {
                if(TextUtils.isEmpty(binding.accountUsername.getText())){
                    binding.llClear.setVisibility(View.GONE);
                    binding.loginAvatar.setImageResource(R.drawable.bg_login_avatar);
                }else{
                    binding.llClear.setVisibility(View.VISIBLE);
                    if(binding.accountUsername.length() == 11 && !CacheHelper.getIsFirstLogin()){
                        uploadAvatar();
                    }else{
                        binding.loginAvatar.setImageResource(R.drawable.bg_login_avatar);
                    }
                }

            }
        });

    }

    private void uploadAvatar() {
        File file = new File(Constants.SYS_CONFIG_FILE_PATH + "file/protrait/" + binding.accountUsername.getText() + ".jpg");
        if(file.exists()){
            Uri uri = Uri.parse("file://"+Constants.SYS_CONFIG_FILE_PATH + "file/protrait/" + binding.accountUsername.getText() + ".jpg");
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            imagePipeline.evictFromMemoryCache(uri);
            imagePipeline.evictFromDiskCache(uri);
            imagePipeline.evictFromCache(uri);
            binding.loginAvatar.setImageURI(uri);
        }else{
            binding.loginAvatar.setImageResource(R.drawable.bg_login_avatar);
        }
    }

    /**
     * 检查是否动态添加获取手机状态的权限
     */
    private void requestDeviceStatePermission() {
        requestRunTimePermission(new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, new IPermission() {
            @Override
            public void onGranted() {
                FileHelper.copyAssetFile(LoginActivity.this,"style.data", Environment.getExternalStorageDirectory()
                        + "/qq.data");
                requestLogin();
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {
            }
        });
    }

    /**
     * 界面点击事件
     *
     * @param view
     *         点击的view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.next == id) {
            requestDeviceStatePermission();
        } else if (R.id.tv_forget_password == id) {
            Intent intent = new Intent(this, VerificationLoginPswdActivity.class);
            intent.putExtra("FLAG","FORGETPASSWORD");
            startActivity(intent);
        } else if (R.id.user_rule == id) {
            Intent intent1 = new Intent(LoginActivity.this, WebHostActivity.class);
            intent1.putExtra("url", "http://cmleopaard.cu-sc.com:20000/view/instruction.html");
            intent1.putExtra("title", "用户协议");
            startActivity(intent1);
        } else if(R.id.remeber_password == id){
            if(rememberPassword){
                rememberPassword = false;
                binding.remeberPassword.setCompoundDrawables(unRemeberPaswDrawable,null,null,null);
            }else{
                rememberPassword = true;
                binding.remeberPassword.setCompoundDrawables(remeberPaswDrawable,null,null,null);
            }
            binding.remeberPassword.setCompoundDrawablePadding(Utils.dp2px(mContext,8));
        } else if(R.id.test_mode == id){
            TestModeLogin();
        } else if(R.id.ll_eye == id){
            if(isOpenEye){
                isOpenEye = false;
                binding.ivEye.setImageResource(R.drawable.bg_closed_eye);
                binding.accountPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }else{
                isOpenEye = true;
                binding.ivEye.setImageResource(R.drawable.bg_open_eye);
                binding.accountPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

            }
        }else if(id == R.id.ll_clear){
            binding.accountUsername.setText("");
        }

    }

    private void TestModeLogin() {

        requestRunTimePermission(
                new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, new IPermission() {
            @Override
            public void onGranted() {
                FileHelper.copyAssetFile(LoginActivity.this,"style.data", Environment.getExternalStorageDirectory()
                        + "/qq.data");
                String jpushRegistrationID = JPushInterface.getRegistrationID(MyApplication.getInstance().getApplicationContext());
                LoadingDialogUtils.showVertical(mContext, "请稍候");
                BaseApplication.getInstance().jbgsn = "498603";
                CacheHelper.setLoginId("13333333334");
                AccountManager.getInstance().login("13333333334", "498603", jpushRegistrationID);
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {
            }
        });
    }




    /**
     * 封装登录请求数据
     */
    private void requestLogin() {
        String userName = binding.accountUsername.getText().toString();
        if (ToolsHelper.isNull(userName)
                || userName.length() != 11) {
            ToolsHelper.showInfo(mContext, "请输入正确手机号");
            return;
        }

        String password = binding.accountPassword.getText().toString();
        BaseApplication.getInstance().jbgsn = password;
        if(rememberPassword){
            CacheHelper.setPassword(password);
        }else{
            CacheHelper.setPassword("");
        }
        if (ToolsHelper.isNull(password) ) {
            ToolsHelper.showInfo(mContext, "请输入密码");
            return;
        }
        if(password.length() < 6 || password.length() > 16){
            ToolsHelper.showInfo(mContext, "密码为6-16位");
            return;
        }
        CacheHelper.setLoginId(userName);
        String jpushRegistrationID = JPushInterface.getRegistrationID(MyApplication.getInstance().getApplicationContext());
        LoadingDialogUtils.showVertical(mContext, "请稍候");
        AccountManager.getInstance().login(userName, password, jpushRegistrationID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            EventBus.getDefault().unregister(this);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param event
     *         请求成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestLogin(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.login.equals(apiNo)
             ||HttpApiKey.systemConfig.equals(apiNo)))
            return;


        switch (apiNo){
            case HttpApiKey.login:
                startMainActivity();
                break;
            case HttpApiKey.systemConfig:
                LoadingDialogUtils.dissmiss();
                processSystemConfig(httpResult);
                break;
        }

    }

    /**
     * 解析系统配置数据
     * @param httpResult
     */
    private void processSystemConfig( HttpResult httpResult) {
        SystemConfigModel systemConfigModel = GsonImplHelp.get().toObject(httpResult.getResult(), SystemConfigModel.class);
        if(systemConfigModel != null && systemConfigModel.getData() != null){
            final SystemConfigModel.DataBean data = systemConfigModel.getData();
            CacheHelper.setServiceTel(data.getServiceCall());
            CacheHelper.setVersionInfo(data.getVerInfo());
            CacheHelper.setVersionContent(data.getHelpContent());
            if(data.getNewerVersionNo() != 0 && !TextUtils.isEmpty(data.getDownloadUrl())){
                requestRunTimePermission(new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, new IPermission() {
                    @Override
                    public void onGranted() {
                        if(!isDownNewVersion){
                            checkAppVersion(LoginActivity.this,data.getNewerVersionNo(),data.isForceUpdate(),data.getNewerVersionInfo(),data.getDownloadUrl());
                        }
                    }

                    @Override
                    public void onDenied( List<String> deniedPermissions ) {

                    }
                });
            }
        }

    }

    /**
     * 检查app版本，更新，安装
     * @param activity 上下文
     * @param serviceVersionCode 版本号
     * @param mustUpdate 是否强制更新
     * @param versionInfo 版本信息
     * @param downApkUrl apk下载地址
     */
    private void checkAppVersion( final Activity activity, int serviceVersionCode, boolean mustUpdate, String versionInfo, final String downApkUrl) {
        int localVersionCode = VersionManager.getLocalVersion(activity);
        if(serviceVersionCode > localVersionCode){
            VersionUpdateDialog updateDialog = new VersionUpdateDialog(activity).setOnDialogClickListener( new OnDialogClickListener() {
                @Override
                public void onDialogClick( Dialog dialog, String clickPosition) {
                    dialog.dismiss();
                    switch (clickPosition) {
                        case ClickPosition.SUBMIT:
                            isDownNewVersion = true;
                            final CircleProgressDialog circleProgressDialog = new CircleProgressDialog(activity);
                            circleProgressDialog.show();
                            VersionManager.getInstance().downloadApk(downApkUrl, new OnDownloadListener() {
                                @Override
                                public void onDownloadSuccess() {
                                    circleProgressDialog.dismiss();

                                    requestInstallApp();

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


    private void requestInstallApp() {
        if (Build.VERSION.SDK_INT >= 26) {
            //来判断应用是否有权限安装apk
            boolean installAllowed= getPackageManager().canRequestPackageInstalls();
            //有权限
            if (installAllowed) {
                //安装apk
                VersionManager.getInstance().installApp();
            } else {
                //无权限 申请权限
                requestRunTimePermission( new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, new IPermission() {
                    @Override
                    public void onGranted() {
                        VersionManager.getInstance().installApp();
                    }

                    @Override
                    public void onDenied( List<String> deniedPermissions ) {
                        Intent intent = new Intent( Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                        startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);

                    }
                } );
            }
        } else {
            VersionManager.getInstance().installApp();
        }

    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        switch (requestCode) {
            case GET_UNKNOWN_APP_SOURCES:
                requestInstallApp();

                break;

            default:
                break;
        }
    }

    /**
     * @param event
     *         请求失败返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestLogin(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!HttpApiKey.login.equals(apiNo))
            return;
        LoadingDialogUtils.dissmiss();
        ToolsHelper.showHttpRequestErrorMsg(mContext, httpResult);
    }

    /**
     * 跳转主界面方法
     */
    private void startMainActivity() {
        startActivity(new Intent(mContext, MainActivity.class));
        this.finish();
    }

    public boolean isFirstLogin() {
        return CacheHelper.getIsFirstLogin();
    }
}
