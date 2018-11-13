package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.databinding.ActivityMapBinding;

public class MapActivity extends BaseActivity {
    ActivityMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map);

//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        Fragment fragment = MapFragment.newInstance();
//        transaction.replace(R.id.map_framlayout, fragment);
//        transaction.commit();

        binding.mapview.onCreate(savedInstanceState);
        initView();
        initListener();
    }


}
