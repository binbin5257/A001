package cn.lds.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.base.BaseFragment;
import cn.lds.common.base.UIInitListener;
import cn.lds.common.data.ConditionReportModel;
import cn.lds.common.data.ControlCarFailtrueEvent;
import cn.lds.common.data.ControlCarSuccessEvent;
import cn.lds.common.data.ControlCarWaitEvent;
import cn.lds.common.data.DeleteFailHistotyEvent;
import cn.lds.common.data.HiddenSuccessEvent;
import cn.lds.common.data.TransactionsModel;
import cn.lds.common.data.UpdateCarInfo;
import cn.lds.common.enums.TransactionsType;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.CarControlManager;
import cn.lds.common.table.ControlCarFailtureHistoryTable;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.TimeHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.FragmentCarControlBinding;
import cn.lds.ui.AccountSecurityActivity;
import cn.lds.ui.CarInpectActivity;
import cn.lds.ui.CheckLoginPswActivity;
import cn.lds.ui.ControlCarFailureResonActivity;
import cn.lds.ui.ControlHistoryActivity;
import cn.lds.ui.MainActivity;
import cn.lds.widget.dialog.ConfirmDialog;
import cn.lds.widget.dialog.ControlCarPromptDialog;
import cn.lds.widget.dialog.InputPinDialog;
import cn.lds.widget.dialog.KeyboardDialog;
import cn.lds.widget.dialog.LoadingDialogUtils;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;


/**
 * 车控界面
 */
public class ControlFragment extends BaseFragment implements UIInitListener, View.OnClickListener {
    private FragmentCarControlBinding mBinding;
    private ImageView tsp_log, check_iv;

    private CarControlManager carControlManager;
    private InputPinDialog inputPinDialog;
    private String currentApiNo;
    private String input_pin;
    private ControlCarFailtureHistoryTable oneControlCarFailHistory;
    private ControlCarPromptDialog controlCarPromptDialog;
    private RotateAnimation controlCarButtonAnimation;
    private KeyboardDialog keyboardDialog;
    private MainActivity activity;
    private Handler delay2sHandler= new Handler();
    private Handler delay1sHandler= new Handler();
    private boolean isControlCarFailture = false;

    public ControlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailFragment.
     */
    public static ControlFragment newInstance() {
        ControlFragment fragment = new ControlFragment();
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
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_car_control, null, false);
        initView();
        initListener();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated(view, savedInstanceState);
        //在本地数据库获取当前车辆控操作失败历史
        activity = (MainActivity) getActivity();
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

    @Override
    public void initView() {
        TextView topTitle = mBinding.getRoot().findViewById(R.id.top_title_tv);
        topTitle.setText("车控");
        tsp_log = mBinding.getRoot().findViewById(R.id.top_back_iv);
        tsp_log.setImageResource(R.drawable.top_tsp_log);

        check_iv = mBinding.getRoot().findViewById(R.id.top_menu_iv);
        check_iv.setImageResource(R.drawable.top_check_iv);
        controlCarPromptDialog = new ControlCarPromptDialog(getActivity());
        controlCarPromptDialog.setOnImKnowClickListenter(new ControlCarPromptDialog.OnImKnowClickListenter() {
            @Override
            public void onClick( ControlCarPromptDialog dialog, boolean isPrompt ) {
                CacheHelper.setIsShowControlCarPrompt(isPrompt);
                showCustomPinDialog();
                dialog.dismiss();
            }
        });
        controlCarButtonAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.pull_rotate);
    }

    @Override
    public void initListener() {
        tsp_log.setOnClickListener(this);
        check_iv.setOnClickListener(this);
        mBinding.contrlDoorLlyt.setOnClickListener(this);
        mBinding.contrlLightingLlyt.setOnClickListener(this);
        mBinding.contrlColderLlyt.setOnClickListener(this);
        mBinding.contrlHeatLlyt.setOnClickListener(this);
        mBinding.seeSee.setOnClickListener(this);
        mBinding.ivRemove.setOnClickListener(this);
        mBinding.tvConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_back_iv:
                getActivity().startActivity(new Intent(getActivity(), ControlHistoryActivity.class));//车控历史记录
                break;
            case R.id.top_menu_iv:
                getActivity().startActivity(new Intent(getActivity(), CarInpectActivity.class));//车控历史记录
                break;
            case R.id.contrl_door_llyt:
                if (mBinding.contrlDoor.isChecked()) {//车锁已开,将要关闭车锁
                    requestControl(HttpApiKey.lock);
                } else {//车门已关，将要开启车门
                    requestControl(HttpApiKey.unlock);
                }
                break;
            case R.id.contrl_lighting_llyt:
                if (!mBinding.contrlLighting.isChecked()) {//开启闪灯鸣笛，只持续两秒
                    requestControl(HttpApiKey.flashLightWhistle);
                }
                break;
            case R.id.contrl_colder_llyt:
                if (mBinding.contrlColder.isChecked()) {//关闭空调
                    requestControl(HttpApiKey.airConditionTurnOff);
                } else {//空调制冷
                    requestControl(HttpApiKey.airConditionRefrigerate);
                }
                break;
            case R.id.contrl_heat_llyt:
                if (mBinding.contrlHeat.isChecked()) {//关闭空调
                    requestControl(HttpApiKey.airConditionTurnOff);
                } else {//空调制热
                    requestControl(HttpApiKey.airConditionHeat);
                }
                break;
            case R.id.see_see: //查看控车失败详情
                LookAtControlCarFailtureDetail();
                break;
            case R.id.tv_confirm:
                EventBus.getDefault().post(new HiddenSuccessEvent());
                break;
            case R.id.iv_remove:
                activity.deleteOneControlCarFailHistory(CacheHelper.getVin(),oneControlCarFailHistory.getType());
                break;

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hiddenSuccessBar(HiddenSuccessEvent event) {
        mBinding.rlControlSuccess.setVisibility(View.GONE);
        showControlCarHistory();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void controlCarFailtrue( ControlCarFailtrueEvent event){
        isControlCarFailture = true;
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

    /**
     * 初始化页面控车状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initControlFragmentStatus(ConditionReportModel.DataBean dataBean){

        //初始化车锁状态
        if (dataBean.isCarLocked()) {
            //关闭状态
            mBinding.contrlDoor.setChecked(false);
            mBinding.ivCarDoor.setVisibility(View.GONE);
        } else {
            //开启状态
            mBinding.contrlDoor.setChecked(true);
            mBinding.ivCarDoor.setVisibility(View.VISIBLE);
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

    /**
     * api请求
     *
     * @param apiNo
     *         url类别
     */
    private void requestControl(String apiNo) {
        currentApiNo = apiNo;
        isControlCarFailture = false;
        if (null == carControlManager) {
            carControlManager = CarControlManager.getInstance();
        }

        if(!CacheHelper.getIsShowControlCarPrompt()){
            controlCarPromptDialog.show();
        }else{
            showCustomPinDialog();
        }

    }

    private void showCustomPinDialog() {
        if(keyboardDialog == null){
            keyboardDialog = new KeyboardDialog(getActivity(), R.style.alex_dialog_anim_bottom2top);
            keyboardDialog.setOnKeyboardListener(new KeyboardDialog.OnKeyboardListener() {
                @Override
                public void inputFinsh( String pin ) {
                    input_pin = pin;
                    startControlCarOper();
                }

                @Override
                public void forgetPin() {
                    Intent intent = new Intent(getActivity(), CheckLoginPswActivity.class);
                    startActivity(intent);
                }
            });
        }
        if(CacheHelper.getIsDemo()){
            keyboardDialog.setTitleText("输入PIN码：1234");
        }else{
            keyboardDialog.setTitleText("输入PIN码");

        }
        keyboardDialog.show();
    }


    /**
     * 开始执行控车操作
     */
    private void startControlCarOper() {
        EventBus.getDefault().post(new ControlCarWaitEvent(currentApiNo));
        mBinding.rlControlWaite.setVisibility(View.VISIBLE);
        mBinding.rlControlSuccess.setVisibility(View.GONE);
        mBinding.rlControlFalture.setVisibility(View.GONE);
        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.pull_rotate);
        mBinding.loadingIcon.startAnimation(rotateAnimation);

        if(currentApiNo.equals(HttpApiKey.unlock)){
            mBinding.tvControlStatus.setText("开车锁执行中");
            mBinding.contrlDoor.setVisibility(View.GONE);
            mBinding.doorLoading.setVisibility(View.VISIBLE);
            mBinding.doorLoading.startAnimation(controlCarButtonAnimation);
            controlCarDoorAnimation(mBinding.ivCarDoor,mBinding.ivCarDoor2,R.anim.alpha_show,"open_door");
            BaseApplication.getInstance().runOnUiThreadDelay(new Runnable() {
                @Override
                public void run() {
                    carControlManager.requestControl(currentApiNo, input_pin);
                }
            },6000);
        }else if(currentApiNo.equals(HttpApiKey.lock)){
            mBinding.tvControlStatus.setText("关车锁执行中");
            mBinding.contrlDoor.setVisibility(View.GONE);
            mBinding.doorLoading.setVisibility(View.VISIBLE);
            mBinding.doorLoading.startAnimation(controlCarButtonAnimation);
            BaseApplication.getInstance().runOnUiThreadDelay(new Runnable() {
                @Override
                public void run() {
                    carControlManager.requestControl(currentApiNo, input_pin);
                }
            },6000);
            controlCarDoorAnimation(mBinding.ivCarDoor,mBinding.ivCarDoor2,R.anim.alpha_hidden, "close_door");
        }else if(currentApiNo.equals(HttpApiKey.flashLightWhistle)){
            mBinding.tvControlStatus.setText("闪灯鸣笛执行中");
            mBinding.contrlLighting.setVisibility(View.GONE);
            mBinding.lightingLoading.setVisibility(View.VISIBLE);
            mBinding.lightingLoading.startAnimation(controlCarButtonAnimation);
            BaseApplication.getInstance().runOnUiThreadDelay(new Runnable() {
                @Override
                public void run() {
                    carControlManager.requestControl(currentApiNo, input_pin);
                }
            },1000);
            carLightAnimation(mBinding.ivCarLight);
        }else if(currentApiNo.equals(HttpApiKey.airConditionTurnOff)){
            mBinding.tvControlStatus.setText("空调关闭执行中");
            if(mBinding.contrlColder.isChecked()){
                mBinding.contrlColder.setVisibility(View.GONE);
                mBinding.colderLoading.setVisibility(View.VISIBLE);
                mBinding.colderLoading.startAnimation(controlCarButtonAnimation);
            }else{
                mBinding.contrlHeat.setVisibility(View.GONE);
                mBinding.heatLoading.setVisibility(View.VISIBLE);
                mBinding.heatLoading.startAnimation(controlCarButtonAnimation);
            }
            BaseApplication.getInstance().runOnUiThreadDelay(new Runnable() {
                @Override
                public void run() {
                    carControlManager.requestControl(currentApiNo, input_pin);
                }
            },6000);
            controlAirAnimation(mBinding.ivCarAir,mBinding.ivCarTop,"air_close");

        }else if(currentApiNo.equals(HttpApiKey.airConditionRefrigerate)){
            mBinding.tvControlStatus.setText("空调制冷执行中");
            mBinding.contrlColder.setVisibility(View.GONE);
            mBinding.colderLoading.setVisibility(View.VISIBLE);
            mBinding.colderLoading.startAnimation(controlCarButtonAnimation);

            if(mBinding.ivCarAir.getVisibility() == View.VISIBLE){
                mBinding.ivCarAir.setVisibility(View.GONE);
            }
            mBinding.ivCarAir.setImageResource(R.drawable.bg_air_cool);
            BaseApplication.getInstance().runOnUiThreadDelay(new Runnable() {
                @Override
                public void run() {
                    carControlManager.requestControl(currentApiNo, input_pin);
                }
            },6000);
            controlAirAnimation(mBinding.ivCarAir,mBinding.ivCarTop,"air_open");
        }else if(currentApiNo.equals(HttpApiKey.airConditionHeat)){
            mBinding.tvControlStatus.setText("空调制热执行中");
            mBinding.contrlHeat.setVisibility(View.GONE);
            mBinding.heatLoading.setVisibility(View.VISIBLE);
            mBinding.heatLoading.startAnimation(controlCarButtonAnimation);
            if(mBinding.ivCarAir.getVisibility() == View.VISIBLE){
                mBinding.ivCarAir.setVisibility(View.GONE);
            }
            mBinding.ivCarAir.setImageResource(R.drawable.bg_air_heat);
            BaseApplication.getInstance().runOnUiThreadDelay(new Runnable() {
                @Override
                public void run() {
                    carControlManager.requestControl(currentApiNo, input_pin);
                }
            },6000);
            controlAirAnimation(mBinding.ivCarAir,mBinding.ivCarTop,"air_open");
        }
        controlCarEvent(false);
    }


    /**
     * 控车事件
     * @param type true 启用控车 false 禁止控车
     */
    private void controlCarEvent(boolean type) {

        mBinding.contrlDoorLlyt.setClickable(type);
        mBinding.contrlDoorLlyt.setFocusable(type);
        mBinding.contrlDoorLlyt.setFocusableInTouchMode(type);

        mBinding.contrlLightingLlyt.setClickable(type);
        mBinding.contrlLightingLlyt.setFocusable(type);
        mBinding.contrlLightingLlyt.setFocusableInTouchMode(type);

        mBinding.contrlColderLlyt.setClickable(type);
        mBinding.contrlColderLlyt.setFocusable(type);
        mBinding.contrlColderLlyt.setFocusableInTouchMode(type);

        mBinding.contrlHeatLlyt.setClickable(type);
        mBinding.contrlHeatLlyt.setFocusable(type);
        mBinding.contrlHeatLlyt.setFocusableInTouchMode(type);
    }


    @Override
    public void onStart() {
        super.onStart();

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
    public void onStop() {
        super.onStop();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    /**
     * 控车api请求成功
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestControl(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.transactions.equals(apiNo)))
            return;
        TransactionsModel transactionsModel = GsonImplHelp.get().toObject(httpResult.getResult(), TransactionsModel.class);
        final TransactionsType type = transactionsModel.getData().getResult();
        if (type == TransactionsType.WAITING_SEND || type == TransactionsType.SENT) {
            carControlManager.startLoop(2000, apiNo);
        } else if (type == TransactionsType.SUCCESS) {
            carControlManager.stopTimer();
            //可以继续控车
            controlCarEvent(true);
            controlCarSuccessUi(type);
            activity.deleteOneControlCarFailHistory(CacheHelper.getVin(),currentApiNo);
        } else if (type == TransactionsType.FAIL){
            LoadingDialogUtils.dissmiss();
            HttpRequestErrorEvent faltureEvent = new HttpRequestErrorEvent(httpResult);
            EventBus.getDefault().post(faltureEvent);
        }


    }

    private void controlCarSuccessUi( TransactionsType type ) {
        if (currentApiNo.equals(HttpApiKey.unlock)) {
            mBinding.doorLoading.clearAnimation();
            mBinding.doorLoading.setVisibility(View.GONE);
            mBinding.contrlDoor.setVisibility(View.VISIBLE);
            mBinding.contrlDoor.setChecked(true);
            mBinding.ivCarDoor.setVisibility(View.VISIBLE);
            mBinding.tvControlSuccessName.setText("开锁成功");
            CarControlManager.getInstance().conditionReport();
        } else if (currentApiNo.equals(HttpApiKey.lock)) {
            mBinding.doorLoading.clearAnimation();
            mBinding.doorLoading.setVisibility(View.GONE);
            mBinding.contrlDoor.setVisibility(View.VISIBLE);
            mBinding.contrlDoor.setChecked(false);
            mBinding.ivCarDoor.setVisibility(View.GONE);
            mBinding.tvControlSuccessName.setText("关锁成功");
            CarControlManager.getInstance().conditionReport();
        } else if (currentApiNo.equals(HttpApiKey.flashLightWhistle)) {
            mBinding.lightingLoading.clearAnimation();
            mBinding.lightingLoading.setVisibility(View.GONE);
            mBinding.contrlLighting.setVisibility(View.VISIBLE);
            mBinding.contrlLighting.setChecked(true);//2秒后自主 关闭
            mBinding.ivCarLight.setVisibility(View.VISIBLE);
            mBinding.tvControlSuccessName.setText("闪灯鸣笛成功");
            delay2sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBinding.ivCarLight.setVisibility(View.GONE);
                    mBinding.contrlLighting.setChecked(false);
                    delay2sHandler.removeCallbacks(this);
                }
            },2000);
        } else if (currentApiNo.equals(HttpApiKey.airConditionRefrigerate)) {
            mBinding.colderLoading.clearAnimation();
            mBinding.colderLoading.setVisibility(View.GONE);
            mBinding.contrlColder.setVisibility(View.VISIBLE);
            mBinding.contrlColder.setChecked(true);
            mBinding.ivCarAir.setVisibility(View.VISIBLE);
            mBinding.ivCarAir.setImageResource(R.drawable.bg_air_cool);
            mBinding.contrlHeat.setChecked(false);
            mBinding.tvControlSuccessName.setText("空调制冷启动成功");
            CarControlManager.getInstance().conditionReport();
        } else if (currentApiNo.equals(HttpApiKey.airConditionHeat)) {
            mBinding.heatLoading.clearAnimation();
            mBinding.heatLoading.setVisibility(View.GONE);
            mBinding.contrlHeat.setVisibility(View.VISIBLE);
            mBinding.contrlColder.setChecked(false);
            mBinding.ivCarAir.setVisibility(View.VISIBLE);
            mBinding.ivCarAir.setImageResource(R.drawable.bg_air_heat);
            mBinding.contrlHeat.setChecked(true);
            mBinding.tvControlSuccessName.setText("空调制热启动成功");
            CarControlManager.getInstance().conditionReport();

        } else if (currentApiNo.equals(HttpApiKey.airConditionTurnOff)) {
            mBinding.heatLoading.clearAnimation();
            mBinding.colderLoading.clearAnimation();
            mBinding.colderLoading.setVisibility(View.GONE);
            mBinding.heatLoading.setVisibility(View.GONE);
            mBinding.contrlHeat.setVisibility(View.VISIBLE);
            mBinding.contrlColder.setVisibility(View.VISIBLE);
            mBinding.contrlColder.setChecked(false);
            mBinding.ivCarAir.setVisibility(View.GONE);
            mBinding.contrlHeat.setChecked(false);
            mBinding.tvControlSuccessName.setText("关闭空调成功");
            CarControlManager.getInstance().conditionReport();

        }
        delay1sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.rlControlWaite.setVisibility(View.GONE);
                if(mBinding.rlControlSuccess.getVisibility() == View.GONE){
                    mBinding.rlControlSuccess.setVisibility(View.VISIBLE);
                    mBinding.rlControlFalture.setVisibility(View.GONE);
                    EventBus.getDefault().post(new ControlCarSuccessEvent(currentApiNo));
                    delay1sHandler.removeCallbacks(this);
                }
            }
        },1000);

        ToolsHelper.showInfo(getActivity(), type.getValue());
    }

    /**
     * 控车api请求失败
     *
     * @param event
     *         失败返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestControlFailed(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.transactions.equals(apiNo)
                ||HttpApiKey.lock.equals(apiNo)
                ||HttpApiKey.unlock.equals(apiNo)
                ||HttpApiKey.flashLightWhistle.equals(apiNo)
                ||HttpApiKey.airConditionRefrigerate.equals(apiNo)
                ||HttpApiKey.airConditionHeat.equals(apiNo)
                ||HttpApiKey.airConditionTurnOff.equals(apiNo)
        ))
            return;
        LoadingDialogUtils.dissmiss();
        mBinding.rlControlWaite.setVisibility(View.GONE);
        //可以继续控车
        controlCarEvent(true);
        if(HttpApiKey.transactions.equals(apiNo)){
            controlCarFaitureUi();
            if("Control_car_Time_out".equals(httpResult.getResult())){
                activity.insertOneControlCarFailHistory(currentApiNo, CacheHelper.getVin(),0);
            }else{
                ToolsHelper.showHttpRequestErrorMsg(getActivity(), httpResult);
                activity.insertOneControlCarFailHistory(currentApiNo, CacheHelper.getVin(),1);
            }
            EventBus.getDefault().post(new ControlCarFailtrueEvent(currentApiNo));
        }else{
            //处理控车失败显示状态
            controlCarFailedStatus(apiNo);
        }

    }

    /**
     * 控车失败ui处理
     */
    private void controlCarFaitureUi() {

        if(currentApiNo.equals(HttpApiKey.unlock)){
            mBinding.doorLoading.clearAnimation();
            mBinding.doorLoading.setVisibility(View.GONE);
            mBinding.contrlDoor.setVisibility(View.VISIBLE);
            mBinding.ivCarDoor.clearAnimation();
            mBinding.ivCarDoor.setVisibility(View.GONE);
        }else if(currentApiNo.equals(HttpApiKey.lock)){
            mBinding.doorLoading.clearAnimation();
            mBinding.doorLoading.setVisibility(View.GONE);
            mBinding.contrlDoor.setVisibility(View.VISIBLE);
            mBinding.ivCarDoor.clearAnimation();
            mBinding.ivCarDoor.setVisibility(View.VISIBLE);
        }else if(currentApiNo.equals(HttpApiKey.flashLightWhistle)){
            mBinding.lightingLoading.clearAnimation();
            mBinding.lightingLoading.setVisibility(View.GONE);
            mBinding.contrlLighting.setVisibility(View.VISIBLE);
            mBinding.ivCarLight.clearAnimation();
            mBinding.ivCarLight.setVisibility(View.GONE);
        }else if(currentApiNo.equals(HttpApiKey.airConditionRefrigerate) || currentApiNo.equals(HttpApiKey.airConditionHeat)){

            if(mBinding.contrlHeat.isChecked()){
                mBinding.heatLoading.clearAnimation();
                mBinding.heatLoading.setVisibility(View.GONE);
                mBinding.contrlHeat.setVisibility(View.VISIBLE);
                mBinding.ivCarTop.setVisibility(View.VISIBLE);
                mBinding.ivCarAir.clearAnimation();
                mBinding.ivCarAir.setImageResource(R.drawable.bg_air_heat);
            }else if(mBinding.contrlColder.isChecked()){
                mBinding.colderLoading.clearAnimation();
                mBinding.colderLoading.setVisibility(View.GONE);
                mBinding.contrlColder.setVisibility(View.VISIBLE);
                mBinding.ivCarTop.setVisibility(View.VISIBLE);
                mBinding.ivCarAir.clearAnimation();
                mBinding.ivCarAir.setImageResource(R.drawable.bg_air_cool);
            }else {
                mBinding.heatLoading.clearAnimation();
                mBinding.heatLoading.setVisibility(View.GONE);
                mBinding.contrlHeat.setVisibility(View.VISIBLE);
                mBinding.colderLoading.clearAnimation();
                mBinding.colderLoading.setVisibility(View.GONE);
                mBinding.contrlColder.setVisibility(View.VISIBLE);
                mBinding.ivCarTop.clearAnimation();
                mBinding.ivCarTop.setVisibility(View.GONE);
                mBinding.ivCarAir.clearAnimation();
                mBinding.ivCarAir.setVisibility(View.GONE);
            }
        }else if(currentApiNo.equals(HttpApiKey.airConditionTurnOff)){
            mBinding.heatLoading.clearAnimation();
            mBinding.heatLoading.setVisibility(View.GONE);
            mBinding.contrlHeat.setVisibility(View.VISIBLE);
            mBinding.colderLoading.clearAnimation();
            mBinding.colderLoading.setVisibility(View.GONE);
            mBinding.contrlColder.setVisibility(View.VISIBLE);
            mBinding.ivCarTop.setVisibility(View.VISIBLE);
            mBinding.ivCarAir.clearAnimation();
            mBinding.ivCarAir.setVisibility(View.VISIBLE);
        }
    }

    private void controlCarFailedStatus(String apiNo) {

    }

    /**
     * 空调控制动画
     * @param command 控制指令
     */
    private void controlAirAnimation( final ImageView ivCarAir, final ImageView carTopIv, String command ) {
        if(command.equals("air_open")){
            final AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_show);
             AlphaAnimation carTopAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_car_top);
             carTopIv.startAnimation(carTopAnimation);
             carTopIv.setVisibility(View.VISIBLE);
             carTopAnimation.setAnimationListener(new Animation.AnimationListener() {
                 @Override
                 public void onAnimationStart( Animation animation ) {

                 }

                 @Override
                 public void onAnimationEnd( Animation animation ) {
                        ivCarAir.startAnimation(alphaAnimation);
                        ivCarAir.setVisibility(View.VISIBLE);
                 }

                 @Override
                 public void onAnimationRepeat( Animation animation ) {

                 }
             });
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart( Animation animation ) {

                }

                @Override
                public void onAnimationEnd( Animation animation ) {
//                    carControlManager.requestControl(currentApiNo, input_pin);
                    if(isControlCarFailture){
                        carTopIv.clearAnimation();
                        carTopIv.setVisibility(View.GONE);
                        ivCarAir.clearAnimation();
                        ivCarAir.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat( Animation animation ) {

                }
            });
        }else{
            carTopIv.clearAnimation();
            carTopIv.setVisibility(View.GONE);
            final AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_hidden);
            ivCarAir.startAnimation(alphaAnimation);
            ivCarAir.setVisibility(View.GONE);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart( Animation animation ) {

                }

                @Override
                public void onAnimationEnd( Animation animation ) {
//                    carControlManager.requestControl(currentApiNo, input_pin);
                    if(isControlCarFailture){
                        ivCarAir.clearAnimation();
                        ivCarAir.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onAnimationRepeat( Animation animation ) {

                }
            });
        }



    }
    /**
     * 开/关门动画
     * @param command 控制指令
     */
    private void controlCarDoorAnimation( final ImageView ivCarDoor, final ImageView ivCarDoor2, int anim, final String command ) {
        final AlphaAnimation door1Animation = (AlphaAnimation) AnimationUtils.loadAnimation(getActivity(), anim);
        AlphaAnimation door2Animation = (AlphaAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_car_light);
        ivCarDoor2.startAnimation(door2Animation);
        ivCarDoor2.setVisibility(View.VISIBLE);
        door2Animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {

            }

            @Override
            public void onAnimationEnd( Animation animation ) {
                ivCarDoor.startAnimation(door1Animation);
                if(command.equals("open_door")){
                    ivCarDoor.setVisibility(View.VISIBLE);
                }else{
                    ivCarDoor.setVisibility(View.GONE);
                }
                ivCarDoor2.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat( Animation animation ) {

            }
        });
        door1Animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {

            }

            @Override
            public void onAnimationEnd( Animation animation ) {
                if(isControlCarFailture){
                    ivCarDoor.clearAnimation();
                    if(ivCarDoor.getVisibility() == View.GONE){
                        ivCarDoor.setVisibility(View.VISIBLE);
                    }else{
                        ivCarDoor.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onAnimationRepeat( Animation animation ) {

            }
        });

    }

    public void carLightAnimation(View view){
        AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_car_light);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {

            }

            @Override
            public void onAnimationEnd( Animation animation ) {

            }

            @Override
            public void onAnimationRepeat( Animation animation ) {

            }
        });
        view.startAnimation(alphaAnimation);
        mBinding.ivCarLight.setVisibility(View.VISIBLE);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCarInfo(UpdateCarInfo event) {
        showControlCarHistory();
        mBinding.contrlColder.setChecked(false);
        mBinding.contrlHeat.setChecked(false);
        mBinding.ivCarAir.clearAnimation();
        mBinding.ivCarAir.setVisibility(View.GONE);
    }
}
