package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.databinding.ActivityControlCarFailureReasonBinding;

/**
 * 控车失败原因
 * Created by sibinbin on 18-4-25.
 */

public class ControlCarFailureResonActivity extends BaseActivity {

    private ImageView backBtn;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void initView() {

        ActivityControlCarFailureReasonBinding mBinding =  DataBindingUtil.setContentView(this,R.layout.activity_control_car_failure_reason);
        TextView title = mBinding.getRoot().findViewById(R.id.top_title_tv);
        title.setText("失败原因");
        backBtn = mBinding.getRoot().findViewById(R.id.top_back_iv);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                finish();
            }
        });
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String reason = bundle.getString("reason");
            mBinding.content.setText(reason);
        }
    }
}
