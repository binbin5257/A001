package cn.lds.amap;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;

import cn.lds.R;
import cn.lds.databinding.FragmentMapBinding;
import cn.lds.ui.CollectionsActivity;
import cn.lds.ui.MapSearchActivity;


public class MapFragment extends Fragment implements LocationSource, AMapLocationListener, View.OnClickListener {

    FragmentMapBinding mBinding;
    private com.amap.api.maps.MapView mapView;
    private AMap mAMap;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private OnLocationChangedListener mListener;
    public AMapLocationClientOption mLocationOption = null;


    public static Fragment newInstance() {
        MapFragment fragment = null;
        synchronized (MapFragment.class) {
            fragment = new MapFragment();
        }
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_map, null, false);
        mapView = mBinding.mapMap;
        mapView.onCreate(savedInstanceState);
        mAMap = mapView.getMap();
        init();
        return mBinding.getRoot();
    }


    /**
     * 初始化AMap对象
     */
    private void init() {
        if (null != mAMap) {
            mAMap.setLocationSource(this);// 设置定位监听
            mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
            mAMap.getUiSettings().setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
            mAMap.getUiSettings().setScaleControlsEnabled(true);
            mAMap.setMyLocationEnabled(true);// 是否可触发定位并显示定位层
        }
        mBinding.collectBtn.setOnClickListener(this);
        mBinding.mapSearch.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onDestroy();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//            mLocationOption.setInterval(2000);
            mLocationOption.setInterval(10000);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);

            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_search:
                startActivity(new Intent(getActivity(), MapSearchActivity.class));
                break;
            case R.id.collect_btn:
                startActivity(new Intent(getActivity(), CollectionsActivity.class));
                break;
        }
    }
}
