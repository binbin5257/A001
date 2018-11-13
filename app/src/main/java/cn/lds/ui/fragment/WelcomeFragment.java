package cn.lds.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.lds.R;
import cn.lds.databinding.FragmentWelcomeBinding;
import cn.lds.ui.LoginActivity;
import cn.lds.ui.WelcomeActivity;

public class WelcomeFragment extends Fragment implements View.OnClickListener {
    protected int position;
    private CountDownTimer timer;
    private WelcomeActivity activity;
    private FragmentWelcomeBinding mBinding;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_welcome, null, false);
        init();

        return mBinding.getRoot();
    }

    @SuppressLint("ValidFragment")
    public WelcomeFragment( int position, WelcomeActivity welcomeActivity) {
        this.position = position;
        this.activity = welcomeActivity;
    }

    public WelcomeFragment() {
    }

    protected void init() {
        switch (position) {
            case 0:
                mBinding.welcomeIv.setBackgroundResource(R.mipmap.bg_navi_one);
                break;
            case 1:
                mBinding.welcomeIv.setBackgroundResource(R.mipmap.bg_navi_second);
                break;
            case 2:
                mBinding.welcomeIv.setBackgroundResource(R.mipmap.bg_navi_third);
                break;
            case 3:
                mBinding.welcomeIv.setBackgroundResource(R.mipmap.bg_navi_four);
                break;
            case 4:
                mBinding.welcomeIv.setBackgroundResource(R.mipmap.bg_navi_five);
                break;

        }
        mBinding.welcomeIv.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.welcome_iv:
                if(position == 4){
                    enterLoginActivity();
                }
                break;
        }
    }

    public void enterLoginActivity(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
