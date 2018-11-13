package cn.lds.ui;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.lds.MyApplication;
import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.constants.Constants;
import cn.lds.common.file.OnDownloadListener;
import cn.lds.common.manager.VersionManager;
import cn.lds.databinding.ActivitySettingBinding;
import cn.lds.widget.dialog.CircleProgressDialog;
import cn.lds.widget.dialog.ConfirmDialog;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;


/**
 * 设置界面
 *
 * @author leadingsoft
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private ActivitySettingBinding mBinding;
    private ConfirmDialog logoutDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        TextView topTitle = mBinding.getRoot().findViewById(R.id.top_title_tv);
        topTitle.setText(R.string.setting_title);
    }

    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        mBinding.feedbackLlyt.setOnClickListener(this);
        mBinding.aboutLlyt.setOnClickListener(this);
        mBinding.logoutBtn.setOnClickListener(this);
        mBinding.updatePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.feedback_llyt:
                startActivity(new Intent(mContext, HelpAndServiceActivity.class));
                break;
            case R.id.about_llyt:
                startActivity(new Intent(mContext,AboutActivity.class));
                break;
            case R.id.logout_btn:
                logut();
                break;
            case R.id.update_password:
//                Intent intent = new Intent(this, UpdatePasswordActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(this, AccountSecurityActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 注销登录
     */
    private void logut() {
        if (null == logoutDialog) {
            logoutDialog = new ConfirmDialog(mContext);
            logoutDialog.setOnDialogClickListener(new OnDialogClickListener() {
                @Override
                public void onDialogClick( Dialog dialog, String clickPosition) {
                    dialog.dismiss();
                    switch (clickPosition) {
                        case ClickPosition.SUBMIT:
                            MyApplication instance = (MyApplication) MyApplication.getInstance();
                            instance.sendLogoutBroadcast(Constants.SYS_CONFIG_LOGOUT_FLITER, "点击注销按钮");
                            finish();
                            break;
                    }
                }
            });
            logoutDialog.setTitle("确定注销？");
            logoutDialog.setContent("");
        }
        logoutDialog.show();
    }
}
