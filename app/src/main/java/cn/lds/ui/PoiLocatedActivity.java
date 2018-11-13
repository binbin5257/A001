package cn.lds.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import cn.lds.R;
import cn.lds.amap.util.AMapUtil;
import cn.lds.amap.util.ToastUtil;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.data.CollectionStateModel;
import cn.lds.common.data.CollectionsModel;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.CarControlManager;
import cn.lds.common.manager.UMengManager;
import cn.lds.common.table.PoiIdTable;
import cn.lds.common.table.SearchPoiTitleTable;
import cn.lds.common.table.base.DBManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.LogHelper;
import cn.lds.common.utils.OnItemClickListener;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityPoiLocatedBinding;
import cn.lds.ui.adapter.MorePoiGridAdapter;
import cn.lds.ui.adapter.PoiLocatedAdapter;
import cn.lds.widget.dialog.LoadingDialogUtils;
import io.realm.Realm;

/**
 * poi 搜索定位界面
 */
public class PoiLocatedActivity extends BaseActivity implements View.OnClickListener, LocationSource, AMapLocationListener {

    private ActivityPoiLocatedBinding mBinding;

    //更多选项描述词
    private static final String[] moreList = {
            "加油站", "充电桩", "地铁站",
            "网吧", "酒吧", "KTV",
            "电影院", "餐饮服务", "快餐",
            "中餐", "火锅", "住宿",
            "星级酒店", "快捷酒店", "宾馆",
            "游乐园", "公园", "博物馆",
            "超市", "商场", "医院",
            "银行"};

    public static final String ACTIONSINGLEPOI = "1";//单个poi定位
    public static final String ACTIONPOILIST = "2";//poilist检索
    public static final String ACTIONMORE = "3";//显示更多
    private String action;
    private AMap mAmap;
    // 声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    // 声明定位回调监听器
    public AMapLocationListener mLocationListener;
    private AMapLocation mMyLocationPoint;
    // 我的位置监听器
    private OnLocationChangedListener mLocationChangeListener = null;

    private ArrayList<PoiItem> poiItems;//poiitems数据
    private PoiLocatedAdapter poiLocatedAdapter;//poi列表适配器
    private Marker singlePoiMarker, lastCilckMark;
    private PoiItem poiItem;//单个poi信息
    private String keyword;
    private Realm realm;
    //private Realm realm;//数据库实例

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_poi_located);
        action = getIntent().getAction();
        poiItems = getIntent().getParcelableArrayListExtra("list");
        mBinding.mapview.onCreate(savedInstanceState);
        initView();
        initListener();

    }

    @Override
    public void initView() {
        keyword = getIntent().getStringExtra("keyWord");
        initMap();
//        realm = DBManager.getInstance().getRealm();
        switch (action) {
            case ACTIONSINGLEPOI:
                poiItem = getIntent().getParcelableExtra("poiItem");
//                updateSinglePoiUi(poiItem, keyword);
                //请求服务器判断该poi是否已经收藏
                currentPoiItemIsCollected();
                break;
            case ACTIONPOILIST:
                mBinding.mapview.setVisibility(View.VISIBLE);
                mBinding.poiList.setVisibility(View.VISIBLE);
                mBinding.singlePoiLlyt.setVisibility(View.GONE);
                mBinding.moreList.setVisibility(View.GONE);
                mBinding.mapSearchEdit.setText(ToolsHelper.toString(keyword));


                poiLocatedAdapter = new PoiLocatedAdapter(poiItems, mContext, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object data, int position) {
                        poiItem = (PoiItem) data;
//                        updateSinglePoiUi(poiItem, poiItem.getTitle());
                        currentPoiItemIsCollected();
                        if (mAmap != null) {

                            mAmap.moveCamera(CameraUpdateFactory.zoomTo(14));
                        }
                    }
                });
                mBinding.poiList.setAdapter(poiLocatedAdapter);
                //地图增加marks
                for (int i = 0; i < poiItems.size(); i++) {
                    PoiItem poiItem1 = poiItems.get(i);
                    LatLonPoint c = poiItem1.getLatLonPoint();
                    if (null != c) {
                        drawaRedDots(poiItem1, c);
                    }
                }
                mBinding.poiList.setLayoutManager(new LinearLayoutManager(this){
                    @Override
                    public void onMeasure( RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec ) {
                        if(getChildCount() > 0){
                            View firstChildView = recycler.getViewForPosition(0);
                            measureChild(firstChildView, widthSpec, heightSpec);
                            if(poiItems.size() > 4){
                                setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), firstChildView.getMeasuredHeight() * 4);
                            }else{
                                setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), firstChildView.getMeasuredHeight() * poiItems.size());
                            }
                        }else{
                            if(poiItems.size() > 4){
                                setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), ToolsHelper.dpToPx(77) * 4);
                            }else if(poiItems.size() < 4 && poiItems.size() > 0){
                                setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), ToolsHelper.dpToPx(77) * poiItems.size());
                            }else{
                                super.onMeasure(recycler, state, widthSpec, heightSpec);
                            }

                        }


                    }
                });
                mAmap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                         for (int i = 0; i < poiItems.size(); i++) {
                            PoiItem p = poiItems.get(i);
                            if (marker.getObject().toString().equals(p.getPoiId())) {
                                poiItem = p;
//                                updateSinglePoiUi(p, p.getTitle());
                                currentPoiItemIsCollected();
                                marker.setVisible(true);
                                lastCilckMark = marker;
                            }
                        }
                        return true;
                    }
                });

                break;
            case ACTIONMORE:
                mBinding.mapview.setVisibility(View.GONE);
                mBinding.poiList.setVisibility(View.GONE);
                mBinding.singlePoiLlyt.setVisibility(View.GONE);
                mBinding.moreList.setVisibility(View.VISIBLE);


                //创建一个GridLayout管理器,设置为3列
                GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
                //设置GridView方向为:垂直方向
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                //添加到RecyclerView容器里面
                mBinding.moreList.setLayoutManager(layoutManager);
                //设置自动适应配置的大小
                mBinding.moreList.setHasFixedSize(true);
                //创建适配器对象
                MorePoiGridAdapter adapter = new MorePoiGridAdapter(Arrays.asList(moreList), mContext, moreListItemClick);
                mBinding.moreList.setAdapter(adapter);
                break;
        }
    }

    /**
     * 在地图上绘制红色marker
     * @param poiItem1
     * @param c
     */
    private void drawaRedDots( PoiItem poiItem1, LatLonPoint c ) {
        Marker marker = mAmap.addMarker(
                new MarkerOptions().icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.navi_poiitem_icon)).position(
                        new LatLng(c.getLatitude(), c.getLongitude())));
        marker.setObject(poiItem1.getPoiId());//点击mark 用来做标记，快速找到对应的poitem对象
    }

    //请求服务器判断该poi是否已经收藏
    private void currentPoiItemIsCollected() {
        CarControlManager.getInstance().findCollectionByCollectionId(poiItem.getPoiId());
    }

    /**
     * 更新当个位置点
     */
    private void updateSinglePoiUi(final PoiItem poiItem, String keyword,boolean isCollected) {
        mBinding.mapSearchEdit.setText(ToolsHelper.toString(keyword));
        mBinding.mapview.setVisibility(View.VISIBLE);
        mBinding.singlePoiLlyt.setVisibility(View.VISIBLE);
        mBinding.poiList.setVisibility(View.GONE);
        mBinding.moreList.setVisibility(View.GONE);
        if (null != poiItem) {
            mBinding.poiName.setText(ToolsHelper.toString(poiItem.getTitle()));
            double distance = AMapUtil.DistanceOfTwoPoints(Double.parseDouble(CacheHelper.getLatitude()), Double.parseDouble(CacheHelper.getLongitude()), poiItem.getLatLonPoint().getLatitude(),poiItem.getLatLonPoint().getLongitude());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("距您");
            if (distance >= 1000) {
                stringBuilder.append(distance / 1000).append("km");
            } else {
                stringBuilder.append(distance).append("m");
            }
            mBinding.poiKm.setText(stringBuilder.toString());
            mBinding.poiAddress.setText(ToolsHelper.toString(poiItem.getSnippet()));
            if (null == singlePoiMarker) {
                LatLonPoint c = poiItem.getLatLonPoint();
                if (null != c) {
                    singlePoiMarker = mAmap.addMarker(
                            new MarkerOptions().icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.navi_poi_location)).position(
                                    new LatLng(c.getLatitude(), c.getLongitude())));
//                    LatLngBounds bounds = AMapUtil.createBounds(Double.parseDouble(CacheHelper.getLatitude()), Double.parseDouble(CacheHelper.getLongitude()), c.getLatitude(),c.getLongitude());
//                    mAmap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 14),200L,null);

                }
            } else {
                if (null != lastCilckMark) {
                    lastCilckMark.setVisible(true);
                }
                LatLng c = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
                singlePoiMarker.setPosition(c);
            }

            if (!isCollected) {//待收藏
                mBinding.mapSearchCollect.setChecked(false);
                mBinding.mapSearchCollectText.setText("收藏");
                mBinding.mapSearchCollectText.setTextColor(getResources().getColor(R.color.white));
//                mBinding.mapSearchCollectText.setTextColor(Color.parseColor("#1affffff"));
            } else {//已收藏
                mBinding.mapSearchCollect.setChecked(true);
                mBinding.mapSearchCollectText.setText("收藏");
                mBinding.mapSearchCollectText.setTextColor(getResources().getColor(R.color.white));

            }

            if (null == realm) {
                realm = DBManager.getInstance().getRealm();
            }
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm1) {
                    //检索纪录保存
                    SearchPoiTitleTable titleTable = new SearchPoiTitleTable(poiItem.getTitle(), poiItem.getSnippet(), System.currentTimeMillis(),CacheHelper.getLoginId());
                    realm1.copyToRealmOrUpdate(titleTable);
                }
            });
        }


    }

    /**
     * 初始化地图
     */
    private void initMap() {
        if (mAmap == null) {
            mAmap = mBinding.mapview.getMap();
            initMyLocation();
        }
//        mAmap.setLocationSource(this);// 设置定位监听
        mAmap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAmap.getUiSettings().setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        mAmap.getUiSettings().setScaleControlsEnabled(true);
        mAmap.setMyLocationEnabled(true);// 是否可触发定位并显示定位层

        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(
                BitmapDescriptorFactory.fromResource(R.drawable.navi_me_location));
        myLocationStyle.strokeColor(getResources().getColor(R.color.map_stroke_color));//设置定位蓝点精度圆圈的边框颜色的方法。
        myLocationStyle.radiusFillColor(getResources().getColor(R.color.map_stroke_color));//设置定位蓝点精度圆圈的填充颜色的方法。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        mAmap.setMyLocationStyle(myLocationStyle);


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
        setMapStyle();
        LogHelper.d(
                "max = " + mAmap.getMaxZoomLevel() + "min = "
                        + mAmap.getMinZoomLevel());
    }

    public void setMapStyle(){
        //写入到本地目录中
        mAmap.setCustomMapStylePath(Environment.getExternalStorageDirectory()
                + File.separator+"/qq.data");
        mAmap.setMapCustomEnable(true);//true 开启; false 关闭
    }

    private OnItemClickListener moreListItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(Object data, int position) {
            UMengManager.getInstance().onClick("POIMoreLabel");
            Intent intent = new Intent();
            intent.putExtra("keyWord", data.toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    @Override
    public void initListener() {
        mBinding.topBackIv.setOnClickListener(this);
        mBinding.poiInputRllt.setOnClickListener(this);
        mBinding.poiLocatedCollectLlyt.setOnClickListener(this);
        mBinding.poiLocatedPostPoi.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.poi_input_rllt:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.poi_located_post_poi:
                //下发poi
                LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
                CollectionsModel.DataBean dataBean = new CollectionsModel.DataBean();
                dataBean.setTypeCode(poiItem.getTypeCode());
                dataBean.setTel(poiItem.getTel());
                dataBean.setName(poiItem.getTitle());
                dataBean.setLongitude(poiItem.getLatLonPoint().getLongitude());
                dataBean.setLatitude(poiItem.getLatLonPoint().getLatitude());
                dataBean.setDesc(poiItem.getTypeDes());
                dataBean.setCollectId(poiItem.getPoiId());
                dataBean.setAddress(poiItem.getSnippet());
                CarControlManager.getInstance().postPoi(dataBean);
                break;
            case R.id.poi_located_collect_llyt:
                toggleCollect();
                break;
        }
    }

    private void toggleCollect() {
        LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
        if (mBinding.mapSearchCollect.isChecked()) {
            //取消收藏
            CarControlManager.getInstance().deleCollection(poiItem.getPoiId());
        } else {
            //增加收藏
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

    @Override
    public void onPause() {
        super.onPause();
        mBinding.mapview.onPause();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();//ֹͣ��λ
        }
        deactivate();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.mapview.onResume();
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
        if (mAmap != null) {

            mAmap.moveCamera(CameraUpdateFactory.zoomTo(14));
        }
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

    /**
     * 初始化定位服务
     */
    private void initLocation() {
        mLocationClient = new AMapLocationClient(mContext);
        mLocationClient.setLocationListener(this);
        // 初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        // 设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);

        // 设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        // 设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        // 设置定位间隔,单位毫秒,默认为30s
//        mLocationOption.setInterval(30000);
        // 给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                if (null == mMyLocationPoint) {//第一次定位
                    LatLng latLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                    mAmap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
                mAmap.moveCamera(CameraUpdateFactory.zoomTo(14));
                mMyLocationPoint = amapLocation;
                mLocationChangeListener.onLocationChanged(mMyLocationPoint);
                CacheHelper.setLatitude(amapLocation.getLatitude());
                CacheHelper.setLongitude(amapLocation.getLongitude());
                CacheHelper.setCity(amapLocation.getCity());
//                CarControlManager.getInstance().conditionReportPosition();//30s 获取一次车的位置
            } else {
                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                LogHelper.d(
                        "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
            }
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
     * Collections api请求成功
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCollectionsSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.deleteCollections.equals(apiNo)
                || HttpApiKey.postPoi.equals(apiNo)
                || HttpApiKey.findCollectionByCollectionId.equals(apiNo)
                || HttpApiKey.addCollections.equals(apiNo))
                )
            return;
        LoadingDialogUtils.dissmiss();
        if (HttpApiKey.postPoi.equals(apiNo)) {
            ToolsHelper.showInfo(mContext, "下发成功");
        } else if (HttpApiKey.addCollections.equals(apiNo)) {
//            PoiIdTable poiIdTable = new PoiIdTable(poiItem.getPoiId());
//            realm.copyToRealmOrUpdate(poiIdTable);
            mBinding.mapSearchCollect.performClick();
            mBinding.mapSearchCollectText.setText("收藏");
            mBinding.mapSearchCollectText.setTextColor(getResources().getColor(R.color.white));
            mBinding.mapSearchCollect.setChecked(true);
            ToastUtil.show(mContext,"收藏成功");
        } else if (HttpApiKey.deleteCollections.equals(apiNo)) {
//            PoiIdTable poiIdTable = realm.where(PoiIdTable.class).
//                    contains("poiId", poiItem.getPoiId()).findFirst();
//            if (null != poiIdTable) {
//                poiIdTable.deleteFromRealm();
//                mBinding.mapSearchCollect.performClick();
//                mBinding.mapSearchCollect.setChecked(false);
//            }
            mBinding.mapSearchCollectText.setText("收藏");
            mBinding.mapSearchCollectText.setTextColor(getResources().getColor(R.color.white));
            mBinding.mapSearchCollect.performClick();
            mBinding.mapSearchCollect.setChecked(false);
            ToastUtil.show(mContext,"取消收藏成功");
        } else if ( HttpApiKey.findCollectionByCollectionId.equals(apiNo)){
            CollectionStateModel collectionStateModel = GsonImplHelp.get().toObject(httpResult.getResult().toString(), CollectionStateModel.class);
            if(collectionStateModel != null && collectionStateModel.getData() != null ){
                switch (action) {
                    case ACTIONSINGLEPOI:
                        updateSinglePoiUi(poiItem, keyword,collectionStateModel.getData().isExist());
                        break;
                    case ACTIONPOILIST:
                    case ACTIONMORE:
                        updateSinglePoiUi(poiItem, poiItem.getTitle(),collectionStateModel.getData().isExist());
                        break;
                }

            }
        }

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
                        HttpApiKey.postPoi.equals(apiNo) ||
                        HttpApiKey.addCollections.equals(apiNo))
                )
            return;
        LoadingDialogUtils.dissmiss();
        ToolsHelper.showHttpRequestErrorMsg(mContext, httpResult);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (null != realm) {
//            realm.close();
//            realm = null;
//        }
    }
}
