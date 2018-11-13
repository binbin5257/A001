package cn.lds.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.databinding.ActivityVoiceListBinding;
import cn.lds.ui.adapter.FeedBackListAdapter;

/**
 * 录音列表界面
 */
public class VoiceListActivity extends BaseActivity implements View.OnClickListener {
    ActivityVoiceListBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_voice_list);
        initView();
        initListener();
    }

    /**
     * 注册事件监听
     */
    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
    }


    /**
     * ui初始化
     */
    @Override
    public void initView() {
        TextView topTitle = mBinding.getRoot().findViewById(R.id.top_title_tv);
        topTitle.setText(getString(R.string.voicelist_title));
        TextView menu = mBinding.getRoot().findViewById(R.id.top_menu_extend_iv);
        menu.setText("新建");
        menu.setOnClickListener(this);

    }

    /**
     * @param view
     *         点击的view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.top_menu_extend_iv:
                startActivity(new Intent(mContext, VoiceRecordActivity.class));
                break;
            case R.id.top_back_iv:
                finish();
                break;
        }
    }


}
