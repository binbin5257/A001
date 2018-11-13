package cn.lds.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.base.IPermission;
import cn.lds.common.data.base.BaseModel;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.AccountManager;
import cn.lds.common.manager.UMengManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityVerficationLoginPasswordBinding;
import cn.lds.widget.PwdEditText;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.captcha.Captcha;
import cn.lds.widget.dialog.ConfirmDialog;
import cn.lds.widget.dialog.LoadingDialogUtils;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;

/**
 * 验证登录界面
 * Created by sibinbin on 18-2-27.
 */

public class VerificationLoginPswdActivity extends BaseActivity implements View.OnClickListener {

    private ActivityVerficationLoginPasswordBinding binding;
//    private ImageView phoneIv;
    private CountDownTimer mTimer;
    private String mCode;
    private String flag;
    private String loginId;
    private InputMethodManager imm;
    private RelativeLayout saveRlty;
    private TextView saveTv;
    private ImageView phoneIv;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verfication_login_password);
        initView();
        initListener();
    }

    @Override
    public void initView() {

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){ //忘记密码
            flag = bundle.getString("FLAG");
            if(!TextUtils.isEmpty(flag) && flag.equals("FORGETPASSWORD")){
                UMengManager.getInstance().onResumePage("ForgetPassword");
                UMengManager.getInstance().onClick("ForgetPassword");
                binding.rlPicVer.setVisibility(View.GONE);
                showSoftInputFromWindow(binding.etInput);
                saveTv = findViewById(R.id.top_menu_extend_iv);
                saveTv.setText("确定");
            }

        }else{ //重置密码
            binding.llInputPhone.setVisibility(View.GONE);
            UMengManager.getInstance().onClick("PhoneVerificationCode");
            binding.rlPicVer.setVisibility(View.VISIBLE);
            loginId = CacheHelper.getLoginId();

        }
        TextView topTitle = binding.getRoot().findViewById(R.id.top_title_tv);
        phoneIv = binding.getRoot().findViewById(R.id.top_menu_iv);
        saveRlty = (RelativeLayout) findViewById(R.id.top_bar_menu_rlyt);

        topTitle.setText("验证");
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
        if(mTimer != null){
            mTimer.cancel();
        }

    }

    /**
     * 倒计时
     */
    private void countTime() {
        phoneIv.setImageResource(R.drawable.bg_phone);
        phoneIv.setVisibility(View.GONE);
        binding.mobile.setText("验证码已发送至" + loginId.substring(0,3) + "****" + loginId.substring(7,11));
        binding.rlSmsVer.setVisibility(View.VISIBLE);
        showSoftInputFromWindow(binding.verCode);
        binding.rlPicVer.setVisibility(View.GONE);
//        binding.servicePhone.setText(CacheHelper.getServiceTel());
        mTimer = new CountDownTimer(120*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.time.setText(millisUntilFinished/1000 + "s");
                binding.time.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onFinish() {
                binding.time.setText("重新获取");
                binding.time.setTextColor(Color.parseColor("#00C9D5"));
            }
        }.start();

    }

    @Override
    public void initListener() {
//        binding.getRoot().findViewById(R.id.top_menu_iv).setOnClickListener(this);
        binding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        binding.getRoot().findViewById(R.id.top_menu_lyt).setOnClickListener(this);
        binding.time.setOnClickListener(this);
        binding.servicePhone.setOnClickListener(this);
        saveRlty.setOnClickListener(this);
        if(saveTv != null){
            saveTv.setOnClickListener(this);

        }
        binding.verCode.setOnInputFinishListener(new PwdEditText.OnInputFinishListener() {
            @Override
            public void onInputFinish( String code ) {
                mCode = code;
                //TODO 请求服务器校验验证码
                LoadingDialogUtils.showHorizontal(mContext,"请稍后...");
                AccountManager.getInstance().checkVerificationCode(loginId,code);
            }
        });
        binding.captCha.setCaptchaListener(new Captcha.CaptchaListener() {
            @Override
            public String onAccess(long time) {
                //请求服务器获取验证码
                getVerificationCode(loginId);//获取验证码
                return "";
            }

            @Override
            public String onFailed(int count) {
                Toast.makeText(VerificationLoginPswdActivity.this, "认证失败,重新认证", Toast.LENGTH_SHORT).show();
                return "";
            }

            @Override
            public String onMaxFailed() {
//                Toast.makeText(VerificationLoginPswdActivity.this, "验证超过次数，你的帐号被封锁", Toast.LENGTH_SHORT).show();
                return "";
            }

        });

    }

    /**
     * 进入修改密码页面
     * @param code
     */
    private void enterUpdatePasswordActivity( String code ) {
        Intent intent = new Intent(mContext,UpdatePasswordActivity.class);
        intent.putExtra("VER_CODE", code);
        intent.putExtra("mobile",loginId);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick( View v ) {
        switch (v.getId()){
//            case R.id.top_menu_lyt:
//                finish();
//                break;
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.top_bar_menu_rlyt:
                if(!TextUtils.isEmpty(flag) && flag.equals("FORGETPASSWORD")){
                    if(saveTv.getVisibility() == View.VISIBLE){
                        verificationPhone();
                    }else{
//                        confirmCallService();
                    }
                }else{
//                    confirmCallService();
                }
                break;
            case R.id.time:
                if("重新获取".equals(binding.time.getText().toString())){
                    getVerificationCode(loginId);//获取验证码
                }
                break;
            case R.id.service_phone:
                confirmCallService();
                break;
            case R.id.top_menu_extend_iv:
                verificationPhone();
                break;
        }
    }

    private void verificationPhone() {
        if(TextUtils.isEmpty(binding.etInput.getText().toString())){
            ToastUtil.showToast(mContext,"手机号不能为空");
            return;
        }
        if(binding.etInput.getText().toString().length() == 11){
            if(imm != null){
                imm.hideSoftInputFromWindow(binding.etInput.getWindowToken(), 0); //强制隐藏键盘
            }
            saveTv.setVisibility(View.GONE);
            binding.llInputPhone.setVisibility(View.GONE);
            UMengManager.getInstance().onClick("PhoneVerificationCode");
            binding.rlPicVer.setVisibility(View.VISIBLE);
            loginId = binding.etInput.getText().toString();
        }else{
            ToastUtil.showToast(mContext,"手机号输入有误");
        }
    }

    private void confirmCallService() {
        ConfirmDialog dialog = new ConfirmDialog(mContext).setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClick( Dialog dialog, String clickPosition ) {
                dialog.dismiss();
                if(clickPosition.equals(ClickPosition.SUBMIT)){
                    callService();
                }else if(clickPosition.equals(ClickPosition.CANCEL)){
                }
            }
        });
        dialog.setRightButtonText("呼叫");
        dialog.setContent(CacheHelper.getServiceTel());
        dialog.setTitle("提示");
        dialog.setTitleVisibilty(View.GONE);
        dialog.show();
    }

    /**
     * 给客服打电话
     */
    private void callService() {
        requestRunTimePermission(new String[]{Manifest.permission.CALL_PHONE}, new IPermission() {
            @SuppressLint("MissingPermission")
            @Override
            public void onGranted() {
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + CacheHelper.getServiceTel());
                intent.setData(data);
                startActivity(intent);
            }

            @Override
            public void onDenied( List<String> deniedPermissions ) {

            }
        });
    }

    /**
     * 通过手机号获取验证码
     */
    public void getVerificationCode(String mobile) {
        LoadingDialogUtils.showHorizontal(this,"请稍后...");
        AccountManager.getInstance().getVerificationCode(mobile);
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
                || HttpApiKey.checkVerificationCode.equals(apiNo)))
            return;
        LoadingDialogUtils.dissmiss();
        switch (apiNo){
            case HttpApiKey.getVerificationCode:
                //120s倒计时
                countTime();
                break;
            case HttpApiKey.checkVerificationCode:
                enterUpdatePasswordActivity(mCode);
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
        if (!(HttpApiKey.getVerificationCode.equals(apiNo)
                || HttpApiKey.checkVerificationCode.equals(apiNo)))
            return;
        LoadingDialogUtils.dissmiss();
        BaseModel model = GsonImplHelp.get().toObject(httpResult.getResult(), BaseModel.class);
        if(model != null && model.getErrors().size() > 0){
            ToastUtil.showToast(this,model.getErrors().get(0).getErrmsg());
        }
        switch (apiNo){
            case HttpApiKey.getVerificationCode:
                break;
            case HttpApiKey.checkVerificationCode:
                break;

        }
    }
}
