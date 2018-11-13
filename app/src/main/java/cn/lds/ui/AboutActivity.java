package cn.lds.ui;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.constants.Constants;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.manager.VersionManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.databinding.ActivityAboutBinding;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.VersionUpdateDialog;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;

/**
 * 关于界面
 * Created by sibinbin on 18-3-30.
 */

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAboutBinding mBinding;
    private ImageView backIv;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();

    }

    @Override
    public void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_about);
        TextView titile = mBinding.getRoot().findViewById(R.id.top_title_tv);
        backIv = findViewById(R.id.top_back_iv);
        titile.setText("关于");
        mBinding.versionDetail.setContent(VersionManager.getLocalVersionName(this));


    }

    @Override
    public void initListener() {
        backIv.setOnClickListener(this);
        mBinding.versionDetail.setOnClickListener(this);
    }

    @Override
    public void onClick( View v ) {
        switch (v.getId()){
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.version_detail:
//                getVersionDetail();
                break;
        }
    }

    public void getVersionDetail() {
        if(!TextUtils.isEmpty(CacheHelper.getVersionInfo())){
            VersionUpdateDialog updateDialog = new VersionUpdateDialog(this).setOnDialogClickListener(new OnDialogClickListener() {
                @Override
                public void onDialogClick( Dialog dialog, String clickPosition) {
                    dialog.dismiss();
                    switch (clickPosition) {
                        case ClickPosition.SUBMIT:
//                            dialog.dismiss();
                            break;
                    }
                }
            });
            updateDialog.setMustUpdate(true);
            updateDialog.setRightButtonText("了解了");
            updateDialog.setTitle("版本信息");
            updateDialog.setUpdateContent(CacheHelper.getVersionInfo());
            updateDialog.show();
        }else{
            ToastUtil.showToast(this,"无版本信息");
        }

    }
}
