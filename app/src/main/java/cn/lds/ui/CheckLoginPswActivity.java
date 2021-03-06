package cn.lds.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.data.base.BaseModel;
import cn.lds.common.databinding.ActivityCheckLoginPswBinding;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.AccountManager;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.CommonInputDialog;
import cn.lds.widget.dialog.LoadingDialogUtils;

/**
 * 校验登录密码页面
 * Created by sibinbin on 18-6-5.
 */

public class CheckLoginPswActivity extends BaseActivity {

    private ActivityCheckLoginPswBinding mBinding;
    private String mPassword;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_check_login_psw);
        TextView titile = mBinding.getRoot().findViewById(R.id.top_title_tv);
//        backIv = findViewById(R.id.top_back_iv);
        titile.setText("登录密码验证");
        verLoginPsw();
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
                LoadingDialogUtils.showVertical(mContext,"请稍后...");
                AccountManager.getInstance().checkLoginPassword(content);
            }

            @Override
            public void onCancelDialog() {
                finish();
            }
        });
        pasDialog.show();
        pasDialog.setCancelable(false);
        pasDialog.setCanceledOnTouchOutside(false);

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
     *
     * 进入设置PIN码页面
     */
    private void enterSettingPin() {
        Intent intent = new Intent(mContext,SettingPinActivity.class);
        intent.putExtra("PSW",mPassword);
        startActivity(intent);
        finish();
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
            verLoginPsw();
        }

    }

    @Override
    public void initListener() {

    }
}
