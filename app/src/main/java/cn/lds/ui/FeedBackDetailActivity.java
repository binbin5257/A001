package cn.lds.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;


import java.util.List;

import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.data.FeedBackListModel;
import cn.lds.common.utils.TimeHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityFeedBackDetailBinding;
import cn.lds.ui.adapter.FeedBackDetaiGridAdapter;
import cn.lds.ui.adapter.FeedBackGridAdapter;

/**
 * 意见反馈详情
 */
public class FeedBackDetailActivity extends BaseActivity implements View.OnClickListener {

    ActivityFeedBackDetailBinding mBinding;
    FeedBackListModel.DataBean dataBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_feed_back_detail);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        TextView topTitle = mBinding.getRoot().findViewById(R.id.top_title_tv);
        topTitle.setText("反馈详情");
        dataBean = getIntent().getParcelableExtra("data");
        if (null == dataBean)
            return;
        mBinding.content.setText(dataBean.getContent());
        mBinding.creatTime.setText(TimeHelper.getTimeByType(dataBean.getCreatedDate(), TimeHelper.FORMAT7)+"  提交");
        if(TextUtils.isEmpty(dataBean.getTspContent())){
            mBinding.tspContent.setVisibility(View.GONE);
            mBinding.replyTime.setVisibility(View.GONE);
        }else{
            mBinding.tspContent.setVisibility(View.VISIBLE);
            mBinding.replyTime.setVisibility(View.VISIBLE);
            mBinding.tspContent.setText(dataBean.getTspContent());
            mBinding.replyTime.setText(TimeHelper.getTimeByType(dataBean.getLastModifiedDate(), TimeHelper.FORMAT7)+"  回复");
        }
        List<String> pictures = dataBean.getPictures();
        FeedBackDetaiGridAdapter adapter = new FeedBackDetaiGridAdapter(this,pictures);
        mBinding.gridIcon.setAdapter(adapter);
    }

    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_back_iv:
                finish();
                break;
        }
    }
}
