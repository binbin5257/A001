package cn.lds.ui;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import cn.lds.common.data.MessagesModel;
import cn.lds.common.enums.MsgType;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.manager.UMengManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.TimeHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityMessageBinding;
import cn.lds.ui.adapter.ControlHistoryListAdapter;
import cn.lds.ui.adapter.MessageListAdapter;
import cn.lds.widget.PullToRefreshLayout;
import cn.lds.widget.dialog.CenterListDialog;
import cn.lds.widget.dialog.ConfirmDialog;
import cn.lds.widget.dialog.LoadingDialog;
import cn.lds.widget.dialog.LoadingDialogUtils;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;
import cn.lds.widget.dialog.callback.OnDialogOnItemClickListener;

/**
 * 消息列表
 */
public class MessageActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = MessageActivity.class.getSimpleName();
    private ActivityMessageBinding mBinding;
    private List<MessagesModel.DataBean> messagesModelList;
    private MessageListAdapter messageListAdapter;

    private TextView top_title_tv;
    private ImageView top_back_iv;

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
    private List<String> centerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_message);
        initView();
        initListener();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
        getMsgData();
    }

    /**
     *获取消息数据
     */
    private void getMsgData() {
        String url = ModuleUrls.getMessages.
                replace("{page}", ToolsHelper.toString(page));
        RequestManager.getInstance().get(url, HttpApiKey.getMessages);
    }

    @Override
    public void initView() {
        top_title_tv = mBinding.getRoot().findViewById(R.id.top_title_tv);
        RelativeLayout head_view = mBinding.getRoot().findViewById(R.id.head_view);
        head_view.setBackgroundColor(getResources().getColor(R.color.bg_color));
        RelativeLayout loadmore_view = mBinding.getRoot().findViewById(R.id.loadmore_view);
        loadmore_view.setBackgroundColor(getResources().getColor(R.color.bg_color));
        TextView state_tv = mBinding.getRoot().findViewById(R.id.state_tv);
        state_tv.setTextColor(getResources().getColor(R.color.white));
        TextView loadstate_tv = mBinding.getRoot().findViewById(R.id.loadstate_tv);
        loadstate_tv.setTextColor(getResources().getColor(R.color.white));
        top_back_iv = mBinding.getRoot().findViewById(R.id.top_back_iv);
        mBinding.pullToRefreshView.setOnRefreshListener(this);
        top_title_tv.setText("消息");

        messagesModelList = new ArrayList<>();
        messageListAdapter = new MessageListAdapter(mContext, messagesModelList);
        mBinding.listview.setAdapter(messageListAdapter);

    }

    @Override
    public void initListener() {
        mBinding.listview.setOnItemLongClickListener(this);
        mBinding.listview.setOnItemClickListener(this);
        top_back_iv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.top_back_iv == id) {
            finish();
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
    protected void onDestroy() {
        super.onDestroy();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    /**
     * 行程里表api请求成功
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestMessagesSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.getMessages.equals(apiNo)
                || HttpApiKey.deleMessages.equals(apiNo)
                || HttpApiKey.markMessage.equals(apiNo)
           ))
            return;
        LoadingDialogUtils.dissmiss();
        if (apiNo.equals(HttpApiKey.getMessages)) {
            processMessageListData(httpResult);
        } else if(apiNo.equals(HttpApiKey.deleMessages)){
            ToolsHelper.showInfo(mContext, getString(R.string.delete_success));
            GET_DATA_TYPE = GET_REFRUSH_DATA;
            page = 0;
            initData();
        } else if(apiNo.equals(HttpApiKey.markMessage)){
            page = 0;
            initData();
        }
    }

    /**
     * 解析消息列表数据
     * @param httpResult 请求结果
     */
    private void processMessageListData( HttpResult httpResult ) {
        MessagesModel model = GsonImplHelp.get().toObject(httpResult.getResult(), MessagesModel.class);
        if (null == model || null == model.getData() || model.getData().isEmpty()) {
            messagesModelList.clear();
            messageListAdapter.setControlList(messagesModelList);
            return;
        }
        last = model.getPageable().isLast();
        if (GET_DATA_TYPE == GET_REFRUSH_DATA) {
            messagesModelList.clear();
            MyApplication.getInstance().runOnUiThreadDelay(new Runnable() {
                @Override
                public void run() {
                    mBinding.pullToRefreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
            }, 1000);


        } else {
            mBinding.pullToRefreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        }
        messagesModelList.addAll(model.getData());
        messageListAdapter.setControlList(messagesModelList);
    }

    /**
     * 行程里表api请求失败
     *
     * @param event
     *         失败返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestMessagesFailed(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.getMessages.equals(apiNo)
                || HttpApiKey.deleMessages.equals(apiNo)
                || HttpApiKey.markMessage.equals(apiNo)
        ))
            return;
        LoadingDialogUtils.dissmiss();
        ToolsHelper.showHttpRequestErrorMsg(mContext, httpResult);
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

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        MessagesModel.DataBean dataBean = messagesModelList.get(i);
        centerList.clear();
        if(dataBean.isChecked()){
            //弹出删除框
            centerList.add("删除");
        }else{
            //弹出标记为已读
            centerList.add("标记为已读");
            centerList.add("删除");
        }
        showCenterListDialog(dataBean);
        return true;
    }

    /**
     * 显示一个对话框
     * @param dataBean 消息对象
     */
    private void showCenterListDialog( final MessagesModel.DataBean dataBean) {
        CenterListDialog cancelAndDel = new CenterListDialog(this,this,centerList);
        cancelAndDel.setOnDialogOnItemClickListener(new OnDialogOnItemClickListener() {
            @Override
            public void onDialogItemClick( Dialog dialog, int position ) {
                dialog.dismiss();
                if(dataBean.isChecked()){
                    if(position == 0){
                        //删除该条消息

                        deleteMessageItem(dataBean.getId());
                    }
                }else{
                    if(position == 0){
                        //标记为已读
                        markMessageRead(dataBean.getId());
                    }else{
                        //删除该消息
                        deleteMessageItem(dataBean.getId());
                    }
                }


            }
        });
        cancelAndDel.show();
    }

    /**
     * 标记消息已读
     * @param id 消息id
     */
    private void markMessageRead( String id ) {
        String url = ModuleUrls.markMessage.replace("{id}",id);
        RequestManager.getInstance().put(url,HttpApiKey.markMessage);
    }

    /**
     * 删除消息
     * @param id 消息id
     */
    private void deleteMessageItem( final String id ) {
        ConfirmDialog dialog = new ConfirmDialog(this).setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClick( Dialog dialog, String clickPosition ) {
                switch (clickPosition){
                    case ClickPosition.SUBMIT:
                        LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
                        List<String> ids = new ArrayList<>();
                        ids.add(id);
                        String json = GsonImplHelp.get().toJson(ids);
                        RequestManager.getInstance().delete(ModuleUrls.deleMessages, HttpApiKey.deleMessages, json);
                        break;
                    case ClickPosition.CANCEL:
                        break;

                }
                dialog.dismiss();

            }
        });
        dialog.setContent("确认要删除该条消息？");
        dialog.setLeftButtonText("取消");
        dialog.setRightButtonText("确认");
        dialog.setTitle("提示");
        dialog.show();

    }

    @Override
    public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
        MessagesModel.DataBean dataBean = messagesModelList.get(position);
        markMessageRead(dataBean.getId());
        enterMessageDetailActivity(dataBean);

    }

    /**
     * 进入消息详情页面
     * @param dataBean
     */
    private void enterMessageDetailActivity( MessagesModel.DataBean dataBean ) {
        Intent intent = new Intent(this,MessageDetailActivity.class);
        Bundle bundle = new Bundle();
        if(TextUtils.isEmpty(dataBean.getTitle())){
            if(dataBean.getMessageType().equals(MsgType.ABNORMAL_MOVEMENT)){
                bundle.putString("TITLE","车辆盗警");
            }else if(dataBean.getMessageType().equals(MsgType.REMOTE_FAULT)){
                bundle.putString("TITLE","远程故障");
            }else if(dataBean.getMessageType().equals(MsgType.CARE_NOTIFACTION)){
                bundle.putString("TITLE","维保提醒");
            }else if(dataBean.getMessageType().equals(MsgType. SYSTEM_NOTIFYCATION)){
                bundle.putString("TITLE","系统消息");
            }else if(dataBean.getMessageType().equals(MsgType.TEXT_APPLICATION)){
                bundle.putString("TITLE","文本申请");
            }else if(dataBean.getMessageType().equals(MsgType.IMAGE_APPLICATION)){
                bundle.putString("TITLE","图片申请");
            }
        }else{
            bundle.putString("TITLE",dataBean.getTitle());
        }
        bundle.putString("CONTENT",dataBean.getContent());
        bundle.putString("TIME", TimeHelper.getTimeByType(dataBean.getSendTime(), TimeHelper.FORMAT1));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
