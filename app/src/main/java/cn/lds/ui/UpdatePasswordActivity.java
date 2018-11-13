package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.lds.MyApplication;
import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.base.UIInitListener;
import cn.lds.common.constants.Constants;
import cn.lds.common.data.base.BaseModel;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.AccountManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityUpdatePasswordBinding;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.LoadingDialogUtils;

/**
 * 修改密码页面
 * Created by sibinbin on 17-12-20.
 */

public class UpdatePasswordActivity extends BaseActivity implements UIInitListener, View.OnClickListener {

    private ActivityUpdatePasswordBinding mBinding;
    private ImageView backIv;
    private String mVerCode;
    private RelativeLayout saveRlty;
    private String loginId;

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
    public void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_password);
        backIv = findViewById(R.id.top_back_iv);
        TextView saveTv = findViewById(R.id.top_menu_extend_iv);
        TextView titleTv = findViewById(R.id.top_title_tv);
        saveRlty = mBinding.getRoot().findViewById(R.id.top_bar_menu_rlyt);
        saveTv.setText("确定");
        titleTv.setText("设置登录密码");
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mVerCode = bundle.getString("VER_CODE");
            loginId = bundle.getString("mobile");
        }
        showSoftInputFromWindow(mBinding.newPasswordEt);

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
    public void initListener() {
        saveRlty.setOnClickListener(this);
        backIv.setOnClickListener(this);
        mBinding.confirmUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.top_bar_menu_rlyt:
                //请求服务器修改密码
                String  passwordStr = mBinding.newPasswordEt.getText().toString();
                if(passwordStr.length() < 4){
                    ToastUtil.showToast(mContext,"密码长度最少需要6位");
                    return;
                }
                if(passwordStr.length() > 18){
                    ToastUtil.showToast(mContext,"密码长度最多18位");
                    return;
                }
                LoadingDialogUtils.showHorizontal(this,"请稍后...");
                AccountManager.getInstance().putRestPassword(loginId,mVerCode,mBinding.newPasswordEt.getText().toString());
                break;
        }
    }


    /**
     * 登录密码设置成功
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void settingPasswordSuccess(HttpRequestEvent event) {
        LoadingDialogUtils.dissmiss();
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.resetPassword.equals(apiNo)))
            return;
        ToastUtil.showToast(this,"密码设置成功，请重新登录");
        //退出重新登录
        MyApplication instance = (MyApplication) MyApplication.getInstance();
        instance.sendLogoutBroadcast(Constants.SYS_CONFIG_LOGOUT_FLITER, "点击注销按钮");
        finish();


    }
    /**
     * 登录密码设置失败
     *
     * @param event
     *         失败返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void settingPasswordFailure(HttpRequestErrorEvent event) {
        LoadingDialogUtils.dissmiss();
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.resetPassword.equals(apiNo)))
            return;
        LoadingDialogUtils.dissmiss();
        BaseModel model = GsonImplHelp.get().toObject(httpResult.getResult(), BaseModel.class);
        if(model != null && model.getErrors().size() > 0){
            ToastUtil.showToast(this,model.getErrors().get(0).getErrmsg());
        }



    }
}
