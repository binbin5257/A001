package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.TextView;

import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.utils.CacheHelper;
import cn.lds.databinding.ActivityHelpBinding;

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ActivityHelpBinding mBinding = DataBindingUtil.setContentView(this,R.layout.activity_help);
        TextView titile = mBinding.getRoot().findViewById(R.id.top_title_tv);
        titile.setText("帮助说明");
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                finish();
            }
        });
        mBinding.webview.loadUrl(CacheHelper.getVersionContent());
    }
}
