package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.amap.util.TTSController;
import cn.lds.common.base.BaseActivity;
import cn.lds.databinding.ActvitityNaviBinding;
import cn.lds.widget.dialog.LoadingDialogUtils;

/**
 * 实时导航页面
 * Created by sibinbin on 18-3-15.
 */

public class NaviActivity extends BaseActivity implements View.OnClickListener, LocationSource, AMapNaviViewListener, AMapNaviListener {

    private ActvitityNaviBinding mBinding;
    private AMapNavi mAMapNavi;
    private boolean AMapNaviInitSuccess = false;
    private List<NaviLatLng> sList = new ArrayList<>();
    private List<NaviLatLng> eList = new ArrayList<>();
    /**
     * 途径点坐标集合
     */
    private List<NaviLatLng> wayList = new ArrayList<NaviLatLng>();
    private double distance;
    private TextView title;
    protected TTSController mTtsManager;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.actvitity_navi);
        initView();
        getIntentData();
        initMap(savedInstanceState);
        initListener();

    }

    public void getIntentData(){
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(!TextUtils.isEmpty(extras.getString("FLAG"))){
                title.setText("经销商导航");
            }else{
                title.setText("寻车导航");
            }
            NaviLatLng startNavi = extras.getParcelable("startNavi");
            NaviLatLng endNavi = extras.getParcelable("endNavi");
            distance = extras.getDouble("distance");
            sList.add(startNavi);
            eList.add(endNavi);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.naviView.onResume();
//        mAMapNavi.startNavi(NaviType.GPS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.naviView.onPause();
        mAMapNavi.stopNavi();
        mTtsManager.stopSpeaking();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.naviView.onDestroy();
        //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
        mAMapNavi.stopNavi();
        mAMapNavi.destroy();
//        mTtsManager.destroy();

    }

    @Override
    public void initView() {
        title = mBinding.getRoot().findViewById(R.id.top_title_tv);


    }

    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);

    }

    /**
     * 初始化地图
     * @param savedInstanceState
     */
    private void initMap( Bundle savedInstanceState ) {
        //实例化语音引擎
        mTtsManager = TTSController.getInstance(getApplicationContext());
//        mTtsManager.init();

        //
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.addAMapNaviListener(mTtsManager);
        mAMapNavi.setUseInnerVoice(true);

        //设置模拟导航的行车速度
        mAMapNavi.setEmulatorNaviSpeed(75);
//        sList.add(mStartLatlng);
//        eList.add(mEndLatlng);
        mBinding.naviView.onCreate(savedInstanceState);
        mBinding.naviView.setAMapNaviViewListener(this);

        AMapNaviViewOptions options = new AMapNaviViewOptions();
        options.setTilt(0);
        options.setCustomMapStylePath(Environment.getExternalStorageDirectory()
                + File.separator+"/qq.data");
        mBinding.naviView.setViewOptions(options);

    }

    public void setMapStyle(){
        AMap map = mBinding.naviView.getMap();
        //写入到本地目录中
        map.setCustomMapStylePath(Environment.getExternalStorageDirectory()
                + File.separator+"/qq.data");
        map.setMapCustomEnable(true);//true 开启; false 关闭
    }

    private void initMyLocation() {

    }


    @Override
    public void onClick( View v ) {
        int id = v.getId();
        switch (id) {
            case R.id.top_back_iv:
                finish();
                break;
        }
    }

    @Override
    public void activate( OnLocationChangedListener onLocationChangedListener ) {

    }


    @Override
    public void onInitNaviSuccess() {
        AMapNaviInitSuccess = true;
        LoadingDialogUtils.showVertical(this,"请稍后...");
        if(distance > 2000){
            int strategy=0;
            try {
                strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mAMapNavi.calculateDriveRoute(sList, eList, wayList, strategy);
        }else{
            mAMapNavi.calculateWalkRoute(sList.get(0),eList.get(0));
        }

    }

    @Override
    public void onCalculateRouteSuccess( int[] ints ) {
        LoadingDialogUtils.dissmiss();
        mAMapNavi.startNavi(NaviType.GPS);

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {
        finish();
    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void onNaviMapMode( int i ) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap( boolean b ) {

    }

    @Override
    public void onNaviViewLoaded() {

    }

    @Override
    public void onMapTypeChanged( int i ) {

    }

    @Override
    public void onNaviViewShowMode( int i ) {

    }

    @Override
    public void onInitNaviFailure() {

    }


    @Override
    public void onStartNavi( int i ) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange( AMapNaviLocation aMapNaviLocation ) {

    }

    @Override
    public void onGetNavigationText( int i, String s ) {

    }

    @Override
    public void onGetNavigationText( String s ) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteFailure( int i ) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint( int i ) {

    }

    @Override
    public void onGpsOpenStatus( boolean b ) {

    }

    @Override
    public void onNaviInfoUpdate( NaviInfo naviInfo ) {

    }

    @Override
    public void onNaviInfoUpdated( AMapNaviInfo aMapNaviInfo ) {

    }

    @Override
    public void updateCameraInfo( AMapNaviCameraInfo[] aMapNaviCameraInfos ) {

    }

    @Override
    public void updateIntervalCameraInfo( AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i ) {

    }

    @Override
    public void onServiceAreaUpdate( AMapServiceAreaInfo[] aMapServiceAreaInfos ) {

    }

    @Override
    public void showCross( AMapNaviCross aMapNaviCross ) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showModeCross( AMapModelCross aMapModelCross ) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo( AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1 ) {

    }

    @Override
    public void showLaneInfo( AMapLaneInfo aMapLaneInfo ) {

    }

    @Override
    public void hideLaneInfo() {

    }


    @Override
    public void notifyParallelRoad( int i ) {

    }

    @Override
    public void OnUpdateTrafficFacility( AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo ) {

    }

    @Override
    public void OnUpdateTrafficFacility( AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos ) {

    }

    @Override
    public void OnUpdateTrafficFacility( TrafficFacilityInfo trafficFacilityInfo ) {

    }

    @Override
    public void updateAimlessModeStatistics( AimLessModeStat aimLessModeStat ) {

    }

    @Override
    public void updateAimlessModeCongestionInfo( AimLessModeCongestionInfo aimLessModeCongestionInfo ) {

    }

    @Override
    public void onPlayRing( int i ) {

    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        
    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onNaviRouteNotify( AMapNaviRouteNotifyData aMapNaviRouteNotifyData ) {

    }
}
