package cn.lds.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import cn.lds.common.data.FeedBackListModel;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityFeedBackListBinding;
import cn.lds.ui.adapter.FeedBackListAdapter;
import cn.lds.widget.PullToRefreshLayout;
import cn.lds.widget.dialog.LoadingDialogUtils;

/**
 * 反馈列表
 * Created by leadingsoft on 17/12/22.
 */

public class FeedBackListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener {
    private ActivityFeedBackListBinding mBinding;
    private List<FeedBackListModel.DataBean> dataBeans;
    private final int POSTFEEDBACKCODE = 999;
    private FeedBackListAdapter feedBackListAdapter;

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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_feed_back_list);
        initView();
        initListener();
        initData();
    }


    private void initData() {
        LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
        String url = ModuleUrls.feedbackList.replace("{page}", String.valueOf(page));
        RequestManager.getInstance().get(url, HttpApiKey.feedbackList);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.top_menu_iv:
                startActivityForResult(new Intent(mContext, FeedBackActivity.class), POSTFEEDBACKCODE);
//                ToolsHelper.showInfo(mContext, getString(R.string.happy_waitting));
                break;
        }
    }

    @Override
    public void initView() {
        TextView topTitle = mBinding.getRoot().findViewById(R.id.top_title_tv);
        topTitle.setText(getString(R.string.feedback_title));
//        TextView menu = mBinding.getRoot().findViewById(R.id.top_menu_extend_iv);
        ImageView menu = mBinding.getRoot().findViewById(R.id.top_menu_iv);
        menu.setImageResource(R.drawable.bg_create);
        menu.setOnClickListener(this);
        dataBeans = new ArrayList<>();
        feedBackListAdapter = new FeedBackListAdapter(mContext, dataBeans);
        mBinding.feedbackListview.setAdapter(feedBackListAdapter);
    }

    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        mBinding.getRoot().findViewById(R.id.top_menu_extend_iv).setOnClickListener(this);
        mBinding.feedbackListview.setOnItemClickListener(this);
        mBinding.pullToRefreshView.setOnRefreshListener(this);
    }

    /**
     * 意见列表api成功
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestFeedbackSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.feedbackList.equals(apiNo)))
            return;
        LoadingDialogUtils.dissmiss();
        FeedBackListModel listModel = GsonImplHelp.get().toObject(httpResult.getResult(), FeedBackListModel.class);
        if (null == listModel || null == listModel.getData())
            return;
        last = listModel.getPageable().isLast();
        if (GET_DATA_TYPE == GET_REFRUSH_DATA) {
            dataBeans.clear();
            MyApplication.getInstance().runOnUiThreadDelay(new Runnable() {
                @Override
                public void run() {
                    mBinding.pullToRefreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
            }, 1000);


        } else {
            mBinding.pullToRefreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        }

        dataBeans.addAll(listModel.getData());
        feedBackListAdapter.setDataBeans(dataBeans);
    }

    /**
     * 意见列表api请求失败
     *
     * @param event
     *         失败返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestFeedbackFailed(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.feedbackList.equals(apiNo)))
            return;
        LoadingDialogUtils.dissmiss();
        ToolsHelper.showHttpRequestErrorMsg(mContext, httpResult);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(mContext, FeedBackDetailActivity.class);
        intent.putExtra("data", dataBeans.get(i));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == POSTFEEDBACKCODE) {
                initData();
            }
        }
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
