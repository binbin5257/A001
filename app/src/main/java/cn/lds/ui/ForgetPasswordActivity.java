package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.base.UIInitListener;
import cn.lds.common.data.base.BaseModel;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.AccountManager;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityForgetPasswordBinding;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.LoadingDialogUtils;

/**
 * 忘记密码页面
 * Created by sibinbin on 17-12-20.
 */

public class ForgetPasswordActivity extends BaseActivity implements UIInitListener, View.OnClickListener {

    private ActivityForgetPasswordBinding mBinding;
    private ImageView backIv;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    protected void onDestroy() {
        super.onDestroy();
        if (null != countDownTimer) {
            countDownTimer.cancel();
            countDownTimer = null;
            mBinding.identifyingGetcode.setText("获取验证码");
            mBinding.identifyingGetcode.setEnabled(true);
        }
    }

    @Override
    public void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forget_password);
        backIv = findViewById(R.id.top_back_iv);
        TextView titleTv = findViewById(R.id.top_title_tv);
        titleTv.setText("修改密码");

    }

    @Override
    public void initListener() {
        backIv.setOnClickListener(this);
        mBinding.identifyingGetcode.setOnClickListener(this);
        mBinding.resetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.identifying_getcode:
                getVerificationCode();//获取验证码
                break;
            case R.id.reset_password:
                resetPassword();//重置密码
                break;

        }
    }

    /**
     * 重置密码
     */
    private void resetPassword() {
        String mobileStr = mBinding.etMobile.getText().toString().trim();
        String verificationCodeStr = mBinding.etVerificationCode.getText().toString().trim();
        String newPasswordStr = mBinding.etNewPassword.getText().toString().trim();
        if(TextUtils.isEmpty(mobileStr)){
            ToastUtil.showToast(this,"手机号不能为空");
            return;
        }
        if(mobileStr.length() != 11){
            ToastUtil.showToast(this,"手机号不合法");
            return;
        }
        if(TextUtils.isEmpty(verificationCodeStr)){
            ToastUtil.showToast(this,"验证码不能为空");
            return;
        }
        if(verificationCodeStr.length() != 4){
            ToastUtil.showToast(this,"验证码不合法");
            return;
        }
        if(TextUtils.isEmpty(newPasswordStr)){
            ToastUtil.showToast(this,"新密码不能为空");
            return;
        }
        //请求服务器重置密码
        //请求服务器获取验证码
        AccountManager.getInstance().putRestPassword(mobileStr,verificationCodeStr,newPasswordStr);

    }

    /**
     * 获取验证码
     */
    private void getVerificationCode() {
        String mobileStr = mBinding.etMobile.getText().toString().trim();
        if(TextUtils.isEmpty(mobileStr)){
            ToastUtil.showToast(this,"手机号不能为空");
            return;
        }
        if(mobileStr.length() != 11){
            ToastUtil.showToast(this,"手机号不合法");
            return;
        }
        //请求服务器获取验证码
        AccountManager.getInstance().getVerificationCode(mobileStr);

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
        if (!(HttpApiKey.getVerificationCode.equals(apiNo)
            || HttpApiKey.resetPassword.equals(apiNo)))
            return;
        LoadingDialogUtils.dissmiss();
        switch (apiNo){
            case HttpApiKey.getVerificationCode:
                ToastUtil.showToast(this,"验证码获取成功");
                //开始60s倒计时
                startCountDownTimer();
                break;
            case HttpApiKey.resetPassword:
                ToastUtil.showToast(this,"登录密码重置成功");
                finish();
                break;
        }
    }

    private void startCountDownTimer() {
        mBinding.identifyingGetcode.setEnabled(false);
        // 第一次调用会有1-10ms的误差，因此需要+15ms，防止第一个数不显示，第二个数显示2s
        countDownTimer = new CountDownTimer(60 * 1000, 1 * 1000 - 10) {

            @Override
            public void onTick(long time) {
                // 第一次调用会有1-10ms的误差，因此需要+15ms，防止第一个数不显示，第二个数显示2s
                mBinding.identifyingGetcode.setText(((time + 15) / 1000) + "s");
            }

            @Override
            public void onFinish() {
                mBinding.identifyingGetcode.setEnabled(true);
                mBinding.identifyingGetcode.setText("重新获取验证码");

            }
        };
        countDownTimer.start();
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
        if (!(HttpApiKey.getVerificationCode.equals(apiNo)
                || HttpApiKey.resetPassword.equals(apiNo)))
            return;
        LoadingDialogUtils.dissmiss();
        BaseModel model = GsonImplHelp.get().toObject(httpResult.getResult(), BaseModel.class);
        if(model != null && model.getErrors().size() > 0){
            ToastUtil.showToast(this,model.getErrors().get(0).getErrmsg());
        }

    }
}
