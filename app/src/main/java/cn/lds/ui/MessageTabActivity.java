package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.databinding.ActivityMessageTabBinding;
import cn.lds.ui.fragment.MessageFragment;

public class MessageTabActivity extends BaseActivity implements View.OnClickListener {

    private ImageView top_back_iv;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        initView();
        initListener();
    }

    @Override
    public void initView() {
        ActivityMessageTabBinding mBinding = DataBindingUtil.setContentView(this,R.layout.activity_message_tab);
        TextView top_title_tv = mBinding.getRoot().findViewById(R.id.top_title_tv);
        top_title_tv.setText("消息");
        top_back_iv = mBinding.getRoot().findViewById(R.id.top_back_iv);
        List<String> tabNews = Arrays.asList(getResources().getStringArray(R.array.news_tab));
        for(String tab : tabNews){
            MessageFragment fragment = MessageFragment.newInStance(tab);
        }



    }

    @Override
    public void initListener() {
        top_back_iv.setOnClickListener(this);
    }

    @Override
    public void onClick( View v ) {
        switch (v.getId()){
            case R.id.top_back_iv:
                finish();
                break;
        }
    }
}
