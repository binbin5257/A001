package cn.lds.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.lds.MyApplication;
import cn.lds.R;
import cn.lds.amap.MapFragment;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseFragmentActivity;
import cn.lds.common.data.ControlCarFailtrueEvent;
import cn.lds.common.data.DeleteFailHistotyEvent;
import cn.lds.common.data.LoginModel;
import cn.lds.common.data.MessageCountModel;
import cn.lds.common.data.TransactionsModel;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.manager.UMengManager;
import cn.lds.common.table.ControlCarFailtureHistoryTable;
import cn.lds.common.table.TableHelper;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityMainBinding;
import cn.lds.ui.fragment.ControlFragment;
import cn.lds.ui.fragment.DetailFragment;
import cn.lds.ui.fragment.MeFragment;
import cn.lds.ui.fragment.NaviFragment;
import cn.lds.ui.fragment.ServiceFragment;
import cn.lds.widget.ToastUtil;

/**
 * 主界面
 */
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener, DrawerLayout.DrawerListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding mBinding;
    private ArrayList<Fragment> fragmentList;
    private int checkFragment = 0;
    public static String UBI_FRAGMENT = "DetailFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initView();
        initListener();

        if(CacheHelper.getIsFirstEnterHome()){
            CacheHelper.setIsFirstEnterHome(false);
            //展示新手引导
            showGuideView();
        }
    }

    private void showGuideView() {
        mBinding.rlHomeGuide.setVisibility(View.VISIBLE);
        mBinding.ivGuideMessage.setVisibility(View.VISIBLE);
        mBinding.ivGuideCarInfo.setVisibility(View.GONE);
        mBinding.ivGuideCustomized.setVisibility(View.GONE);
        mBinding.ivGuideKnow.setVisibility(View.GONE);
        mBinding.ivGuideMessage.setOnClickListener(this);
        mBinding.ivGuideCarInfo.setOnClickListener(this);
        mBinding.ivGuideCustomized.setOnClickListener(this);
        mBinding.ivGuideKnow.setOnClickListener(this);
    }

    public void initView() {
        //底部菜单栏 和 fragent配置
        initFragments();
    }

    /**
     * 切换到地图页面并定位到车的位置
     */
    public void switeNaviFragment(){
        mBinding.mainCarNavi.setChecked(true);
        if(fragmentList != null && fragmentList.size() > 3){
            //定位到车的位置
            NaviFragment naviFragment = (NaviFragment) fragmentList.get(3);
            naviFragment .locationCar();
        }
    }

    private void initFragments() {
        if (fragmentList == null) {
            fragmentList = new ArrayList<>();
            fragmentList.add(new DetailFragment());
            fragmentList.add(new ControlFragment());
            fragmentList.add(new ServiceFragment());
            fragmentList.add(new NaviFragment());
            fragmentList.add(new MeFragment());
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            for (int i = 0; i < fragmentList.size(); i++) {
                Fragment fragment = fragmentList.get(i);
                transaction.add(R.id.content_flyt, fragment);
                transaction.hide(fragment);
            }
            transaction.show(fragmentList.get(checkFragment));
            transaction.commit();

        }
    }

    /**
     * 请求服务
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.getMessagesCount.equals(apiNo)))
            return;
        String result = httpResult.getResult();
        MessageCountModel model = GsonImplHelp.get().toObject(result, MessageCountModel.class);
        if(model != null && "success".equals(model.getStatus())){
            EventBus.getDefault().post(model);
        }
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        checkFragment = 0;
        switch (checkedId) {
            case R.id.main_car_detail:
                checkFragment = 0;
                break;
            case R.id.main_car_control:
                checkFragment = 1;
                break;
            case R.id.main_car_service:
                checkFragment = 2;
                break;
            case R.id.main_car_navi:
                checkFragment = 3;
                break;
            case R.id.main_car_me:
                checkFragment = 4;
                break;
            default:
                break;
        }
        //切换Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (int i = 0; i < fragmentList.size(); i++) {
            if (checkFragment == i) {
                resumeUMengEvent(i);
                transaction.show(fragmentList.get(i));
            } else {
                onPauseUMeng(i);
                transaction.hide(fragmentList.get(i));
            }
        }
        transaction.commit();
    }

    private void onPauseUMeng( int i ) {
        if(fragmentList.get(i).getClass().getSimpleName().equals("DetailFragment")){
            UMengManager.getInstance().onPausePage("DetailFragment");
        }else if(fragmentList.get(i).getClass().getSimpleName().equals("ControlFragment")){
            UMengManager.getInstance().onPausePage("ControlFragment");
        }else if(fragmentList.get(i).getClass().getSimpleName().equals("ServiceFragment")){
            UMengManager.getInstance().onPausePage("ServiceFragment");
        }else if(fragmentList.get(i).getClass().getSimpleName().equals("NaviFragment")){
            UMengManager.getInstance().onPausePage("NaviFragment");
        }else if(fragmentList.get(i).getClass().getSimpleName().equals("MeFragment")){
            UMengManager.getInstance().onPausePage("MeFragment");
        }
    }

    private void resumeUMengEvent( int i ) {
        if(fragmentList.get(i).getClass().getSimpleName().equals("DetailFragment")){
            UMengManager.getInstance().onResumePage("DetailFragment");
            UMengManager.getInstance().onClick("DetailFragment");
        }else if(fragmentList.get(i).getClass().getSimpleName().equals("ControlFragment")){
            UMengManager.getInstance().onResumePage("ControlFragment");
            UMengManager.getInstance().onClick("ControlFragment");

        }else if(fragmentList.get(i).getClass().getSimpleName().equals("ServiceFragment")){
            UMengManager.getInstance().onResumePage("ServiceFragment");
            UMengManager.getInstance().onClick("ServiceFragment");

        }else if(fragmentList.get(i).getClass().getSimpleName().equals("NaviFragment")){
            UMengManager.getInstance().onResumePage("NaviFragment");
            UMengManager.getInstance().onClick("NaviFragment");

        }else if(fragmentList.get(i).getClass().getSimpleName().equals("MeFragment")){
            UMengManager.getInstance().onResumePage("MeFragment");
            UMengManager.getInstance().onClick("MeFragment");

        }
    }

    public void initListener() {
        mBinding.radioGroup.setOnCheckedChangeListener(this);
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
        switch (id){
            case R.id.iv_guide_message:
                mBinding.ivGuideMessage.setVisibility(View.GONE);
                mBinding.ivGuideMessage.setClickable(false);
                mBinding.ivGuideCarInfo.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_guide_car_info:
                mBinding.ivGuideCarInfo.setVisibility(View.INVISIBLE);
                mBinding.ivGuideCarInfo.setClickable(false);
                mBinding.ivGuideCustomized.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_guide_customized:
                mBinding.ivGuideMessage.setVisibility(View.VISIBLE);
                mBinding.ivGuideCarInfo.setVisibility(View.VISIBLE);
                mBinding.ivGuideKnow.setVisibility(View.VISIBLE);
                mBinding.ivGuideCustomized.setClickable(false);
                break;
            case R.id.iv_guide_know:
                mBinding.rlHomeGuide.setVisibility(View.GONE);
                break;
        }

    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
    }

    @Override
    public void onDrawerClosed(View drawerView) {
    }

    @Override
    public void onDrawerStateChanged(int newState) {

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
    protected void onResume() {
        super.onResume();
        //获取未读信息数量
        RequestManager.getInstance().get(ModuleUrls.getMessagesCount,HttpApiKey.getMessagesCount);
    }

    @Override
    protected void onStop() {
        super.onStop();
        onPauseUMeng(checkFragment);
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestUpdate(final LoginModel.DataBean.DetailsBean detailsBean) {
        //设置极光tag分组
        Set<String> strings = new HashSet<>();
        String[] s = detailsBean.getTagList().split(",");
        strings.addAll(Arrays.asList(s));
        JPushInterface.setTags(mContext, 1, strings);
    }
    // 按两次返回键退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                if (!MyApplication.isExiting) {
                    MyApplication.getInstance().exitApp();
                    return true;
                } else {
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 0x02){
                if(fragmentList != null && fragmentList.size() == 5){
                    DetailFragment fragment = (DetailFragment) fragmentList.get(0);
                    fragment.initView();
                }
            }

        }
    }


    public String convertHttpKey(String type){
        String convertType = "";
        if(TextUtils.isEmpty(type)){
            return "";
        }
        switch (type){
            case HttpApiKey.unlock:
                convertType = "开启车锁";
                break;
            case HttpApiKey.lock:
                convertType = "关闭车锁";
                break;
            case HttpApiKey.flashLightWhistle:
                convertType = "启动闪灯鸣笛";
                break;
            case HttpApiKey.airConditionTurnOff:
                convertType = "关闭空调";
                break;
            case HttpApiKey.airConditionRefrigerate:
                convertType = "空调制冷启动";
                break;
            case HttpApiKey.airConditionHeat:
                convertType = "空调制热启动";
                break;
        }
        return convertType;
    }

    /**
     * 从数据库删除一条控车失败历史记录
     * @param vin
     */
    public void deleteOneControlCarFailHistory(String vin,String type){
        TableHelper.getInstance().deleteOneControlCarFailHistory(vin,type);
        EventBus.getDefault().post(new DeleteFailHistotyEvent());
    }

    /**
     * 从数据库获取一条控车失败历史记录
     * @param vin
     */
    public ControlCarFailtureHistoryTable getOneControlCarFailHistory( String vin){
        ControlCarFailtureHistoryTable table = TableHelper.getInstance().findFirstControlCarFailHistoryByVin(vin);
        return table;
    }

    /**
     * 数据库添加一条控车失败历史记录
     * @param currentApiNo
     * @param vin
     */
    public void insertOneControlCarFailHistory( String currentApiNo, String vin ,int timeOut) {
        ControlCarFailtureHistoryTable table = new ControlCarFailtureHistoryTable();
        table.setVin(vin);
        table.setType(currentApiNo);
        table.setTime(System.currentTimeMillis());
        table.setTimeOut(timeOut);
        TableHelper.getInstance().insertControlCarFailHistory(table);
    }
}
