package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.databinding.ActivityMessageDetailBinding;

/**
 * 消息详情
 * Created by sibinbin on 18-3-6.
 */

public class MessageDetailActivity extends BaseActivity implements View.OnClickListener {

    private ActivityMessageDetailBinding mBinding;
    private String title;
    private String content;
    private String time;
    private ImageView backBtn;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initData();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            title = bundle.getString("TITLE");
            content = bundle.getString("CONTENT");
            time = bundle.getString("TIME");
            mBinding.title.setText(title);
            mBinding.content.setText(content);
            mBinding.time.setText(time);
        }
    }

    @Override
    public void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_message_detail);
        TextView title = mBinding.getRoot().findViewById(R.id.top_title_tv);
        title.setText("消息详情");
        backBtn = mBinding.getRoot().findViewById(R.id.top_back_iv);

    }

    @Override
    public void initListener() {
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick( View v ) {
        if(v.getId() == R.id.top_back_iv){
            finish();
        }
    }
}
