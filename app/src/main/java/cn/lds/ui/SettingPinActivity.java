package cn.lds.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.RenderProcessGoneDetail;
import android.widget.ImageView;
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
import cn.lds.databinding.ActivitySettingPinBinding;
import cn.lds.widget.PwdEditText;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.KeyboardDialog;
import cn.lds.widget.dialog.LoadingDialog;
import cn.lds.widget.dialog.LoadingDialogUtils;

/**
 * 设置PIN码页面
 * Created by sibinbin on 18-2-28.
 */

public class SettingPinActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySettingPinBinding settingPinBinding;

    private String mPinCode;

    private int mPinLength;
    private String mPassword;
    private KeyboardDialog keyboardDialog;

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
        settingPinBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting_pin);
        TextView topTitle = settingPinBinding.getRoot().findViewById(R.id.top_title_tv);
        topTitle.setText("设置PIN码");
        TextView saveTv = settingPinBinding.getRoot().findViewById(R.id.top_menu_extend_iv);
        saveTv.setText("");
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mPassword = bundle.getString("PSW");
        }
        showCustomPinDialog();
    }

    private void showCustomPinDialog() {
        if(keyboardDialog == null){
            keyboardDialog = new KeyboardDialog(this, R.style.alex_dialog_anim_bottom2top);
            keyboardDialog.setOnKeyboardListener(new KeyboardDialog.OnKeyboardListener() {
                @Override
                public void inputFinsh( String pin ) {
                    LoadingDialogUtils.showHorizontal(SettingPinActivity.this,"请稍后...");
                    AccountManager.getInstance().updatePin(mPassword,pin);
                }

                @Override
                public void forgetPin() {
                    Intent intent = new Intent(SettingPinActivity.this, AccountSecurityActivity.class);
                    startActivity(intent);
                }
            });
            keyboardDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss( DialogInterface dialog ) {
                    finish();
                }
            });
        }
        keyboardDialog.setForgetPinTextVisibility(View.INVISIBLE);
        keyboardDialog.show();
    }

    @Override
    public void initListener() {
        settingPinBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        settingPinBinding.getRoot().findViewById(R.id.top_menu_lyt).setOnClickListener(this);
        settingPinBinding.pinCode.setOnInputFinishListener(new PwdEditText.OnInputFinishListener() {



            @Override
            public void onInputFinish( String pinCode ) {
                mPinCode = pinCode;
            }
        });

        settingPinBinding.pinCode.setOnTextChangeListener(new PwdEditText.OnTextChangeListener() {



            @Override
            public void onTextLengthChange( int length ) {
                mPinLength = length;
            }
        });
    }

    @Override
    public void onClick( View v ) {
        switch (v.getId()){
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.top_menu_lyt:
                if(!TextUtils.isEmpty(mPinCode) && mPinLength == 4){
                    LoadingDialogUtils.showHorizontal(this,"请稍后...");
                    AccountManager.getInstance().updatePin(mPassword,mPinCode);
                }else{
                    ToastUtil.showToast(this,"请输入4位PIN码");

                }

                break;
        }
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
        if (!(HttpApiKey.updatePin.equals(apiNo)
        ))
            return;
        LoadingDialogUtils.dissmiss();
        switch (apiNo){
            case HttpApiKey.updatePin:
                ToastUtil.showToast(this,"PIN码设置成功");
                finish();
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
        if (!(HttpApiKey.updatePin.equals(apiNo)
        ))
            return;
        LoadingDialogUtils.dissmiss();
        BaseModel model = GsonImplHelp.get().toObject(httpResult.getResult(), BaseModel.class);
        if(model != null && model.getErrors().size() > 0){
            ToastUtil.showToast(this,model.getErrors().get(0).getErrmsg());
        }

    }
}
