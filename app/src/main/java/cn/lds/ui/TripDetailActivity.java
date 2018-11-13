package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cn.lds.R;
import cn.lds.amap.PointsUtil;
import cn.lds.amap.SmoothMarker;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.data.TripDetailModel;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityTripDetailBinding;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.LoadingDialogUtils;

/**
 * 行程记录详情，轨迹回放
 */
public class TripDetailActivity extends BaseActivity implements TraceListener, View.OnClickListener, SmoothMarker.SmoothMarkerMoveListener {
    ActivityTripDetailBinding mBinding;


    private AMap aMap;
    LBSTraceClient mTraceClient;
    private int mSequenceLineID = 1000;
    private List<TraceLocation> mTraceList = new ArrayList<>();
    private ConcurrentMap<Integer, TraceOverlay> mOverlayList = new ConcurrentHashMap<Integer, TraceOverlay>();
    private static String mDistanceString, mStopTimeString;
    private static final String DISTANCE_UNIT_DES = " KM";
    private static final String TIME_UNIT_DES = " 分钟";
    private List<TripDetailModel.DataBean> pointList;
    List<LatLng> points = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_trip_detail);

        String uuid = null;
        if (null != getIntent()) {
            uuid = getIntent().getStringExtra("uuid");
        }
        mBinding.map.onCreate(savedInstanceState);// 此方法必须重写
        mDistanceString = "总距离";
        mStopTimeString = "等    待";
        initView();
        initListener();
        initData(uuid);

    }

    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        mBinding.ivStart.setOnClickListener(this);
        mBinding.ivStop.setOnClickListener(this);
        mBinding.btnTripStart.setOnClickListener(this);
        mBinding.btnTripStop.setOnClickListener(this);
        mBinding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( RadioGroup group, int checkedId ) {
                if(points != null && !points.isEmpty()){
                    switch (checkedId){
                        case R.id.rb_half_speed:
                            mBinding.tvPrompt.setText("0.5倍速度播放");
                            smoothMarker.setTotalDuration(points.size() * 2000);
                            break;
                        case R.id.rb_normal_speed:
                            mBinding.tvPrompt.setText("正常速度播放");
                            smoothMarker.setTotalDuration(points.size() * 1000);
                            break;
                        case R.id.rb_two_speed:
                            mBinding.tvPrompt.setText("2倍速度播放");
                            smoothMarker.setTotalDuration(points.size() * 500);
                            break;
                    }
                }else{
                    ToastUtil.showToast(mContext,"未找到可播放轨迹");
                }

            }
        });
    }

    private void initData(CharSequence uuid) {
        LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
        String url = ModuleUrls.track.replace("{uuid}", uuid).replace("{vin}", CacheHelper.getVin());
        RequestManager.getInstance().get(url, HttpApiKey.track);
    }

    @Override
    public void initView() {
        TextView title = mBinding.getRoot().findViewById(R.id.top_title_tv);
        title.setText("行程轨迹");
        aMap = mBinding.map.getMap();
        aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        mBinding.radioGroup.check(R.id.rb_normal_speed);
        mBinding.ivStop.setImageResource(R.drawable.bg_reasume);
    }

    /**
     * 行程里表api请求成功
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestTripDetailSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.track.equals(apiNo)))
            return;
        LoadingDialogUtils.dissmiss();
        TripDetailModel model = GsonImplHelp.get().toObject(httpResult.getResult(), TripDetailModel.class);
        if (null == model || null == model.getData() || model.getData().isEmpty()) {
            ToolsHelper.showInfo(mContext, "经纬度点坐标不足，无法轨迹回放");
            return;
        }
        pointList = model.getData();

        for (int i = 0; i < pointList.size(); i++) {
            double lat = pointList.get(i).getLatitude();
            double lng = pointList.get(i).getLongitude();
            if (lat != 0 && lng != 0) {
                LatLng desLatLng = convert(new LatLng(lat, lng));
                points.add(desLatLng);
            }
        }
        writeTxtToFile(points.toString(), Environment.getExternalStorageDirectory().getAbsolutePath(), "/point.txt");
        if (points.size() >= 2) {

            initRoute();
        } else if (points.size() == 1) {
            addCarToMap(points.get(0));
        }
    }

    /**
     * 绘制起点和终点marker
     */
    private void drawaStartAndEnd() {
        aMap.addMarker(
                new MarkerOptions().icon(
                        BitmapDescriptorFactory
                                .fromResource(R.drawable.amap_start))
                        .position(points.get(0)));
        aMap.addMarker(
                new MarkerOptions().icon(
                        BitmapDescriptorFactory
                                .fromResource(R.drawable.amap_end))
                        .position(points.get(points.size() - 1)));
    }

    /**
     * 行程里表api请求失败
     *
     * @param event
     *         失败返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestTripDetailFailed(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.track.equals(apiNo)))
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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.iv_stop:
                if(isplay){
                    isplay = false;
                    mBinding.ivStop.setImageResource(R.drawable.bg_reasume);

                }else{
                    isplay = true;
                    mBinding.ivStop.setImageResource(R.drawable.bg_pause);

                }
                if (mHandler == null){
                    mHandler = new Handler();
                }
                move();
                break;
        }
    }


    // 将字符串写入到文本文件中
    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    /**
     * GPS坐标转高德坐标
     *
     * @param sourceLatLng
     * @return
     */
    private LatLng convert(LatLng sourceLatLng) {
        CoordinateConverter.CoordType mcoordtype = CoordinateConverter.CoordType.valueOf("GPS");
        CoordinateConverter converter = new CoordinateConverter(this);
        // CoordType.GPS 待转换坐标类型
        converter.from(mcoordtype);
        // sourceLatLng待转换坐标点
        converter.coord(sourceLatLng);
        // 执行转换操作
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    private void initRoute() {
        addPolylineInPlayGround();
        //绘制起点和终点marker
        drawaStartAndEnd();
        double minLat = points.get(0).latitude;
        double minLng = points.get(0).longitude;
        double maxLat = 0;
        double maxLng = 0;
        for (int i = 0; i < points.size(); i++) {
            minLat = Math.min(minLat, points.get(i).latitude);
            minLng = Math.min(minLng, points.get(i).longitude);
            maxLat = Math.max(maxLat, points.get(i).latitude);
            maxLng = Math.max(maxLng, points.get(i).longitude);
        }
        if (minLat == maxLat && minLng == maxLng) {
            maxLat += 0.00001;
            maxLng += 0.00001;
        }
        LatLng maxLatLng = new LatLng(maxLat, maxLng);
        LatLng minLatLng = new LatLng(minLat, minLng);
//        List<LatLng> points = readLatLngs();
        LatLngBounds bounds = new LatLngBounds(minLatLng, maxLatLng);
//        for (int i = 0; i < points.size(); i++) {
//            bounds.including(points.get(i));
//        }
        try {
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        } catch (Exception e) {
            e.printStackTrace();
        }
        smoothMarker = new SmoothMarker(aMap);
        smoothMarker.setMoveListener(this);
        smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.car));
        LatLng drivePoint = points.get(0);
        Pair<Integer, LatLng> pair = PointsUtil.calShortestDistancePoint(points, drivePoint);
        points.set(pair.first, drivePoint);
        List<LatLng> subList = points.subList(pair.first, points.size());

        smoothMarker.setPoints(subList);
        smoothMarker.setTotalDuration(points.size() * 1000);
    }


    /**********************************************************************************************************/
    /**********************************************************************************************************/
    /**********************************************************************************************************/

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mBinding.map.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mBinding.map.onPause();

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mBinding.map.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.map.onDestroy();

    }

    SmoothMarker smoothMarker;
    boolean isplay = false;
    private MarkerOptions markerOption;

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(LatLng latlng) {

        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(latlng)
                .icon(
                        // BitmapDescriptorFactory
                        // .fromResource(R.drawable.location_marker)
                        BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                .decodeResource(getResources(),
                                        R.drawable.car_location)))
                .draggable(true);
        aMap.addMarker(markerOption);
        aMap.getScalePerPixel();
    }

    /**
     * 在地图上添加car
     */
    private void addCarToMap(LatLng latlng) {

        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(latlng)
                .icon(
                        // BitmapDescriptorFactory
                        // .fromResource(R.drawable.location_marker)
                        BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                .decodeResource(getResources(),
                                        R.drawable.car)))
                .draggable(true);
        aMap.addMarker(markerOption);
        changeCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        latlng, 15, 0, 0)), null);
    }

    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update, AMap.CancelableCallback callback) {
//        if (animated) {
//        aMap.animateCamera(update, 1000, callback);
//        } else {
        aMap.moveCamera(update);
//        }
    }

    ArrayList<MarkerOptions> makers = new ArrayList<>();

    /**
     * 在地图上添加marker
     */
    private void addAllMarkersToMap() {

        for (LatLng latLng : points) {
            MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(latLng)
                    .icon(
                            // BitmapDescriptorFactory
                            // .fromResource(R.drawable.location_marker)
                            BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                    .decodeResource(getResources(),
                                            R.drawable.car_location)))
                    .visible(false);

            makers.add(markerOption);
        }
        aMap.addMarkers(makers, true);
    }

    private Handler mHandler;
    int i = 0;

    public void move() {

        mHandler.post(new Runnable() {
                          @Override
                          public void run() {
                              // TODO Auto-generated method stub

                              if (isplay) {
//                                  if (i < points.size()) {
//                                      addMarkersToMap(points.get(i));
//                                      i++;
//                                      mHandler.postDelayed(this, 300);
//
//                                  } else {
//                                      if (smoothMarker != null) {
//
//                                          smoothMarker.startSmoothMove();
//                                      }
//                                  }
                                  if (smoothMarker != null) {
                                      if(mBinding.rbHalfSpeed.isChecked()){
                                          smoothMarker.setTotalDuration(points.size() * 2000);
                                      }else if(mBinding.rbNormalSpeed.isChecked()){
                                          smoothMarker.setTotalDuration(points.size() * 1000);
                                      }else if(mBinding.rbTwoSpeed.isChecked()){
                                          smoothMarker.setTotalDuration(points.size() * 500);
                                      }
                                      smoothMarker.startSmoothMove();

                                  }
                              } else {
                                  if (smoothMarker != null)
                                      smoothMarker.stopMove();
                              }

                          }
                      }

        );


    }

    public void stop(View view) {

        StopCarRoute();
    }

    AMap.InfoWindowAdapter infoWindowAdapter = new AMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {

            return getInfoWindowView(marker);
        }

        @Override
        public View getInfoContents(Marker marker) {


            return getInfoWindowView(marker);
        }
    };

    LinearLayout infoWindowLayout;
    TextView title;
    TextView snippet;

    private View getInfoWindowView(Marker marker) {
        if (infoWindowLayout == null) {
            infoWindowLayout = new LinearLayout(mContext);
            infoWindowLayout.setOrientation(LinearLayout.VERTICAL);
            title = new TextView(mContext);
            snippet = new TextView(mContext);
            title.setTextColor(Color.BLACK);
            snippet.setTextColor(Color.BLACK);
//            infoWindowLayout.setBackgroundResource(R.drawable.infowindow_bg);

            infoWindowLayout.addView(title);
            infoWindowLayout.addView(snippet);
        }

        return infoWindowLayout;
    }

    private void addPolylineInPlayGround() {
//        List<LatLng> list = readLatLngs();
        List<Integer> colorList = new ArrayList<Integer>();
        List<BitmapDescriptor> bitmapDescriptors = new ArrayList<BitmapDescriptor>();

        int[] colors = new int[]{Color.argb(255, 0, 255, 0), Color.argb(255, 255, 255, 0), Color.argb(255, 255, 0, 0)};

        //用一个数组来存放纹理
        List<BitmapDescriptor> textureList = new ArrayList<BitmapDescriptor>();
        textureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture));

        List<Integer> texIndexList = new ArrayList<Integer>();
        texIndexList.add(0);//对应上面的第0个纹理
        texIndexList.add(1);
        texIndexList.add(2);

        Random random = new Random();
        for (int i = 0; i < points.size(); i++) {
            colorList.add(colors[random.nextInt(3)]);
            bitmapDescriptors.add(textureList.get(0));

        }

        try {
            aMap.addPolyline(new PolylineOptions().setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.custtexture)) //setCustomTextureList(bitmapDescriptors)
                    //				.setCustomTextureIndex(texIndexList)
                    .addAll(points)
                    .useGradient(true)
                    .width(12));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        mTraceClient = new LBSTraceClient(this.getApplicationContext());
//        mTraceClient.queryProcessedTrace(mSequenceLineID, mTraceList,
//                LBSTraceClient.TYPE_AMAP, this);
    }


    private void addMarkerInBeijing(int number) {
        Random random = new Random();

        //new LatLng(39.90403, 116.407525)
        double tem_lat = 39.9;
        double tem_lon = 116.4;
        BitmapDescriptor descriptor = BitmapDescriptorFactory.defaultMarker();
        if (number == 1) {
            aMap.addMarker(new MarkerOptions().position(new LatLng(tem_lat, tem_lon)).icon(descriptor));
            return;
        }

        for (int i = 0; i < number; i++) {
            LatLng latLng = new LatLng(tem_lat + random.nextDouble() * (random.nextBoolean() ? 1 : -1), tem_lon + random.nextDouble() * (random.nextBoolean() ? 1 : -1));
            aMap.addMarker(new MarkerOptions().position(latLng).icon(descriptor));
        }

    }

    /**
     * 轨迹纠偏失败回调
     */
    @Override
    public void onRequestFailed(int lineID, String errorInfo) {
        Toast.makeText(this.getApplicationContext(), errorInfo,
                Toast.LENGTH_SHORT).show();
        if (mOverlayList.containsKey(lineID)) {
            TraceOverlay overlay = mOverlayList.get(lineID);
            overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FAILURE);
            setDistanceWaitInfo(overlay);
        }
    }

    /**
     * 轨迹纠偏过程回调
     */
    @Override
    public void onTraceProcessing(int lineID, int index, List<LatLng> segments) {
        if (segments == null) {
            return;
        }
        if (mOverlayList.containsKey(lineID)) {
            TraceOverlay overlay = mOverlayList.get(lineID);
            overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_PROCESSING);
            overlay.add(segments);
        }
    }

    /**
     * 轨迹纠偏结束回调
     */
    @Override
    public void onFinished(int lineID, List<LatLng> linepoints, int distance,
                           int watingtime) {
        Toast.makeText(this.getApplicationContext(), "onFinished",
                Toast.LENGTH_SHORT).show();
        if (mOverlayList.containsKey(lineID)) {
            TraceOverlay overlay = mOverlayList.get(lineID);
            overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FINISH);
            overlay.setDistance(distance);
            overlay.setWaitTime(watingtime);
            setDistanceWaitInfo(overlay);
        }

    }

    /**
     * 设置显示总里程和等待时间
     *
     * @param overlay
     */
    private void setDistanceWaitInfo(TraceOverlay overlay) {
        int distance = -1;
        int waittime = -1;
        if (overlay != null) {
            distance = overlay.getDistance();
            waittime = overlay.getWaitTime();
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        mBinding.tvtrip.setText(mDistanceString
                + decimalFormat.format(distance / 1000d) + DISTANCE_UNIT_DES + "\n" + mStopTimeString
                + decimalFormat.format(waittime / 60d) + TIME_UNIT_DES);
    }

    @Override
    public void move(double distance) {
        if (distance == 0) {
            StopCarRoute();
        }
    }

    private void StopCarRoute() {

//        btnStart.setBackgroundResource(R.drawable.ic_track_play);
        if (smoothMarker != null)
            smoothMarker.destroy();
        isplay = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.ivStop.setImageResource(R.drawable.bg_reasume);
            }
        });

        i = 0;
        aMap.clear();
        initRoute();

    }

}
