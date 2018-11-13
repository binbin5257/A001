package cn.lds.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.base.IPermission;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.databinding.ActivityHelpServiceBinding;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.ConfirmDialog;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;

/**
 * 帮助与服务
 * Created by sibinbin on 18-3-14.
 */

public class HelpAndServiceActivity extends BaseActivity implements View.OnClickListener {

    private ActivityHelpServiceBinding mBinding;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_help_service);
        TextView title = mBinding.getRoot().findViewById(R.id.top_title_tv);
        title.setText("帮助与服务");

    }

    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        mBinding.service.setOnClickListener(this);
        mBinding.feedback.setOnClickListener(this);
        mBinding.helpExplain.setOnClickListener(this);
    }

    @Override
    public void onClick( View v ) {
        switch (v.getId()) {
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.feedback:
                startActivity(new Intent(mContext, FeedBackListActivity.class));
                break;
            case R.id.service:
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
                break;
            case R.id.help_explain:
                ToastUtil.showToast(this,"当前版本并无帮助说明");
                break;
        }

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
}
