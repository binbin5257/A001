package cn.lds.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.amap.util.AMapUtil;
import cn.lds.amap.util.ToastUtil;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.data.CollectionStateModel;
import cn.lds.common.data.CollectionsModel;
import cn.lds.common.data.ConditionReportModel;
import cn.lds.common.data.ConditionReportPositionModel;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.CarControlManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityCarLocationBinding;
import cn.lds.widget.dialog.LoadingDialogUtils;


/**
 * 车辆位置
 * Created by sibinbin on 18-3-14.
 */

public class CarLocationActivity extends BaseActivity implements View.OnClickListener, LocationSource, GeocodeSearch.OnGeocodeSearchListener, AMapLocationListener, RouteSearch.OnRouteSearchListener {

    private ActivityCarLocationBinding mBinding;
    private List<NaviLatLng> startList = new ArrayList<>();
    private List<NaviLatLng> endList = new ArrayList<>();
    private AMap amap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation mMyLocationPoint;
    private Marker carMarker;
    private RouteSearch routeSearch;
    private double distance = 0;
    private String collectionId;
    private PoiItem poiItem;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        initView(savedInstanceState);
        initListener();
        initMap();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.mapMap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.mapMap.onPause();
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
    protected void onDestroy() {
        super.onDestroy();
        mBinding.mapMap.onDestroy();
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        super.onSaveInstanceState(outState);
        mBinding.mapMap.onSaveInstanceState(outState);
    }

    public void initView( Bundle savedInstanceState ) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_car_location);
        mBinding.mapMap.onCreate(savedInstanceState);
        TextView topTitle = mBinding.getRoot().findViewById(R.id.top_title_tv);
        topTitle.setText("车辆位置");
        ConditionReportModel.DataBean c = CarControlManager.getInstance().getCarDetail();
        if(c != null){
            endList.clear();
            startList.add(new NaviLatLng(c.getLatitude(),c.getLongitude()));
            endList.add(new NaviLatLng(Double.parseDouble(CacheHelper.getLatitude()),Double.parseDouble(CacheHelper.getLongitude())));
        }else{
            ToastUtil.show(mContext,"未发现车辆位置");
        }

    }

    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        mBinding.startNavi.setOnClickListener(this);
        mBinding.poiLocatedCollectLlyt.setOnClickListener(this);
    }

    @Override
    public void onClick( View v ) {
        int id = v.getId();
        switch (id) {
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.start_navi:
                enterNaviActivity();
                break;
            case R.id.poi_located_collect_llyt:
                toggleCollect();
                break;
        }
    }

    private void enterNaviActivity() {
        if(mMyLocationPoint == null || distance == 0){
            ToastUtil.show(this,"尚未定位成功");
            return;
        }
        NaviLatLng s = new NaviLatLng(mMyLocationPoint.getLatitude(),mMyLocationPoint.getLongitude());
        NaviLatLng e = new NaviLatLng(carMarker.getPosition().latitude,carMarker.getPosition().longitude);
        Intent intent = new Intent(this,NaviActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("startNavi",s);
        bundle.putParcelable("endNavi",e);
        bundle.putDouble("distance",distance);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        if(amap == null){
            amap = mBinding.mapMap.getMap();
            setMapStyle();
            //设置显示定位按钮 并且可以点击
            UiSettings settings = amap.getUiSettings();
            amap.setLocationSource(this);//设置了定位的监听,这里要实现LocationSource接口
            // 是否显示定位按钮
            settings.setMyLocationButtonEnabled(true);
            amap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            // 自定义定位蓝点图标
            myLocationStyle.myLocationIcon(
                    BitmapDescriptorFactory.fromResource(R.drawable.navi_me_location));
            myLocationStyle.strokeColor(getResources().getColor(R.color.map_stroke_color));//设置定位蓝点精度圆圈的边框颜色的方法。
            myLocationStyle.radiusFillColor(getResources().getColor(R.color.map_stroke_color));//设置定位蓝点精度圆圈的填充颜色的方法。
            amap.setMyLocationStyle(myLocationStyle);
            amap.moveCamera(CameraUpdateFactory.zoomTo(14));
            if (null == carMarker) {
                ConditionReportModel.DataBean c = CarControlManager.getInstance().getCarDetail();
                if (null != c) {
                    carMarker = amap.addMarker(
                             new MarkerOptions().icon(
                                     BitmapDescriptorFactory.fromResource(R.drawable.navi_car_location)).position(
                                     new LatLng(c.getLatitude(), c.getLongitude())));
                    amap.animateCamera(CameraUpdateFactory.newLatLng(carMarker.getPosition()));
                    amap.moveCamera(CameraUpdateFactory.zoomTo(14));

                }
            }

            GeocodeSearch geocodeSearch = new GeocodeSearch(this);
            if(geocodeSearch != null && carMarker != null){
                geocodeSearch.setOnGeocodeSearchListener(this);
                RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(carMarker.getPosition().latitude, carMarker.getPosition().longitude), 3000,GeocodeSearch.AMAP);
                geocodeSearch.getFromLocationAsyn(query);
            }


            routeSearch = new RouteSearch(this);
            routeSearch.setRouteSearchListener(this);
        }
    }
    public void setMapStyle(){
        //写入到本地目录中
        amap.setCustomMapStylePath(Environment.getExternalStorageDirectory()
                + File.separator+"/qq.data");
        amap.setMapCustomEnable(true);//true 开启; false 关闭
    }

    /**
     * 步行路线规划
     */
    private void walkRouteQuery(LatLonPoint startPoint,LatLonPoint endPoint){
        try {
            RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                    startPoint, endPoint);
            RouteSearch.WalkRouteQuery walkRouteQuery = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WALK_DEFAULT);
            routeSearch.calculateWalkRoute(walkRouteQuery);
        } catch (AMapException e) {
            e.printStackTrace();
        }

    }
    /**
     * 驾车路线规划
     */
    private void driveRouteQuery(LatLonPoint startPoint,LatLonPoint endPoint){
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        RouteSearch.DriveRouteQuery driveRouteQuery = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT, null,
                null, "");
        routeSearch.calculateDriveRouteAsyn(driveRouteQuery);
    }

    @Override
    public void onRegeocodeSearched( RegeocodeResult regeocodeResult, int i ) {
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        if(regeocodeAddress.getStreetNumber() != null && !TextUtils.isEmpty(regeocodeAddress.getStreetNumber().getStreet())){
            mBinding.poiName.setText(regeocodeAddress.getStreetNumber().getStreet());
        }else{
            mBinding.poiName.setText(regeocodeAddress.getTownship());
        }
        mBinding.poiAddress.setText(regeocodeAddress.getFormatAddress());
        List<PoiItem> pois = regeocodeAddress.getPois();
        if(pois != null && pois.size() > 0){
            poiItem = pois.get(0);
            //poi id为收藏id
            collectionId = poiItem.getPoiId();
            if(!TextUtils.isEmpty(collectionId)){
                //判断是否已经收藏
                LoadingDialogUtils.showVertical(mContext,getResources().getString(R.string.loading_waitting));
                CarControlManager.getInstance().findCollectionByCollectionId(collectionId);
            }
        }

    }

    @Override
    public void onGeocodeSearched( GeocodeResult geocodeResult, int i ) {


    }
    /**
     * 计算路程
     * @param allLength
     * @return
     */
    private String getLength(float allLength) {
        if(allLength > 1000){
            float a = allLength /1000;
            return new DecimalFormat("#.0").format(a) + "km";
        }else{
            return allLength+"m";
        }
    }
    /**
     * 定位地点
     * @param amapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null&&amapLocation != null) {
            if (amapLocation != null
                    &&amapLocation.getErrorCode() == 0) {
                if (null == mMyLocationPoint) {//第一次定位
                    amap.animateCamera(CameraUpdateFactory.newLatLng(carMarker.getPosition()));
                    mListener.onLocationChanged(mMyLocationPoint);
                    CacheHelper.setLatitude(amapLocation.getLatitude());
                    CacheHelper.setLongitude(amapLocation.getLongitude());
                    CacheHelper.setCity(amapLocation.getCity());
                    CarControlManager.getInstance().conditionReportPosition();//30s 获取一次车的位置
                    distance = AMapUtil.DistanceOfTwoPoints(carMarker.getPosition().latitude, carMarker.getPosition().longitude, amapLocation.getLatitude(), amapLocation.getLongitude());
                    LatLonPoint startLatLonPoint = new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude());
                    LatLonPoint endLatLonPoint = new LatLonPoint(carMarker.getPosition().latitude, carMarker.getPosition().longitude);
                    if(distance > 2000){
                        driveRouteQuery(startLatLonPoint,endLatLonPoint);
                    }else{
                        walkRouteQuery(startLatLonPoint,endLatLonPoint);
                    }

                }
                mMyLocationPoint = amapLocation;
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }
    /**
     * 后台请求
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCarPosition(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.conditionReportPosition.equals(apiNo)
                || HttpApiKey.findCollectionByCollectionId.equals(apiNo)
                || HttpApiKey.deleteCollections.equals(apiNo)
                || HttpApiKey.addCollections.equals(apiNo)
        )) {
            return;
        }
        LoadingDialogUtils.dissmiss();
        if (HttpApiKey.conditionReportPosition.equals(apiNo)) {
            ConditionReportPositionModel model = GsonImplHelp.get().toObject(httpResult.getResult(), ConditionReportPositionModel.class);
            if (null == model || null == model.getData()) {
                return;
            }
            ConditionReportPositionModel.DataBean dataBean = model.getData();
            if (null == carMarker) {
                carMarker = amap.addMarker(
                        new MarkerOptions().icon(
                                BitmapDescriptorFactory.fromResource(R.drawable.navi_car_location)).position(
                                new LatLng(dataBean.getLatitude(), dataBean.getLongitude())));
            } else {
                carMarker.setPosition(new LatLng(dataBean.getLatitude(), dataBean.getLongitude()));
            }
        } else if (HttpApiKey.findCollectionByCollectionId.equals(apiNo)) {
            CollectionStateModel collectionStateModel = GsonImplHelp.get().toObject(httpResult.getResult().toString(), CollectionStateModel.class);
            updateCollectStatus(collectionStateModel.getData().isExist());
        } else if (HttpApiKey.addCollections.equals(apiNo)) {
            mBinding.mapSearchCollect.performClick();
            mBinding.mapSearchCollectText.setText("收藏");
            mBinding.mapSearchCollectText.setTextColor(getResources().getColor(R.color.white));
            mBinding.mapSearchCollect.setChecked(true);
        } else if (HttpApiKey.deleteCollections.equals(apiNo)) {
            mBinding.mapSearchCollectText.setText("收藏");
            mBinding.mapSearchCollectText.setTextColor(getResources().getColor(R.color.white));
            mBinding.mapSearchCollect.performClick();
            mBinding.mapSearchCollect.setChecked(false);

        }
    }
    private void toggleCollect() {

        if (mBinding.mapSearchCollect.isChecked()) {
            //取消收藏
            LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
            CarControlManager.getInstance().deleCollection(poiItem.getPoiId());
        } else {
            if(poiItem == null){
                ToastUtil.show(mContext,"当前位置无法收藏");
                return;
            }
            //增加收藏
            LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
            CollectionsModel.DataBean dataBean2 = new CollectionsModel.DataBean();
//                            dataBean.setTypeCode(poiItem.getTypeCode());
            dataBean2.setTel(poiItem.getTel());
            dataBean2.setName(poiItem.getTitle());
            dataBean2.setLongitude(poiItem.getLatLonPoint().getLongitude());
            dataBean2.setLatitude(poiItem.getLatLonPoint().getLatitude());
            dataBean2.setDesc(poiItem.getTypeDes());
            dataBean2.setCollectId(poiItem.getPoiId());
            dataBean2.setAddress(poiItem.getSnippet());
            CarControlManager.getInstance().addCollection(dataBean2, null);
        }
    }
    /**
     * 更新收藏状态
     * @param isCollected
     */
    private void updateCollectStatus(boolean isCollected) {
        if (!isCollected) {//待收藏
            mBinding.mapSearchCollect.setChecked(false);
            mBinding.mapSearchCollectText.setText("收藏");
//            mBinding.mapSearchCollectText.setTextColor(Color.parseColor("#1affffff"));
        } else {//已收藏
            mBinding.mapSearchCollect.setChecked(true);
            mBinding.mapSearchCollectText.setText("收藏");
            mBinding.mapSearchCollectText.setTextColor(getResources().getColor(R.color.white));

        }
    }

    @Override
    public void activate( OnLocationChangedListener listener ) {
        mListener = listener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocation(true);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 设置定位间隔,单位毫秒,默认为30s
//            mLocationOption.setInterval(30000);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void onPointerCaptureChanged( boolean hasCapture ) {

    }
    @Override
    public void deactivate() {

    }


    @Override
    public void onBusRouteSearched( BusRouteResult busRouteResult, int i ) {

    }

    @Override
    public void onDriveRouteSearched( DriveRouteResult driveRouteResult, int code ) {
        if (code == 1000) {
            if (driveRouteResult != null && driveRouteResult.getPaths() != null) {
                if (driveRouteResult.getPaths().size() > 0) {
                    DrivePath drivePath = driveRouteResult.getPaths().get(0);
                    float tollDistance = drivePath.getTollDistance();
                    mBinding.poiKm.setText("距您"+getLength(tollDistance));
                }

            }
        }
    }

    @Override
    public void onWalkRouteSearched( WalkRouteResult walkRouteResult, int code ) {
        if (code == 1000) {
            if (walkRouteResult != null && walkRouteResult.getPaths() != null) {
                if (walkRouteResult.getPaths().size() > 0) {
                    WalkPath walkPath = walkRouteResult.getPaths().get(0);
                    float distance = walkPath.getDistance();
                    mBinding.poiKm.setText(getLength(distance));
                }

            }
        }
    }

    @Override
    public void onRideRouteSearched( RideRouteResult rideRouteResult, int i ) {

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
        if (!(
                HttpApiKey.deleteCollections.equals(apiNo) ||
                        HttpApiKey.addCollections.equals(apiNo))
                )
            return;
        LoadingDialogUtils.dissmiss();
        ToolsHelper.showHttpRequestErrorMsg(mContext, httpResult);
    }
}
