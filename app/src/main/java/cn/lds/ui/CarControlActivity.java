package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.data.TransactionsModel;
import cn.lds.common.enums.TransactionsType;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.CarControlManager;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityCarControlBinding;
import cn.lds.widget.dialog.LoadingDialogUtils;

/**
 * 车辆控制页面
 */
public class CarControlActivity extends BaseActivity implements View.OnClickListener {
    private ActivityCarControlBinding mBinding;
    private CarControlManager carControlManager;
    private ImageView backBtn;

    /**
     * 界面初始化
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_car_control);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        TextView title = mBinding.getRoot().findViewById(R.id.top_title_tv);
        title.setText("车辆控制");

        backBtn = mBinding.getRoot().findViewById(R.id.top_back_iv);
        try {
            EventBus.getDefault().register(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 初始化点击事件
     */
    @Override
    public void initListener() {
        backBtn.setOnClickListener(this);
        mBinding.airConditionHeat.setOnClickListener(this);
        mBinding.airConditionRefrigerate.setOnClickListener(this);
        mBinding.airConditionTurnOff.setOnClickListener(this);
        mBinding.flashLightWhistle.setOnClickListener(this);
        mBinding.lock.setOnClickListener(this);
        mBinding.unlock.setOnClickListener(this);
        mBinding.startEngine.setOnClickListener(this);
    }

    /**
     * 界面点击事件
     *
     * @param view
     *         点击的view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.airConditionHeat:
                requestControl(HttpApiKey.airConditionHeat);
                break;
            case R.id.airConditionRefrigerate:
                requestControl(HttpApiKey.airConditionRefrigerate);
                break;
            case R.id.airConditionTurnOff:
                requestControl(HttpApiKey.airConditionTurnOff);
                break;
            case R.id.flashLightWhistle:
                requestControl(HttpApiKey.flashLightWhistle);
                break;
            case R.id.lock:
                requestControl(HttpApiKey.lock);
                break;
            case R.id.unlock:
                requestControl(HttpApiKey.unlock);
                break;
            case R.id.startEngine:
                requestControl(HttpApiKey.startEngine);
                break;
            case R.id.top_back_iv:
                finish();
                break;

        }
    }

    /**
     * api请求
     *
     * @param apiNo
     *         url类别
     */
    private void requestControl(String apiNo) {
        LoadingDialogUtils.showVertical(mContext, "请稍候", false);
        if (null == carControlManager) {
            carControlManager = CarControlManager.getInstance();
        }
        carControlManager.requestControl(apiNo, "1234");
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
        TransactionsType type = transactionsModel.getData().getResult();
        if (type == TransactionsType.WAITING_SEND || type == TransactionsType.SENT) {
            carControlManager.startLoop(1000, apiNo);
        } else {
            LoadingDialogUtils.dissmiss();
            ToolsHelper.showInfo(mContext, type.getValue());
        }
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
        if (!(HttpApiKey.transactions.equals(apiNo)))
            return;
        LoadingDialogUtils.dissmiss();
        ToolsHelper.showHttpRequestErrorMsg(mContext, httpResult);
    }

    /**
     * 生命周期，销毁页面
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != carControlManager) {
            carControlManager.onDestory();
        }
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
