package cn.lds.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.data.CarCheckModel;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.CarControlManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityCarInpectBinding;
import cn.lds.ui.adapter.CarCheckAdapter;
import cn.lds.ui.view.RatingBar;
import cn.lds.widget.dialog.LoadingDialogUtils;

/**
 * 车辆体检页面
 * Created by sibinbin on 18-3-16.
 */

public class CarInpectActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCarInpectBinding mBinding;

    private List<Integer> mList = new ArrayList<>();
    private CarCheckAdapter adapter;
    private int TIME = 2000;//定时的时间间隔
    private Handler handler = new Handler();
    private CarCheckModel checkModel;
    private Runnable runnable1;
    private Runnable runnable2;
    private Runnable runnable3;
    private Runnable runnable4;
    private Runnable runnable5;
    private Runnable runnable;

    private boolean checkFinish = false;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_car_inpect);
        initView();
        initListener();
    }


    @Override
    protected void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        handler.removeCallbacks(runnable1);
        handler.removeCallbacks(runnable2);
        handler.removeCallbacks(runnable3);
        handler.removeCallbacks(runnable4);
        handler.removeCallbacks(runnable5);
    }

    @Override
    public void initView() {
        TextView titile = mBinding.getRoot().findViewById(R.id.top_title_tv);
        titile.setText("车辆体检");
        CarControlManager.getInstance().faultLamp();
        AnimationSet animationSet=new AnimationSet(true);
        TranslateAnimation translateAnimation=new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,  //X轴的开始位置
                Animation.RELATIVE_TO_SELF, 0f,  //X轴的结束位置
                Animation.RELATIVE_TO_SELF, 0f,  //Y轴的开始位置
                Animation.RELATIVE_TO_SELF, 1.5f);  //Y轴的结束位置
        translateAnimation.setDuration(2000);
        translateAnimation.setRepeatCount(10000);
        animationSet.addAnimation(translateAnimation);
        mBinding.light.startAnimation(animationSet);



    }

    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        mBinding.rlBraking.setOnClickListener(this);
        mBinding.rlPower.setOnClickListener(this);
        mBinding.rlSteering.setOnClickListener(this);
        mBinding.rlSecurity.setOnClickListener(this);
        mBinding.rlCarBody.setOnClickListener(this);

    }

    /**
     * 点击事件
     *
     * @param view
     *         点击的view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.rl_power:
                if(checkModel != null && checkModel.getData() != null){
                    enterWebview(checkModel.getData().getPower());
                }
                break;
            case R.id.rl_steering:
                if(checkModel != null && checkModel.getData() != null){
                    enterWebview(checkModel.getData().getSteering());
                }
                break;
            case R.id.rl_security:
                if(checkModel != null && checkModel.getData() != null){
                    enterWebview(checkModel.getData().getSecurity());
                }
                break;
            case R.id.rl_braking:
                if(checkModel != null && checkModel.getData() != null){
                    enterWebview(checkModel.getData().getBraking());
                }
                break;
            case R.id.rl_car_body:
                if(checkModel != null && checkModel.getData() != null){
                    enterWebview(checkModel.getData().getBodyControl());
                }
                break;
        }
    }

    private void enterWebview( int score ) {
        if(score < 5){
            Intent intent = new Intent(this, WebviewActivity.class);
            intent.putExtra("URL", ModuleUrls.dealerUrl
                    .replace("{city}",CacheHelper.getCity())
                    .replace("{adCode}",CacheHelper.getCityAdCode())
                    .replace("{longitude}",CacheHelper.getLongitude())
                    .replace("{latitude}",CacheHelper.getLatitude())
                    .replace("{vin}",CacheHelper.getVin())
            );

            startActivity(intent);
        }

    }

    /**
     * 请求成功
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.faultLamp.equals(apiNo)

        ))
            return;
        LoadingDialogUtils.dissmiss();
        switch (apiNo){
            case HttpApiKey.faultLamp:
                processfaultLampData(httpResult);
                break;
        }

    }

    /**
     * 解析车检数据
     * @param httpResult
     */
    private void processfaultLampData( HttpResult httpResult ) {
        checkModel = GsonImplHelp.get().toObject(httpResult.getResult(), CarCheckModel.class);
        if(checkModel != null && checkModel.getData() != null){

            startCheck(mBinding.titlePower,
                    "动力系统 ！",
                    mBinding.contentPower,
                    "车辆发动机，变速箱，等传动系统",
                    "车辆发动机，变速箱，等传动系统",

//                    "动力系统异常，请到4S进行维修",
                    mBinding.starPower,checkModel.getData().getPower()

            );
            runnable1 = new Runnable() {
               @Override
               public void run() {
                   startCheck(mBinding.titleSecurity,
                           "安全系统 ！",
                           mBinding.contentSecurity,
                           "车辆安全气囊，车身雷达等",
                           "车辆安全气囊，车身雷达等",
//                           "安全系统异常，请到4S进行维修",
                           mBinding.starSecurity,checkModel.getData().getSecurity()
                   );
               }
           };
            handler.postDelayed(runnable1,2000);
            runnable2 = new Runnable() {
               @Override
               public void run() {
                   startCheck(mBinding.titleBraking,
                           "制动系统 ！",
                           mBinding.contentBraking,
                           "刹车管线，刹车制动盘等",
                           "刹车管线，刹车制动盘等",
//                           "制动系统异常，请到4S店维修！",
                           mBinding.starBraking,checkModel.getData().getBraking()
                   );
               }
           };
            handler.postDelayed(runnable2,4000);
            runnable3 = new Runnable() {
               @Override
               public void run() {
                   startCheck(mBinding.titleSteering,
                           "转向系统 ！",
                           mBinding.contentSteering,
                           "车辆转向机，转向助力等",
                           "车辆转向机，转向助力等",
//                           "转向系统异常，请到4S进行维修",
                           mBinding.starSteering,checkModel.getData().getSteering()
                   );


               }
           };
            handler.postDelayed(runnable3,6000);
            runnable4 = new Runnable() {
               @Override
               public void run() {
                   startCheck(mBinding.titleCarBody,
                           "车身控制系统 ！",
                           mBinding.contentCarBody,
                           "车身稳定控制系统，制动防抱死系统等",
                           "车身稳定控制系统，制动防抱死系统等",
//                           "车身控制系统异常，请到4S店维修！",
                           mBinding.starCarBody,checkModel.getData().getBodyControl()
                   );

               }
           };
            handler.postDelayed(runnable4,8000);
            runnable5 = new Runnable() {
                @Override
                public void run() {
                    mBinding.light.clearAnimation();
                    mBinding.light.setVisibility(View.INVISIBLE);
                }
            };
            handler.postDelayed(runnable5,10000);
        }


    }
    public void startCheck( final TextView title, final String titleStrError, final TextView content, final String contentSuccess, final String contentError, final RatingBar ratingBar, final int score){
        try {
            content.setText("检测中");
            content.setTextColor(Color.parseColor("#FF5B00"));
            runnable = new Runnable() {
               @Override
               public void run() {
                   ratingBar.setVisibility(View.VISIBLE);
                   ratingBar.setStar(score);
                   if(score < 4){
                       content.setText(contentSuccess);
                       content.setTextColor(Color.parseColor("#ffffff"));
                   }else{
                       content.setText(contentSuccess);
                       content.setTextColor(Color.parseColor("#ffffff"));
//                       mBinding.ivCheckCarError.setVisibility(View.VISIBLE);
//                       mBinding.ivCheckCarError.setImageResource(R.drawable.bg_car_check_error);
//                       title.setText(titleStrError);
//                       title.setTextColor(Color.parseColor("#FF3939"));
//                       content.setText(contentError);
//                       content.setTextColor(Color.parseColor("#FF3939"));
                   }

               }
           };
            handler.postDelayed(runnable,TIME);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPointerCaptureChanged( boolean hasCapture ) {

    }
}
