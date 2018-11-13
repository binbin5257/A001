package cn.lds.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.base.BaseFragment;
import cn.lds.common.base.UIInitListener;
import cn.lds.common.data.AdvertisementModel;
import cn.lds.common.data.ControlCarFailtrueEvent;
import cn.lds.common.data.ControlCarSuccessEvent;
import cn.lds.common.data.ControlCarWaitEvent;
import cn.lds.common.data.DeleteFailHistotyEvent;
import cn.lds.common.data.HiddenSuccessEvent;
import cn.lds.common.data.MessageCountModel;
import cn.lds.common.data.TokenModel;
import cn.lds.common.data.UpdateCarInfo;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.ImageManager;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.manager.UMengManager;
import cn.lds.common.table.ControlCarFailtureHistoryTable;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.TimeHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.FragmentCarServiceBinding;
import cn.lds.ui.ControlCarFailureResonActivity;
import cn.lds.ui.MainActivity;
import cn.lds.ui.MessageActivity;
import cn.lds.ui.SettingActivity;
import cn.lds.ui.WebviewActivity;
import cn.lds.ui.view.banner.MZBannerView;
import cn.lds.ui.view.banner.MZHolderCreator;
import cn.lds.ui.view.banner.MZViewHolder;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.ConfirmDialog;
import cn.lds.widget.dialog.LoadingDialogUtils;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;


/**
 * 服务界面
 */
public class ServiceFragment extends BaseFragment implements UIInitListener, View.OnClickListener {
    private static final String TAG = "ServiceFragment";
    private FragmentCarServiceBinding mBinding;
    private ImageView main_logo, msg_notice;
    private List<String> bannerList = new ArrayList<>();
    private int index;
    private AdvertisementModel model;
    private MainActivity activity;
    private ControlCarFailtureHistoryTable oneControlCarFailHistory;
    private String service_type = "";



    public ServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailFragment.
     */
    public static ServiceFragment newInstance() {
        ServiceFragment fragment = new ServiceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_car_service, null, false);
        initView();
        initListener();
        getAdvertisementData();
        return mBinding.getRoot();

    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();
        showControlCarHistory();
    }

    @Override
    public void initView() {
        TextView topTitle = mBinding.getRoot().findViewById(R.id.top_title_tv);
        topTitle.setText("服务");
        main_logo = mBinding.getRoot().findViewById(R.id.top_back_iv);
        main_logo.setImageResource(R.drawable.main_top_icon);
        main_logo.setVisibility(View.GONE);
        main_logo.setVisibility(View.INVISIBLE);
        msg_notice = mBinding.getRoot().findViewById(R.id.top_menu_iv);
        msg_notice.setImageResource(R.drawable.main_top_notices);


        mBinding.serviceBanner.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int position) {
                if(model != null && model.getData() != null && model.getData().size() > 0){
                    String url = model.getData().get(position).getUrl();
                    if(url.contains("token")){
                        MainActivity.UBI_FRAGMENT = "ServiceFragment";
                        service_type = "ubi_fragment";
                        LoadingDialogUtils.showVertical(getActivity(), "请稍候");
                        getToken();
                    }else{
                        WebviewActivity.enterWebviewActivity(getActivity(),url);

                    }
                }
            }
        });
        mBinding.serviceBanner.addPageChangeLisnter(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    /**
     * 获取活动数据
     */
    private void getAdvertisementData() {
        String url = ModuleUrls.advertisement;
        RequestManager.getInstance().get(url, HttpApiKey.advertisement);
    }



    @Override
    public void initListener() {
//        main_logo.setOnClickListener(this);
        msg_notice.setOnClickListener(this);
        mBinding.serviceIv1.setOnClickListener(this);
        mBinding.serviceIv2.setOnClickListener(this);
        mBinding.serviceIv3.setOnClickListener(this);
        mBinding.tvConfirm.setOnClickListener(this);
        mBinding.seeSee.setOnClickListener(this);
        mBinding.ivRemove.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_back_iv:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.top_menu_iv:
                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            case R.id.service_iv1:
                MainActivity.UBI_FRAGMENT = "ServiceFragment";
                service_type = "ubi_fragment";
                LoadingDialogUtils.showVertical(getActivity(), "请稍候");
                getToken();
                break;
            case R.id.service_iv2:
                service_type = "maintanceUrl";
                MainActivity.UBI_FRAGMENT = "ServiceFragment";
                LoadingDialogUtils.showVertical(getActivity(), "请稍候");
                getToken();


                break;
            case R.id.service_iv3:
                WebviewActivity.enterWebviewActivity(getActivity(),ModuleUrls.rescue);
                break;
            case R.id.tv_confirm:
                EventBus.getDefault().post(new HiddenSuccessEvent());
                break;
            case R.id.see_see: //查看控车失败详情
                LookAtControlCarFailtureDetail();
                break;
            case R.id.iv_remove:
                activity.deleteOneControlCarFailHistory(CacheHelper.getVin(),oneControlCarFailHistory.getType());
                break;
        }
    }


    /**
     * 查看控车失败详情
     */
    private void LookAtControlCarFailtureDetail() {
        final String content = "您在" + TimeHelper.getTimeByType(oneControlCarFailHistory.getTime(),TimeHelper.FORMAT9) + activity.convertHttpKey(oneControlCarFailHistory.getType()) +"操作没有成功";
        ConfirmDialog dialog = new ConfirmDialog(getActivity()).setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClick( Dialog dialog, String clickPosition ) {
                switch (clickPosition){
                    case ClickPosition.SUBMIT:
                        // TODO1.跳转到失败原因界面 2.删除本地数据库该条失败记录
                        Intent intent = new Intent(getContext(), ControlCarFailureResonActivity.class);
                        intent.putExtra("reason",content);
                        startActivity(intent);
                        activity.deleteOneControlCarFailHistory(oneControlCarFailHistory.getVin(),oneControlCarFailHistory.getType());
                        break;
                    case ClickPosition.CANCEL:
                        activity.deleteOneControlCarFailHistory(CacheHelper.getVin(),oneControlCarFailHistory.getType());
                        break;
                }
                dialog.dismiss();
            }
        });
        dialog.setTitle("操作失败提示");
        dialog.setContent(content);
        dialog.setLeftButtonText("我知道了");
        dialog.setRightButtonText("查看详情");
        dialog.show();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteCarFailtrueHistory( DeleteFailHistotyEvent event){
        mBinding.rlControlWaite.setVisibility(View.GONE);
        mBinding.rlControlSuccess.setVisibility(View.GONE);
        showControlCarHistory();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void controlCarFailtrue( ControlCarFailtrueEvent event){
        mBinding.rlControlWaite.setVisibility(View.GONE);
        mBinding.rlControlSuccess.setVisibility(View.GONE);
        showControlCarHistory();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hiddenSuccessBar(HiddenSuccessEvent event) {
        mBinding.rlControlSuccess.setVisibility(View.GONE);
        showControlCarHistory();
    }

    private void showControlCarHistory() {
        oneControlCarFailHistory = activity.getOneControlCarFailHistory(CacheHelper.getVin());
        if(null == oneControlCarFailHistory){
            mBinding.rlControlFalture.setVisibility(View.GONE);
        }else{
            mBinding.rlControlFalture.setVisibility(View.VISIBLE);
            mBinding.tvControlName.setText(oneControlCarFailHistory.getContent());
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void controCarWaiting( ControlCarWaitEvent event){
        mBinding.rlControlSuccess.setVisibility(View.GONE);
        mBinding.rlControlFalture.setVisibility(View.GONE);
        mBinding.rlControlWaite.setVisibility(View.VISIBLE);
        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.pull_rotate);
        mBinding.loadingIcon.startAnimation(rotateAnimation);
        switch (event.getmType()){
            case HttpApiKey.unlock:
                mBinding.tvControlStatus.setText("开车锁执行中");
                break;
            case HttpApiKey.lock:
                mBinding.tvControlStatus.setText("关车锁执行中");
                break;
            case HttpApiKey.flashLightWhistle:
                mBinding.tvControlStatus.setText("闪灯鸣笛执行中");
                break;
            case HttpApiKey.airConditionRefrigerate:
                mBinding.tvControlStatus.setText("空调制冷执行中");
                break;
            case HttpApiKey.airConditionHeat:
                mBinding.tvControlStatus.setText("空调制热执行中");
                break;
            case HttpApiKey.airConditionTurnOff:
                mBinding.tvControlStatus.setText("空调关闭执行中");

                break;

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void controlCarSuccess( ControlCarSuccessEvent event){
        mBinding.rlControlWaite.setVisibility(View.GONE);
        mBinding.rlControlFalture.setVisibility(View.GONE);
        mBinding.rlControlSuccess.setVisibility(View.VISIBLE);
        switch (event.getmType()){
            case HttpApiKey.unlock:
                mBinding.tvControlSuccessName.setText("开锁成功");
                break;
            case HttpApiKey.lock:
                mBinding.tvControlSuccessName.setText("关锁成功");
                break;
            case HttpApiKey.flashLightWhistle:
                mBinding.tvControlSuccessName.setText("闪灯鸣笛成功");
                break;
            case HttpApiKey.airConditionRefrigerate:
                mBinding.tvControlSuccessName.setText("空调制冷启动成功");
                break;
            case HttpApiKey.airConditionHeat:
                mBinding.tvControlSuccessName.setText("空调制热启动成功");
                break;
            case HttpApiKey.airConditionTurnOff:
                mBinding.tvControlSuccessName.setText("关闭空调成功");
                break;

        }
    }

    /**
     * 请求成功
     *
     * @param event
     *         返回数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void respSuccess(HttpRequestEvent event) {HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.getToken.equals(apiNo)
                ||HttpApiKey.advertisement.equals(apiNo)
        ))
            return;
        LoadingDialogUtils.dissmiss();
        switch (apiNo){
            case HttpApiKey.getToken:
                processToken(httpResult);
                break;
            case HttpApiKey.advertisement:
                showAdvertisementBanner(httpResult);
                break;
        }

    }

    /**
     * 显示服务轮播图
     * @param httpResult
     */
    private void showAdvertisementBanner( HttpResult httpResult ) {
        String result = httpResult.getResult();
        model = GsonImplHelp.get().toObject(result,AdvertisementModel.class);
        if(model != null && model.getData() != null){}
        bannerList.clear();
        List<AdvertisementModel.DataBean> datas= model.getData();
        for(AdvertisementModel.DataBean data : datas){
            bannerList.add(data.getPictureUrl());
        }
        mBinding.serviceBanner.setIndicatorVisible(true);
        mBinding.serviceBanner.setPages(bannerList, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
        mBinding.serviceBanner.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageCountSuccess(MessageCountModel model) {
        if(model.getData() > 0){
            msg_notice.setImageResource(R.drawable.main_top_notices_red);
        }else{
            msg_notice.setImageResource(R.drawable.main_top_notices);
        }

    }
    /**
     * 请求失败
     *
     * @param event
     *         返回数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void respFail(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.getToken.equals(apiNo)
        ))
            return;
        LoadingDialogUtils.dissmiss();
        switch (apiNo){
            case HttpApiKey.getToken:
                if(MainActivity.UBI_FRAGMENT.equals(TAG)){
                    ToastUtil.showToast(getActivity(),"账号异常，请联系经销商解决");

                   enterService(new TokenModel());

                }

                break;
        }

    }

    /**
     * 解析token
     * @param httpResult
     */
    private void processToken( HttpResult httpResult ) {
        TokenModel model = GsonImplHelp.get().toObject(httpResult.getResult(), TokenModel.class);
        if(model != null){
            if(MainActivity.UBI_FRAGMENT.equals("ServiceFragment")){
                if(!TextUtils.isEmpty(model.getData())){
                    enterService( model );

                }else{
                    ToastUtil.showToast( getActivity(),"账号异常，请联系经销商解决" );
                }

            }
        }else{
            ToastUtil.showToast( getActivity(),"账号异常，请联系经销商解决" );
        }

    }

    private void enterService( TokenModel model ) {
        if("maintanceUrl".equals( service_type )){
            WebviewActivity.enterWebviewActivity(getActivity(), ModuleUrls.maintanceUrl
                    .replace("{city}", CacheHelper.getCity())
                    .replace("{adCode}",CacheHelper.getCityAdCode())
                    .replace("{longitude}",CacheHelper.getLongitude())
                    .replace("{latitude}",CacheHelper.getLatitude())
                    .replace("{token}",model.getData())
                    .replace("{vin}",CacheHelper.getVin()));
        }else if("ubi_fragment".equals( service_type )){
            WebviewActivity.enterWebviewActivity(getActivity(),"http://webservice.cihon.cn/activity-leopaard/#/?token="+ model.getData() +"&vin="
                    + CacheHelper.getVin());
//            WebviewActivity.enterWebviewActivity(getActivity(),"http://123.57.60.91/activity-leopaard/#/?token=testtoken&vin=testvin");
        }
    }

    /**
     * 获取token
     */
    private void getToken() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("password",BaseApplication.getInstance().jbgsn);
            RequestManager.getInstance().post(ModuleUrls.getToken, HttpApiKey.getToken,jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCarInfo(UpdateCarInfo event) {
        showControlCarHistory();
    }


    public static class BannerViewHolder implements MZViewHolder<String> {
        private SimpleDraweeView mImageView;

        @Override
        public View createView(Context context) {
            // 返回页面布局文件
            View view = LayoutInflater.from(context).inflate(R.layout.layout_img, null);
            mImageView = view.findViewById(R.id.iv_icon);
            return view;
        }

        @Override
        public void onBind(Context context, int position, String url) {
            // 数据绑定
            mImageView.setImageURI(url);
        }
    }


    @Override
    public void onAttach( Context context ) {
        super.onAttach(context);
        try {
            EventBus.getDefault().register(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        mBinding.serviceBanner.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.serviceBanner.start();
    }

}
