package cn.lds.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.base.BaseFragment;
import cn.lds.common.base.UIInitListener;
import cn.lds.common.data.ControlCarFailtrueEvent;
import cn.lds.common.data.ControlCarSuccessEvent;
import cn.lds.common.data.ControlCarWaitEvent;
import cn.lds.common.data.DeleteFailHistotyEvent;
import cn.lds.common.data.HiddenSuccessEvent;
import cn.lds.common.data.MessageCountModel;
import cn.lds.common.data.TokenModel;
import cn.lds.common.data.UpdateCarInfo;
import cn.lds.common.data.UserInfoModel;
import cn.lds.common.file.OnDownloadListener;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.AccountManager;
import cn.lds.common.manager.FilesManager;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.table.CarsTable;
import cn.lds.common.table.ControlCarFailtureHistoryTable;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.TimeHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.FragmentMyBinding;
import cn.lds.ui.CarListActivity;
import cn.lds.ui.ControlCarFailureResonActivity;
import cn.lds.ui.HelpActivity;
import cn.lds.ui.HomeCustomActivity;
import cn.lds.ui.MainActivity;
import cn.lds.ui.MessageActivity;
import cn.lds.ui.ProfileActivity;
import cn.lds.ui.SettingActivity;
import cn.lds.ui.TripListActivity;
import cn.lds.ui.WebviewActivity;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.ConfirmDialog;
import cn.lds.widget.dialog.LoadingDialogUtils;
import cn.lds.widget.dialog.VersionUpdateDialog;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;


/**
 * 我的界面
 */
    public class MeFragment extends BaseFragment implements UIInitListener, View.OnClickListener {
    private FragmentMyBinding mBinding;
    private ImageView main_logo, msg_notice;

    private UserInfoModel model;
    private MainActivity activity;
    private ControlCarFailtureHistoryTable oneControlCarFailHistory;
    private String service_type = "";

    public MeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailFragment.
     */
    public static MeFragment newInstance() {
        MeFragment fragment = new MeFragment();
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
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_my, null, false);
        initView();
        initListener();
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
        topTitle.setText("我的");
        main_logo = mBinding.getRoot().findViewById(R.id.top_back_iv);
        main_logo.setImageResource(R.drawable.main_top_icon);
        main_logo.setVisibility(View.INVISIBLE);

        msg_notice = mBinding.getRoot().findViewById(R.id.top_menu_iv);
        msg_notice.setImageResource(R.drawable.main_top_notices);
        AccountManager.getInstance().getPesionInfo();


    }

    @Override
    public void initListener() {
        msg_notice.setOnClickListener(this);
        mBinding.mePersonLlyt.setOnClickListener(this);
        mBinding.meOrderLlyt.setOnClickListener(this);
        mBinding.meTripIcon.setOnClickListener(this);
        mBinding.mePageIcon.setOnClickListener(this);
        mBinding.meVoiceIcon.setOnClickListener(this);
        mBinding.meCarIcon.setOnClickListener(this);
        mBinding.meSettingLlyt.setOnClickListener(this);
        mBinding.helpExplain.setOnClickListener(this);
        mBinding.tvConfirm.setOnClickListener(this);
        mBinding.seeSee.setOnClickListener(this);
        mBinding.ivRemove.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_menu_iv://进入消息列表页面
                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            case R.id.me_trip_icon://进入行驶历史页面
                startActivity(new Intent(getActivity(), TripListActivity.class));
                break;
            case R.id.me_page_icon://进入首页定制页面
                enterCarInfoShowConfigActivity();
                break;
            case R.id.me_voice_icon: //开机语音
                ToolsHelper.showInfo(getActivity(), getString(R.string.happy_waitting));
                break;
            case R.id.me_car_icon://进入车辆列表页面
                startCarListActivity();
                break;
            case R.id.me_setting_llyt:
            case R.id.top_back_iv:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;

            case R.id.me_order_llyt://进入我的订单
//                enterMyOrderActivity();
                LoadingDialogUtils.showVertical(getActivity(), "请稍候");
                MainActivity.UBI_FRAGMENT = "MeFragment";
                service_type = "myOrderUrl";
                getToken();
                break;
            case R.id.me_person_llyt: //进入个人信息页面
                enterProfileActivity();
                break;
            case R.id.help_explain: //进入帮助说明页面
                startHelpActivity();
                break;
            case R.id.tv_confirm:
                EventBus.getDefault().post(new HiddenSuccessEvent());
                break;
            case R.id.see_see: //查看控车失败详情
                LookAtControlCarFailtureDetail();
                break;
            case R.id.iv_remove://删除一条控车失败历史
                activity.deleteOneControlCarFailHistory(CacheHelper.getVin(),oneControlCarFailHistory.getType());
                break;
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

    /**
     * 进入帮助说明页面
     */
    private void startHelpActivity() {
        String versionContent = CacheHelper.getVersionContent();
        if(!TextUtils.isEmpty(versionContent)){
            Intent intent = new Intent(getActivity(), WebviewActivity.class);//http://127.0.0.1:1084 http://123.125.218.29:1082
            intent.putExtra("URL",CacheHelper.getVersionContent());
            startActivity(intent);
        }else{
            ToastUtil.showToast( getActivity(),"该版本无帮助说明" );
        }

    }

    private void startCarListActivity() {
        if(!"nullVin".equals(CacheHelper.getVin())){
            Intent carListIntent = new Intent(getActivity(), CarListActivity.class);
            startActivityForResult(carListIntent,0x02);
        }else{
            ToastUtil.showToast(BaseApplication.getInstance().getBaseContext(),"无车辆");
        }
    }

    private void enterProfileActivity() {
        if(model != null){
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("USERINFO", model.getData());
            intent.putExtras(bundle);
            startActivityForResult(intent,500);
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
    public void controlCarFailtrue( ControlCarFailtrueEvent event){
        mBinding.rlControlWaite.setVisibility(View.GONE);
        mBinding.rlControlSuccess.setVisibility(View.GONE);
        showControlCarHistory();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteCarFailtrueHistory( DeleteFailHistotyEvent event){
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
    public void messageCountSuccess(MessageCountModel model) {
        if(model.getData() > 0){
            msg_notice.setImageResource(R.drawable.main_top_notices_red);
        }else{
            msg_notice.setImageResource(R.drawable.main_top_notices);
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
     * 进入车辆信息配置页面
     */
    private void enterCarInfoShowConfigActivity() {
        Intent intent = new Intent(getActivity(), HomeCustomActivity.class);
        startActivity(intent);
    }

    private void enterMyOrderActivity( String result ) {
        TokenModel model = GsonImplHelp.get().toObject(result, TokenModel.class);
        if(!TextUtils.isEmpty(model.getData())){
            if("myOrderUrl".equals( service_type )){
                WebviewActivity.enterWebviewActivity(getActivity(),ModuleUrls.myOrderUrl
                        .replace("{adCode}",CacheHelper.getCityAdCode())
                        .replace("{longitude}",CacheHelper.getLongitude())
                        .replace("{latitude}",CacheHelper.getLatitude())
                        .replace("{token}",model.getData())
                        .replace("{vin}",CacheHelper.getVin()));
            }
        }else{
            ToastUtil.showToast( getActivity(),"账号异常，请联系经销商解决" );
        }

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        AccountManager.getInstance().getPesionInfo();
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCarInfo(UpdateCarInfo event) {
        showControlCarHistory();
        AccountManager.getInstance().getPesionInfo();

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void persionInfo(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.getPersonalInfo.equals(apiNo)
                || HttpApiKey.getToken.equals(apiNo)
        ))
            return;

        if(HttpApiKey.getPersonalInfo.equals(apiNo)){
            model = GsonImplHelp.get().toObject(httpResult.getResult(), UserInfoModel.class);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                ToastUtil.showToast(getActivity(), "获取个人信息成功！");
                    initPersonInfo(model);
                }

            });
        }else if(HttpApiKey.getToken.equals(apiNo)){
            if("MeFragment".equals(MainActivity.UBI_FRAGMENT)){
                enterMyOrderActivity(httpResult.getResult());
            }
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void persionInfo(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.getPersonalInfo.equals(apiNo)
                || HttpApiKey.getToken.equals(apiNo)
        ))
            return;
        if(HttpApiKey.getToken.equals(apiNo)){
            if("MeFragment".equals(MainActivity.UBI_FRAGMENT)){
                ToastUtil.showToast(getActivity(), "账号异常，请联系经销商解决");
                if("myOrderUrl".equals( service_type )){
                    WebviewActivity.enterWebviewActivity(getActivity(),ModuleUrls.myOrderUrl
                            .replace("{adCode}",CacheHelper.getCityAdCode())
                            .replace("{longitude}",CacheHelper.getLongitude())
                            .replace("{latitude}",CacheHelper.getLatitude())
                            .replace("{token}","")
                            .replace("{vin}",CacheHelper.getVin()));
                }
            }

        }else {
            ToolsHelper.showHttpRequestErrorMsg(getActivity(), event.httpResult);

        }

    }

    private void initPersonInfo(UserInfoModel model) {
        if(TextUtils.isEmpty( model.getData().getNickname() )){
            mBinding.userName.setText(model.getData().getName());
        }else {
            mBinding.userName.setText(model.getData().getNickname());
        }
        if (!TextUtils.isEmpty(model.getData().getAvatarFileRecordNo())) {
            //根据id加载图片
            mBinding.bgAvatar.setImageURI(ModuleUrls.displayFile + model.getData().getAvatarFileRecordNo());
            FilesManager.getInstance().download(model.getData().getAvatarFileRecordNo(), new OnDownloadListener() {
                @Override
                public void onDownloadSuccess() {
//                    ToastUtil.showToast(getActivity(),"下载成功.....");
                }

                @Override
                public void onDownloading( int progress ) {

                }

                @Override
                public void onDownloadFailed() {

                }
            });
        }
        CarsTable table = CacheHelper.getUsualcar();
        if (null != table) {
            if(TextUtils.isEmpty(table.getMode())){
                mBinding.currentCar.setText(table.getLicensePlate().toString());

            }else{
                mBinding.currentCar.setText(new StringBuilder().append(table.getMode()).append("|").append(table.getLicensePlate()).toString());
            }
        }

    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){
            if(requestCode == 0x02){
                String licensePlate = data.getStringExtra("no");
                String mode = data.getStringExtra("mode");
                if(TextUtils.isEmpty(mode)){
                    mBinding.currentCar.setText(licensePlate);

                }else{
                    mBinding.currentCar.setText(new StringBuilder().append(mode).append("|").append(licensePlate).toString());
                }
            }
        }
        if(requestCode == 500){
            AccountManager.getInstance().getPesionInfo();
        }

    }

//    public void updateCarInfo() {
//        AccountManager.getInstance().getPesionInfo();
//    }
}
