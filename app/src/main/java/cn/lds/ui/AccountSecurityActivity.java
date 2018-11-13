package cn.lds.ui;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.data.base.BaseModel;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.AccountManager;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityAccountSecurityBinding;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.LoadingDialogUtils;
import cn.lds.widget.dialog.CommonInputDialog;

/**
 * 账号与安全
 * Created by sibinbin on 18-2-27.
 */

public class AccountSecurityActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAccountSecurityBinding accountSecurityBinding;
    /**
     * 登录密码
     */
    private String mPassword;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        accountSecurityBinding = DataBindingUtil.setContentView(this, R.layout.activity_account_security);
        initView();
        initListener();
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

    @Override
    public void initView() {
        TextView topTitle = accountSecurityBinding.getRoot().findViewById(R.id.top_title_tv);
        topTitle.setText(R.string.account_security);
    }

    @Override
    public void initListener() {
        accountSecurityBinding.settingLoginPsd.setOnClickListener(this);
        accountSecurityBinding.settingPin.setOnClickListener(this);
        accountSecurityBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);

    }

    @Override
    public void onClick(View v ) {
        switch (v.getId()){
            case R.id.setting_login_psd:
                enterVerification();
                break;
            case R.id.setting_pin:
                //验证登录密码
                verLoginPsw();
                break;
            case R.id.top_back_iv:
                finish();
                break;
        }

    }

    /**
     * 验证登录密码
     */
    private void verLoginPsw() {
        CommonInputDialog pasDialog = new CommonInputDialog(this,"请输入登录密码");
        pasDialog.setOnSubmitInputListener(new CommonInputDialog.OnSubmitInputListener() {
            @Override
            public void onSubmitInput( String content ) {
                mPassword = content;
                LoadingDialogUtils.showHorizontal(mContext,"请稍后...");
                AccountManager.getInstance().checkLoginPassword(mPassword);
            }

            @Override
            public void onCancelDialog() {

            }
        });
        pasDialog.show();
        showSoftInputFromWindow(this, pasDialog.getEditText());


    }
    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow( Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     *
     * 进入设置PIN码页面
     */
    private void enterSettingPin() {
        Intent intent = new Intent(mContext,SettingPinActivity.class);
        intent.putExtra("PSW",mPassword);
        startActivity(intent);
    }

    /**
     * 进入验证页面
     */
    private void enterVerification() {
        Intent intent = new Intent(this,VerificationLoginPswdActivity.class);
        startActivity(intent);
    }

    /**
     * 服务器请求成功
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reponseSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.checkPassword.equals(apiNo)
                ))
            return;
        LoadingDialogUtils.dissmiss();
        switch (apiNo){
            case HttpApiKey.checkPassword:
                enterSettingPin();
                break;
        }
    }

    /**
     * 服务器请求失败
     *
     * @param event
     *         失败返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reponseFailure(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.checkPassword.equals(apiNo)
                ))
            return;
        LoadingDialogUtils.dissmiss();
        BaseModel model = GsonImplHelp.get().toObject(httpResult.getResult(), BaseModel.class);
        if(model != null && model.getErrors().size() > 0){
            ToastUtil.showToast(this,model.getErrors().get(0).getErrmsg());
        }

    }
}
