package cn.lds.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.data.ControlHistoryListModel;
import cn.lds.common.data.TripListModel;
import cn.lds.common.enums.TransactionsType;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.manager.UMengManager;
import cn.lds.common.utils.AnimationUtil;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.TimeHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActvityControlHistoryBinding;
import cn.lds.ui.adapter.ControlHistoryAdapter;
import cn.lds.ui.adapter.TripListAdapter;
import cn.lds.ui.view.calendar.CalendarLayout;
import cn.lds.ui.view.calendar.CalendarView;
import cn.lds.ui.view.group.BaseRecyclerAdapter;
import cn.lds.ui.view.group.GroupItemDecoration;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.LoadingDialogUtils;

/**
 * 控车操作历史界面
 * Created by sibinbin on 18-3-9.
 */

public class ControlHistoryActivity extends BaseActivity implements View.OnClickListener {

    private ActvityControlHistoryBinding mBinding;
    private int page = 0;
    //是否是最后一页 true 是； false 否
//    private boolean last = false;
    private List<ControlHistoryListModel.DataBean> controlHistoryList = new ArrayList<>();
    private ControlHistoryAdapter controlHistoryAdapter;
    private int LONG_AGO = 30;
    private Calendar theCa;
    private List<cn.lds.ui.view.calendar.Calendar> allTripDays = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private int groupHeight;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initData();
    }
    @Override
    protected void onStart() {
        super.onStart();
        UMengManager.getInstance().onResumePage("cmdHistory");

        try {
            EventBus.getDefault().register(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        UMengManager.getInstance().onPausePage("cmdHistory");

        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * api请求成功
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
        //更新列表
        controlHistoryList.addAll(model.getData());
        controlHistoryAdapter.updateAdapter(controlHistoryList);
        mBinding.recyclerView.notifyDataSetChanged();

        controlHistoryAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick( int position, long itemId ) {
                ControlHistoryListModel.DataBean dataBean = controlHistoryList.get(position);
                if(!(TransactionsType.SUCCESS.equals(dataBean.getCommandResult()) || TransactionsType.SENT.equals(dataBean.getCommandResult()))){
                    String content = "您在" + TimeHelper.getTimeByType(dataBean.getLastUpdateTime(),TimeHelper.FORMAT9) + convertHttpKey(dataBean) +"操作没有成功";
                    Intent intent = new Intent(mContext, ControlCarFailureResonActivity.class);
                    intent.putExtra("reason",content);
                    startActivity(intent);
                }
            }
        });
    }
    public String convertHttpKey(ControlHistoryListModel.DataBean dataBean){
        String convertType = "";

        switch (dataBean.getCommandType()){
            case UNLOCK:
                convertType = "开启车门";
                break;
            case LOCK:
                convertType = "关闭车门";
                break;
            case FLASHLIGHTWHISTLE:
                convertType = "启动闪灯鸣笛";
                break;
            case AIRCONDITIONTURNOFF:
                convertType = "关闭空调";
                break;
            case AIRCONDITIONREFRIGERATE:
                convertType = "空调制冷启动";
                break;
            case AIRCONDITIONHEAT:
                convertType = "空调制热启动";
                break;
        }
        return convertType;
    }

    /**
     * api请求失败
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
     * 初始化 请求数据
     */
    private void initData() {
        LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
        String url = ModuleUrls.tspLog.
                replace("{vin}", CacheHelper.getVin());
        RequestManager.getInstance().get(url, HttpApiKey.tspLog);

    }

    @Override
    public void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.actvity_control_history);
        TextView titile = mBinding.getRoot().findViewById(R.id.top_title_tv);
        titile.setText("操作历史");
        AnimationUtil.startRoateUp(ControlHistoryActivity.this,mBinding.ivDropDown);
        controlHistoryAdapter = new ControlHistoryAdapter(mContext);
        linearLayoutManager = new LinearLayoutManager(mContext);
        mBinding.recyclerView.setLayoutManager(linearLayoutManager);
        GroupItemDecoration<String, ControlHistoryListModel.DataBean> itemDecoration = new GroupItemDecoration<>(this);
        groupHeight = itemDecoration.getmGroupHeight();
        mBinding.recyclerView.addItemDecoration(itemDecoration);
        mBinding.recyclerView.setAdapter(controlHistoryAdapter);
        mBinding.calendarView.setWeeColor(
                getResources().getColor(R.color.car_detail_group_bg)
                , getResources().getColor(R.color.half_white));
        setCalendarViewRange();

    }

    /**
     * 设置日历控件显示范围
     */
    @SuppressLint("SetTextI18n")
    private void setCalendarViewRange() {
        long  end = System.currentTimeMillis();
        Date date = new Date(end);
        theCa = Calendar.getInstance();
        theCa.setTime(date);
        theCa.add(theCa.DATE, -LONG_AGO);//最后一个数字30可改，30天的意思
        Calendar calendar = Calendar.getInstance();
        mBinding.calendarView.setRange(theCa.get(Calendar.YEAR), theCa.get(Calendar.MONTH) + 1
                , calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        for(int i = 0; i <= LONG_AGO;i ++){
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);
            instance.add(instance.DATE,-i);
            allTripDays.add(setSchemeDate(instance.get(Calendar.YEAR), instance.get(Calendar.MONTH)+1, instance.get(Calendar.DAY_OF_MONTH), 0xFF40db25, ""));
        }
        mBinding.calendarView.setSchemeDate(allTripDays);
        mBinding.tvYearMonth.setText(calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月");

    }
    /**)
     * 标记绿色出行
     */
    private cn.lds.ui.view.calendar.Calendar setSchemeDate(int year, int month, int day, int color, String text) {
        cn.lds.ui.view.calendar.Calendar calendar = new cn.lds.ui.view.calendar.Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        mBinding.calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange( int year, int month ) {
                mBinding.tvYearMonth.setText(year + "年" + month + "月");
            }
        });

        mBinding.llDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if(mBinding.calendarLayout.isExpand()){
                    mBinding.calendarLayout.shrink();
                }else{
                    mBinding.calendarLayout.expand();
                }
            }
        });

        mBinding.calendarLayout.setOnCalendarLayoutToggleListener(new CalendarLayout.OnCalendarLayoutToggleListener() {
            @Override
            public void expand() {
                AnimationUtil.startRoateDown(ControlHistoryActivity.this,mBinding.ivDropDown);
                mBinding.aboveView.setVisibility(View.VISIBLE);
            }

            @Override
            public void shrink() {
                AnimationUtil.startRoateUp(ControlHistoryActivity.this,mBinding.ivDropDown);
                mBinding.aboveView.setVisibility(View.GONE);
            }
        });
        mBinding.aboveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                mBinding.calendarLayout.shrink();
            }
        });

        mBinding.calendarView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void onDateSelected( cn.lds.ui.view.calendar.Calendar calendar, boolean isClick ) {
                if(Integer.parseInt(
                        TimeHelper.getTimeByType(System.currentTimeMillis(),TimeHelper.FORMAT5)) < Integer.parseInt(calendar.toString())
                        || Integer.parseInt(TimeHelper.getTimeByType(theCa.getTime().getTime(),TimeHelper.FORMAT5)) > Integer.parseInt(calendar.toString())){
                    ToastUtil.showToast(mContext,"所选时间超出范围");
                    return;
                }

                if (isClick) {
                    String get = calendar.toDateString();
                    if (mBinding.calendarLayout.isExpand()) {
                        mBinding.calendarLayout.shrink();
                    }
                    int position = controlHistoryAdapter.getPositionByGroup(get);
                    mToPosition = position;
                    if(position  == -1){
                        Toast.makeText(mContext,get + "没有操作历史",Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    linearLayoutManager.scrollToPositionWithOffset(position,0);
//                    int top = linearLayoutManager.getChildAt(position).getTop();
//                    mBinding.recyclerView.scrollBy(0, top);
//                    mBinding.recyclerView.scrollTo( 0, top);
//                    mBinding.recyclerView.scrollToPosition( position );
                    smoothMoveToPosition( mBinding.recyclerView,position  );
//                    View viewByPosition = linearLayoutManager.findViewByPosition( position );
//                    int visibleItemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();


                }
            }
        });


        mBinding.recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged( RecyclerView recyclerView, int newState ) {
                super.onScrollStateChanged(recyclerView, newState);

                if (RecyclerView.SCROLL_STATE_IDLE == newState) {//滚动结束
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    //判断是当前layoutManager是否为LinearLayoutManager
                    // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                        //获取最后一个可见view的位置
                        int lastItemPosition = linearManager.findLastVisibleItemPosition();
                        //获取第一个可见view的位置
                        int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                        int middleItemPosition = (firstItemPosition + lastItemPosition) / 2;
                        String s = controlHistoryAdapter.getGroupByChildPosition(middleItemPosition);

                        if (!ToolsHelper.isNull(s)) {//选中的天
                            String[] strings = s.split("-");
                            setSeletedDay(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
                        }
                    }
                }
            }

            @Override
            public void onScrolled( RecyclerView recyclerView, int dx, int dy ) {
                super.onScrolled(recyclerView, dx, dy);
                //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                if (move){
                    move = false;
                    //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                    int n = mToPosition - linearLayoutManager.findFirstVisibleItemPosition();
                    if ( 0 <= n && n < mBinding.recyclerView.getChildCount()){
                        //获取要置顶的项顶部离RecyclerView顶部的距离
                        int top = recyclerView.getChildAt(n).getTop();
                        //最后的移动
                        mBinding.recyclerView.scrollBy(0, top);
                    }
                }

            }
        });
    }

    private boolean move;
    //记录目标项位置
    private int mToPosition;
    /**
     * 滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = linearLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (position <= firstItem ){
            //当要置顶的项在当前显示的第一个项的前面时
            mRecyclerView.scrollToPosition(position);
        }else if ( position <= lastItem ){
            //当要置顶的项已经在屏幕上显示时
            int top = mRecyclerView.getChildAt(position - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        }else{
            //当要置顶的项在当前显示的最后一项的后面时
            mRecyclerView.scrollToPosition(position);
            //这里这个变量是用在RecyclerView滚动监听里面的
            move = true;
        }
    }
    /**
     * 设置选中日期
     * @param year
     * @param month
     * @param day
     */
    private void setSeletedDay( int year, int month, int day ) {
        cn.lds.ui.view.calendar.Calendar calendars = new cn.lds.ui.view.calendar.Calendar();
        calendars.setYear(year);
        calendars.setMonth(month);
        calendars.setDay(day);
        mBinding.calendarView.selectDate(calendars);
    }


    @Override
    public void onClick( View v ) {
        switch (v.getId()) {
            case R.id.top_back_iv:
                finish();
                break;
        }
    }


}
