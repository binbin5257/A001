package cn.lds.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import cn.lds.R;
import cn.lds.amap.util.AMapUtil;
import cn.lds.amap.util.ToastUtil;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseFragment;
import cn.lds.common.base.UIInitListener;
import cn.lds.common.data.CollectionsModel;
import cn.lds.common.data.ConditionReportModel;
import cn.lds.common.data.ConditionReportPositionModel;
import cn.lds.common.data.ControlCarFailtrueEvent;
import cn.lds.common.data.ControlCarSuccessEvent;
import cn.lds.common.data.ControlCarWaitEvent;
import cn.lds.common.data.DeleteFailHistotyEvent;
import cn.lds.common.data.HiddenSuccessEvent;
import cn.lds.common.data.HomeAndCompanyModel;
import cn.lds.common.data.MessageCountModel;
import cn.lds.common.data.UpdateCarInfo;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.CarControlManager;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.table.ControlCarFailtureHistoryTable;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.LogHelper;
import cn.lds.common.utils.TimeHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.FragmentCarNaviBinding;
import cn.lds.ui.CarLocationActivity;
import cn.lds.ui.CollectionsActivity;
import cn.lds.ui.ControlCarFailureResonActivity;
import cn.lds.ui.DealerListActivity;
import cn.lds.ui.MainActivity;
import cn.lds.ui.MapSearchListActivity;
import cn.lds.ui.MessageActivity;
import cn.lds.ui.SettingActivity;
import cn.lds.widget.dialog.ConfirmDialog;
import cn.lds.widget.dialog.LoadingDialogUtils;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;


/**
 * 导航界面
 */
public class NaviFragment extends BaseFragment implements UIInitListener, AMapLocationListener, LocationSource, View.OnClickListener {
    private static final String TAG = "NaviFragment";
    private FragmentCarNaviBinding mBinding;
    private AMap mAmap;
    // 声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    // 声明定位回调监听器
    public AMapLocationListener mLocationListener;
    private AMapLocation mMyLocationPoint;
    // 我的位置监听器
    private OnLocationChangedListener mLocationChangeListener = null;
    private ImageView notices;
    private ImageView top_icon;
    private Marker carMarker;
    private int GET_SEARCH_DATA = 1001;
    private MainActivity activity;
    private ControlCarFailtureHistoryTable oneControlCarFailHistory;

    private int postType = 0;
    private int postHome = 1;
    private int postCompy = 2;


    public NaviFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailFragment.
     */
    public static NaviFragment newInstance() {
        NaviFragment fragment = new NaviFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_car_navi, null, false);
        mBinding.mapMap.onCreate(savedInstanceState);
        initView();
        initListener();
        initMap();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();
        showControlCarHistory();

    }

    /**
     * 初始化 家和公司地址
     */
    private void initHomeAndCompany() {
        CarControlManager.getInstance().getHomeAndCompany();
    }



    @Override
    public void initView() {
        TextView topTitle = mBinding.getRoot().findViewById(R.id.top_title_tv);
        topTitle.setText("地图导航");
        top_icon = mBinding.getRoot().findViewById(R.id.top_back_iv);
        top_icon.setImageResource(R.drawable.main_top_icon);
        top_icon.setVisibility(View.INVISIBLE);

        notices = mBinding.getRoot().findViewById(R.id.top_menu_iv);
        notices.setImageResource(R.drawable.main_top_notices);
    }

    /**
     * 设置监听
     */
    @Override
    public void initListener() {
        mBinding.collect.setOnClickListener(this);
        mBinding.mapSearch.setOnClickListener(this);
        mBinding.home.setOnClickListener(this);
        mBinding.company.setOnClickListener(this);
        mBinding.location.setOnClickListener(this);
        mBinding.ivCar.setOnClickListener(this);
        mBinding.distributor.setOnClickListener(this);
        notices.setOnClickListener(this);
        mBinding.tvConfirm.setOnClickListener(this);
        mBinding.seeSee.setOnClickListener(this);
        mBinding.ivRemove.setOnClickListener(this);
        mBinding.cbLounk.setOnClickListener(this);
    }


    /**
     * 初始化地图
     */
    private void initMap() {
        if (mAmap == null) {
            mAmap = mBinding.mapMap.getMap();
            initMyLocation();
            setMapStyle();
        }
        mAmap.setLocationSource(this);// 设置定位监听
        mAmap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAmap.getUiSettings().setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        mAmap.getUiSettings().setScaleControlsEnabled(true);
        mAmap.setMyLocationEnabled(true);// 是否可触发定位并显示定位层
        mAmap.setTrafficEnabled(false);// 显示实时交通状况
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(
                BitmapDescriptorFactory.fromResource(R.drawable.navi_me_location));
        myLocationStyle.strokeColor(getResources().getColor(R.color.map_stroke_color));//设置定位蓝点精度圆圈的边框颜色的方法。
        myLocationStyle.radiusFillColor(getResources().getColor(R.color.map_stroke_color));//设置定位蓝点精度圆圈的填充颜色的方法。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);

        mAmap.setMyLocationStyle(myLocationStyle);
        if (null == carMarker) {
            ConditionReportModel.DataBean c = CarControlManager.getInstance().getCarDetail();
            if(c != null && c.getLongitude() != 0.0 && c.getLatitude() != 0.0){
                carMarker = mAmap.addMarker(
                        new MarkerOptions().icon(
                                BitmapDescriptorFactory.fromResource(R.drawable.navi_car_location)).position(
                                new LatLng(c.getLatitude(), c.getLongitude())));
            }

        }
        mAmap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick( Marker marker ) {
                if(carMarker != null && carMarker.getPosition() != null && carMarker.getPosition().toString().equals(marker.getPosition().toString())){
                    enterCarLocationActivity();
                }
                return false;
            }
        });
    }

    /**
     * 初始化定位服务
     */
    private void initLocation() {
        mLocationClient = new AMapLocationClient(getActivity());
        mLocationClient.setLocationListener(this);
        // 初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        // 设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        // 设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        // 设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        // 设置定位间隔,单位毫秒,默认为30s
        mLocationOption.setInterval(30000);
        // 给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 启动定位
        mLocationClient.startLocation();
    }

    /**
     * 初始化我的定位
     */
    private void initMyLocation() {
        mAmap.setLocationSource(this);
        mAmap.getUiSettings().setMyLocationButtonEnabled(true);
        mAmap.setMyLocationEnabled(true);
        mAmap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        mAmap.getUiSettings().setLogoPosition(
                AMapOptions.LOGO_POSITION_BOTTOM_LEFT);// logo位置
        mAmap.getUiSettings().setLogoBottomMargin(-100);//隐藏logo
        mAmap.getUiSettings().setScaleControlsEnabled(true);// 标尺开关
        mAmap.getUiSettings().setZoomControlsEnabled(false);
        mAmap.getUiSettings().setCompassEnabled(false);// 指南针开关
        LogHelper.d(
                "max = " + mAmap.getMaxZoomLevel() + "min = "
                        + mAmap.getMinZoomLevel());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBinding.mapMap.onPause();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();//ֹͣ��λ
        }
        deactivate();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.mapMap.onResume();
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
//        mAmap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onSaveInstanceState(Bundle arg0) {
        super.onSaveInstanceState(arg0);
        mBinding.mapMap.onSaveInstanceState(arg0);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
        mBinding.mapMap.onDestroy();
    }

    @Override
    public void deactivate() {
        mLocationChangeListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mLocationChangeListener = listener;
        if (mLocationClient == null) {
            initLocation();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                amapLocation.getCityCode();// 城市编码
                amapLocation.getAdCode();// 地区编码

                if (null == mMyLocationPoint) {//第一次定位
//                    LatLng latLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
//                    mAmap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
                mMyLocationPoint = amapLocation;
                mLocationChangeListener.onLocationChanged(mMyLocationPoint);
                CacheHelper.setLatitude(amapLocation.getLatitude());
                CacheHelper.setLongitude(amapLocation.getLongitude());
                CacheHelper.setCity(amapLocation.getCity());
                CacheHelper.setCityAdCode(amapLocation.getAdCode());
//                CacheHelper.setCityAdCode(amapLocation.getProvince());
                CarControlManager.getInstance().conditionReportPosition();//30s 获取一次车的位置

            } else {
                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                LogHelper.d(
                        "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
//                ToastUtil.show(getActivity(),"请开启定位权限");

            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_search://点击搜索框进入poi搜索页面
                startActivityForResult(new Intent(getActivity(), MapSearchListActivity.class), GET_SEARCH_DATA);
                break;
            case R.id.collect://进入收藏夹
                startActivity(new Intent(getActivity(), CollectionsActivity.class));
                break;
            case R.id.home://下发家地址poi到车机
//                postHomePoi();
                postType = postHome;
                initHomeAndCompany();
                break;
            case R.id.company: //下发公司地址poi到车机
//                postCompanyPoi();
                postType = postCompy;
                initHomeAndCompany();
                break;
            case R.id.location: //将个人位置设置为地图中心
                setMapCenterPhone();
                break;
            case R.id.iv_car: //设置车辆到地图中心
                locationCar();
                break;
            case R.id.distributor: //进入经销商列表页面
                getDealerListData();
//                startActivity(new Intent(getActivity(), DealerListActivity.class));
                break;
            case R.id.top_back_iv: //进入设置界面
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.top_menu_iv: //进入消息界面
                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            case R.id.tv_confirm: //控车成功 点击确认隐藏成功栏
                EventBus.getDefault().post(new HiddenSuccessEvent());
                break;
            case R.id.see_see: //查看控车失败详情
                lookAtControlCarFailtureDetail();
                break;
            case R.id.iv_remove:
                activity.deleteOneControlCarFailHistory(CacheHelper.getVin(),oneControlCarFailHistory.getType());
                break;
            case R.id.cb_lounk:
                if(mBinding.cbLounk.isChecked()){
                    mAmap.setTrafficEnabled(true);
                }else{
                    mAmap.setTrafficEnabled(false);
                }
                break;
        }
    }

    /**
     * 初始化 请求数据
     */
    private void getDealerListData() {
        LoadingDialogUtils.showVertical(getActivity(), "请稍候");
        String cityAdCode = CacheHelper.getCityAdCode();
        if(!TextUtils.isEmpty(cityAdCode)){
            String provinceCode = cityAdCode.substring(0, 2) + "0000";
            String cityCode = cityAdCode.substring(0, 4) + "00";
            String url = ModuleUrls.dealer.
                    replace("{vin}", CacheHelper.getVin())
                    .replace("{provinceCode}",provinceCode)
                    .replace("{cityCode}",cityCode)
                    .replace("{latitude}", CacheHelper.getLatitude())
                    .replace("{longitude}", CacheHelper.getLongitude());
            RequestManager.getInstance().get(url, HttpApiKey.dealer);
        }

    }

    /**
     * 设置手机为地图中心
     */
    private void setMapCenterPhone() {
        if(mMyLocationPoint != null){
            LatLng latLng = new LatLng(mMyLocationPoint.getLatitude(), mMyLocationPoint.getLongitude());
            mAmap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }else{
            ToastUtil.show(getActivity(),"无法获取到位置信息，请检查网络环境");
        }
    }

    /**
     * 下发公司poi
     */
    private void postCompanyPoi() {
        CollectionsModel.DataBean company = CarControlManager.getInstance().getCompany();
        if (null == company) {
            ToolsHelper.showInfo(getActivity(), "未设置公司地址");
        } else {
            LoadingDialogUtils.showVertical(getActivity(), getString(R.string.loading_waitting));
            CarControlManager.getInstance().postPoi(CarControlManager.getInstance().getCompany());
        }
    }
    /**
     * 下发家poi
     */
    private void postHomePoi() {
        CollectionsModel.DataBean home = CarControlManager.getInstance().getHome();
        if (null == home) {
            ToolsHelper.showInfo(getActivity(), "未设置家地址");
        } else {
            LoadingDialogUtils.showVertical(getActivity(), getString(R.string.loading_waitting));
            CarControlManager.getInstance().postPoi(CarControlManager.getInstance().getHome());
        }
    }


    /**
     * 查看控车失败详情
     */
    private void lookAtControlCarFailtureDetail() {
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
    public void hiddenSuccessBar(HiddenSuccessEvent event) {
        mBinding.rlControlSuccess.setVisibility(View.GONE);
        showControlCarHistory();
    }

    /**
     * 显示控车失败历史
     */
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
     * 进入车辆位置页面
     */
    private void enterCarLocationActivity() {
        Intent intent = new Intent(getActivity(), CarLocationActivity.class);
        Bundle bundle = new Bundle();

        startActivity(intent);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageCountSuccess(MessageCountModel model) {
        if(model.getData() > 0){
            notices.setImageResource(R.drawable.main_top_notices_red);
        }else{
            notices.setImageResource(R.drawable.main_top_notices);
        }

    }


    /**
     * 获取家和公司地址 api 成功
     *
     * @param event
     *         返回数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestHomeAndCompanySuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.getHomeAndCompany.equals(apiNo)
                || HttpApiKey.dealer.equals(apiNo)
        ))
            return;
        LoadingDialogUtils.dissmiss();
        switch (apiNo){
            case HttpApiKey.getHomeAndCompany:
                HomeAndCompanyModel model = GsonImplHelp.get().toObject(httpResult.getResult(), HomeAndCompanyModel.class);
                if (null == model || null == model.getData()) {
                    CarControlManager.getInstance().setCompany(null);
                    CarControlManager.getInstance().setHome(null);
                } else {
                    CarControlManager.getInstance().setCompany(model.getData().getCompany());
                    CarControlManager.getInstance().setHome(model.getData().getHome());
                }
                if(postType == postHome){
                    postHomePoi(); //下发家的Poi
                }else if(postType == postCompy){
                    postCompanyPoi();//下发公司的Poi
                }


                break;
            case HttpApiKey.dealer:
                Intent intent = new Intent(getActivity(),DealerListActivity.class);
                intent.putExtra("dealer_data",httpResult.getResult());
                startActivity(intent);
                break;
        }

    }


    /**
     * 获取家和公司地址 api 失败
     *
     * @param event
     *         返回数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestHomeAndCompanyFailed(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.getHomeAndCompany.equals(apiNo) || HttpApiKey.dealer.equals(apiNo)
        ))
            return;
        switch (apiNo){
            case HttpApiKey.getHomeAndCompany:
                CarControlManager.getInstance().setCompany(null);
                CarControlManager.getInstance().setHome(null);
                break;
            case HttpApiKey.dealer:
                ToastUtil.show(getActivity(),"经销商获取失败");
                break;
        }

    }

    @Override
    public void onStop() {
        super.onStop();


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
    public void deleteCarFailtrueHistory( DeleteFailHistotyEvent event){
        mBinding.rlControlWaite.setVisibility(View.GONE);
        mBinding.rlControlSuccess.setVisibility(View.GONE);
        showControlCarHistory();
    }


    /**
     * 获取车辆位置信息
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCarPosition(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.conditionReportPosition.equals(apiNo)
        ))
            return;
        ConditionReportPositionModel model = GsonImplHelp.get().toObject(httpResult.getResult(), ConditionReportPositionModel.class);
        if (null == model || null == model.getData()) {
            return;
        }
        ConditionReportPositionModel.DataBean dataBean = model.getData();
        if(dataBean != null && dataBean.getLatitude() != 0.0 && dataBean.getLongitude() != 0.0){
            if(!"nullVin".equals(CacheHelper.getVin())){
                if (null == carMarker) {
                    carMarker = mAmap.addMarker(
                            new MarkerOptions().icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.navi_car_location)).position(
                                    new LatLng(dataBean.getLatitude(), dataBean.getLongitude())));
                } else {
                    carMarker.setPosition(new LatLng(dataBean.getLatitude(), dataBean.getLongitude()));
                }
                LatLngBounds bounds = AMapUtil.createBounds(Double.parseDouble(CacheHelper.getLatitude()), Double.parseDouble(CacheHelper.getLongitude()), dataBean.getLatitude(), dataBean.getLongitude());
                mAmap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));
            }
        }else{
            if(carMarker != null){
                carMarker.remove();
                carMarker = null;
            }
            setMapCenterPhone();
        }


    }

    /**
     * Collections api请求成功
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCollectionsSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.postPoi.equals(apiNo)
        ))
            return;
        LoadingDialogUtils.dissmiss();
        ToolsHelper.showInfo(getActivity(), "下发成功");
    }

    /**
     * Collections api请求失败
     *
     * @param event
     *         失败返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CollectionsFailed(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.postPoi.equals(apiNo)
        ))
            return;
        LoadingDialogUtils.dissmiss();
        ToolsHelper.showHttpRequestErrorMsg(getActivity(), httpResult);
    }

    public void setMapStyle(){
        //写入到本地目录中
        mAmap.setCustomMapStylePath(Environment.getExternalStorageDirectory()
                + File.separator+"/qq.data");
        mAmap.setMapCustomEnable(true);//true 开启; false 关闭
    }
//
//    public void updateCarLocation(){
//        CarControlManager.getInstance().conditionReportPosition();
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCarInfo(UpdateCarInfo event) {
        showControlCarHistory();
        CarControlManager.getInstance().conditionReportPosition();

    }

    /**
     * 设置车的位置到地图中心
     */
    public void locationCar() {
        if(!"nullVin".equals(CacheHelper.getVin())){
            if (null != carMarker) {
                mAmap.animateCamera(CameraUpdateFactory.newLatLng(carMarker.getPosition()));
            }else{
                cn.lds.widget.ToastUtil.showToast(getActivity(),"车辆位置获取不到");
            }
        }else{
        }
    }
}
