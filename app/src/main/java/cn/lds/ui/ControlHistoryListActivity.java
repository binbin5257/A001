package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.lds.MyApplication;
import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.data.ControlHistoryListModel;
import cn.lds.common.data.LoginModel;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityControlHistoryListBinding;
import cn.lds.ui.adapter.ControlHistoryListAdapter;
import cn.lds.widget.PullToRefreshLayout;
import cn.lds.widget.dialog.LoadingDialogUtils;

/**
 * 操作历史界面
 */
public class ControlHistoryListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener {
    private ActivityControlHistoryListBinding mBinding;
    private List<ControlHistoryListModel.DataBean> controlHistoryList;
    private ControlHistoryListAdapter controlHistoryListAdapter;

    private int page = 0;
    private int size = 20;
    //下拉刷新获取数据标示
    private int GET_REFRUSH_DATA = 1;
    //加载更多获取数据标示
    private int GET_LOAD_MORE_DATA = 2;
    //获取数据标示 1：下拉刷新 2：加载更多 默认下拉刷新
    private int GET_DATA_TYPE = GET_REFRUSH_DATA;
    //是否是最后一页 true 是； false 否
    private boolean last = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_control_history_list);
        initView();
        initListener();
        initData();
    }

    /**
     * 初始化 请求数据
     */
    private void initData() {
        LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
        String url = ModuleUrls.tspLog.
                replace("{vin}", CacheHelper.getVin()).
                replace("{page}", ToolsHelper.toString(page));
        RequestManager.getInstance().get(url, HttpApiKey.tspLog);

    }

    /**
     * 初始化view
     */
    @Override
    public void initView() {
        TextView titile = mBinding.getRoot().findViewById(R.id.top_title_tv);
        titile.setText("操作历史");
        controlHistoryList = new ArrayList<>();
        controlHistoryListAdapter = new ControlHistoryListAdapter(mContext, controlHistoryList);
        mBinding.historyListview.setAdapter(controlHistoryListAdapter);
        mBinding.historyListview.setOnItemClickListener(this);
    }

    /**
     * 初始化点击事件
     */
    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        mBinding.pullToRefreshView.setOnRefreshListener(this);
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
        }
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

    /**
     * 行程里表api请求成功
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestTspLogSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.tspLog.equals(apiNo)))
            return;
        LoadingDialogUtils.dissmiss();
        ControlHistoryListModel model = GsonImplHelp.get().toObject(httpResult.getResult(), ControlHistoryListModel.class);
        if (null == model || null == model.getData() || model.getData().isEmpty()) {
            return;
        }
        last = model.getPageable().isLast();
        if (GET_DATA_TYPE == GET_REFRUSH_DATA) {
            controlHistoryList.clear();
            MyApplication.getInstance().runOnUiThreadDelay(new Runnable() {
                @Override
                public void run() {
                    mBinding.pullToRefreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
            }, 1000);


        } else {
            mBinding.pullToRefreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        }
        controlHistoryList.addAll(model.getData());
        controlHistoryListAdapter.setControlList(controlHistoryList);
    }

    /**
     * 行程里表api请求失败
     *
     * @param event
     *         失败返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestTspLogFailed(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.tspLog.equals(apiNo)))
            return;
        LoadingDialogUtils.dissmiss();
        ToolsHelper.showHttpRequestErrorMsg(mContext, httpResult);
    }

    /**
     * 列表点击事件
     * 点击进入行驶轨迹
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        GET_DATA_TYPE = GET_REFRUSH_DATA;
        page = 0;
        initData();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        GET_DATA_TYPE = GET_LOAD_MORE_DATA;
        if (last) {
            mBinding.pullToRefreshView.loadmoreFinish(PullToRefreshLayout.FAIL_NO_DATA);
        } else {
            page++;
            initData();
        }


    }
}
