package cn.lds.ui;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import cn.lds.R;
import cn.lds.common.base.BaseFragmentActivity;
import cn.lds.common.image.GifFragment;
import cn.lds.databinding.ActivityImageTestBinding;

public class ImageTestActivity extends BaseFragmentActivity {
    ActivityImageTestBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_test);

        mBinding.gifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment fragment = GifFragment.newInstance("");
                transaction.replace(R.id.fragment_layout, fragment);
                transaction.commit();
            }
        });

        mBinding.cornerImage.setImageURI("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=189381644,2512543027&fm=27&gp=0.jpg");
        mBinding.circleImage.setImageURI("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=189381644,2512543027&fm=27&gp=0.jpg");
    }
}
