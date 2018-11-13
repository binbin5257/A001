package cn.lds.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.base.BaseFragment;
import cn.lds.common.base.UIInitListener;
import cn.lds.common.data.ConditionReportModel;
import cn.lds.common.data.ControlCarFailtrueEvent;
import cn.lds.common.data.ControlCarSuccessEvent;
import cn.lds.common.data.ControlCarWaitEvent;
import cn.lds.common.data.DeleteFailHistotyEvent;
import cn.lds.common.data.HiddenSuccessEvent;
import cn.lds.common.data.MessageCountModel;
import cn.lds.common.data.TokenModel;
import cn.lds.common.data.UpdateCarInfo;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.CarControlManager;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.table.CarsTable;
import cn.lds.common.table.ControlCarFailtureHistoryTable;
import cn.lds.common.table.HomeCustomTable;
import cn.lds.common.table.TableHelper;
import cn.lds.common.table.base.DBManager;
import cn.lds.common.utils.AnimationUtil;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.OnItemClickListener;
import cn.lds.common.utils.SpacesItemDecoration;
import cn.lds.common.utils.TimeHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.FragmentCarDetailBinding;
import cn.lds.ui.ControlCarFailureResonActivity;
import cn.lds.ui.HomeCustomActivity;
import cn.lds.ui.MainActivity;
import cn.lds.ui.MessageActivity;
import cn.lds.ui.SettingActivity;
import cn.lds.ui.WebviewActivity;
import cn.lds.ui.adapter.CarDetailOthersAdapter;
import cn.lds.ui.adapter.MenuCarListAdapter;
import cn.lds.ui.view.UIHelper;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.captcha.Utils;
import cn.lds.widget.dialog.ConfirmDialog;
import cn.lds.widget.dialog.LoadingDialogUtils;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;
import cn.lds.widget.pullToRefresh.PullToRefreshBase;
import io.realm.Realm;
import io.realm.RealmResults;


/**
 * 车况界面
 */
public class DetailFragment extends BaseFragment implements UIInitListener, View.OnClickListener, OnItemClickListener {

    private FragmentCarDetailBinding mBinding;
    private List<CarsTable> carsTableList;
    private Typeface typeface;//字体
    private List<String> data_list;
    private ConditionReportModel.DataBean dataBean;
    private DecimalFormat df;
    private int fuelType = 2;
    private MainActivity activity;
    private ControlCarFailtureHistoryTable oneControlCarFailHistory;
    private String mColorModel;
    private CarDetailOthersAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<String> gridlist;
    private List<Integer> picList;

    private String service_type = "";

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance() {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_car_detail, null, false);
        initView();
        initListener();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        showControlCarHistory();
    }

    @Override
    public void initView() {
        df = new DecimalFormat("#,##0");
        data_list = new ArrayList<>();

        DBManager.getInstance().getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<CarsTable> carsTables = realm.where(CarsTable.class).equalTo("account", CacheHelper.getAccount()).findAll();
                carsTableList = realm.copyFromRealm(carsTables);
                for (int i = 0; i < carsTables.size(); i++) {
                    CarsTable carsTable = carsTables.get(i);
                    if (carsTable != null) {
                        String text = "暂无车牌";
                        if (ToolsHelper.isNull(carsTable.getLicensePlate())) {
                            if (!ToolsHelper.isNull(carsTable.getMode())) {
                                text = "暂无车牌";
                            }
                        } else {
                            text = carsTable.getLicensePlate();
                        }
                        if (!TextUtils.isEmpty(CacheHelper.getVin())) {
                            if (carsTable.getVin().equals(CacheHelper.getVin())) {
                                mBinding.carLisenceNoTv.setText(text);
                            }
                        }
                        data_list.add(text);
                    }
                }
            }
        });
        if (carsTableList != null && carsTableList.size() > 0) {
            if (TextUtils.isEmpty(CacheHelper.getVin())) {
                mBinding.carLisenceNoTv.setText(data_list.get(0));
                mColorModel = carsTableList.get(0).getModelColor();
            } else {
                if (!CacheHelper.getVin().equals(CacheHelper.getUsualcar().getVin())) {
                    for (CarsTable table : carsTableList) {
                        if (CacheHelper.getVin().equals(table.getVin())) {
                            CacheHelper.setUsualcar(table);
                            mColorModel = table.getModelColor();
                        }
                    }
                }
            }
        }
        if (data_list.size() > 1) {
            mBinding.ivTopDown.setVisibility(View.VISIBLE);
        }else{
            mBinding.ivTopDown.setVisibility(View.INVISIBLE);
        }
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "DINCond-Bold.otf");

        //创建一个GridLayout管理器,设置为4列
        linearLayoutManager = new LinearLayoutManager(getActivity());
        //设置GridView方向为:垂直方向
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //添加到RecyclerView容器里面
        mBinding.carDetailOthers.setLayoutManager(linearLayoutManager);
        //设置自动适应配置的大小
        mBinding.carDetailOthers.setHasFixedSize(true);


        int spacingInPixels = ToolsHelper.dpToPx(6);
        mBinding.carDetailOthers.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mBinding.pullScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mBinding.pullScrollView.onRefreshComplete();
                if (!"nullVin".equals(CacheHelper.getVin())) {
                    LoadingDialogUtils.showVertical(getActivity(), getString(R.string.loading_waitting));
                    CarControlManager.getInstance().conditionReport();
                } else {
                    ToastUtil.showToast(BaseApplication.getInstance().getBaseContext(), "无车辆");

                }
            }
        });
        mBinding.menuIcon.setVisibility(View.INVISIBLE);
        //创建适配器对象
        gridlist = new ArrayList<>();
//        gridlist.add("创新车险");
//        gridlist.add("创新车险");
//        gridlist.add("创新车险");
//        gridlist.add("保养");
        gridlist.add("创新车险");
        gridlist.add("豹王之战");
        gridlist.add("驾驶报告");
        gridlist.add("爱车保养");
//        picList = Arrays.asList(R.drawable.detail_others_1, R.drawable.bg_detail_maintain, R.drawable.bg_detail_illegal, R.drawable.bg_detail_maintencel,R.drawable.bg_innovative_car_insurance, R.drawable.bg_tora, R.drawable.bg_driver_report, R.drawable.bg_detail_mainten);
        picList = Arrays.asList(R.drawable.bg_detail_maintencel,R.drawable.bg_innovative_car_insurance, R.drawable.bg_tora, R.drawable.bg_driver_report, R.drawable.bg_detail_mainten);
        getService(0);
    }


    @Override
    public void onItemClick(Object data, int position) {
        if (position == 0) { //爱车保养
//            WebviewActivity.enterWebviewActivity(getActivity(),ModuleUrls.maintanceUrl
//                    .replace("{city}", CacheHelper.getCity())
//                    .replace("{adCode}", CacheHelper.getCityAdCode())
//                    .replace("{longitude}", CacheHelper.getLongitude())
//                    .replace("{latitude}", CacheHelper.getLatitude())
//                    .replace("{vin}", CacheHelper.getVin()));
            LoadingDialogUtils.showVertical(getActivity(), "请稍候");
            MainActivity.UBI_FRAGMENT = "DetailFragment";
            service_type = "ubi_fragment";
            getToken();
        } else if (position == 1) { //爱车保养
            LoadingDialogUtils.showVertical(getActivity(), "请稍候");
            MainActivity.UBI_FRAGMENT = "DetailFragment";
            service_type = "ubi_fragment";
            getToken();
        } else if (position == 2) {
            MainActivity.UBI_FRAGMENT = "DetailFragment";
            LoadingDialogUtils.showVertical(getActivity(), "请稍候");
            service_type = "ubi_fragment";
            getToken();
        } else if (position == 3) {
            MainActivity.UBI_FRAGMENT = "DetailFragment";
            LoadingDialogUtils.showVertical(getActivity(), "请稍候");
            service_type = "maintanceUrl";
            getToken();

        } else if(position == 4){
            LoadingDialogUtils.showVertical(getActivity(), "请稍候");
            MainActivity.UBI_FRAGMENT = "DetailFragment";
            service_type = "maintanceUrl";
            getToken();
//            WebviewActivity.enterWebviewActivity(getActivity(),ModuleUrls.maintanceUrl
//                    .replace("{city}", CacheHelper.getCity())
//                    .replace("{adCode}", CacheHelper.getCityAdCode())
//                    .replace("{longitude}", CacheHelper.getLongitude())
//                    .replace("{latitude}", CacheHelper.getLatitude())
//                    .replace("{vin}", CacheHelper.getVin()));
        }
    }

    /**
     * 获取token
     */
    private void getToken() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("password", BaseApplication.jbgsn);
            RequestManager.getInstance().post(ModuleUrls.getToken, HttpApiKey.getToken, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initListener() {
        mBinding.carLisenceNoLlyt.setOnClickListener(this);
        mBinding.menuIcon.setOnClickListener(this);
        mBinding.menuNotices.setOnClickListener(this);
        mBinding.carLisenceNoTv.setOnClickListener(this);
        mBinding.carInfoConfigLeft.setOnClickListener(this);
        mBinding.carInfoConfigRight.setOnClickListener(this);
        mBinding.tvConfirm.setOnClickListener(this);
        mBinding.seeSee.setOnClickListener(this);
        mBinding.ivRemove.setOnClickListener(this);
        mBinding.carDetailService.setOnClickListener(this);
        mBinding.carDetailRecommend.setOnClickListener(this);
        mBinding.mainLocationAddress.setOnClickListener(this);
        mBinding.carLisenceNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(R.color.white));    //设置颜色
                tv.setTypeface(typeface);//设置字体
                if (null != carsTableList && !carsTableList.isEmpty()) {
                    CarsTable carsTable = carsTableList.get(i);
                    CacheHelper.setUsualcar(carsTable);
                    LoadingDialogUtils.showVertical(getActivity(), getString(R.string.loading_waitting));
                    CarControlManager.getInstance().conditionReport();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mBinding.carDetailOthers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged( RecyclerView recyclerView, int newState ) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastVisibleItemPosition= linearLayoutManager.findLastVisibleItemPosition();//可见范围内的最后一项的位置
//                if(lastVisibleItemPosition == 4){
//                    mBinding.carDetailService.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.car_detail_text_size_11));
//                    mBinding.carDetailService.setTextColor(getActivity().getResources().getColor(R.color.car_detail_text_color));
//                    mBinding.carDetailRecommend.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.car_detail_text_size_13));
//                    mBinding.carDetailRecommend.setTextColor(getActivity().getResources().getColor(R.color.white));
//                }else if(lastVisibleItemPosition == 3){
//                    mBinding.carDetailRecommend.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.car_detail_text_size_11));
//                    mBinding.carDetailRecommend.setTextColor(getActivity().getResources().getColor(R.color.car_detail_text_color));
//                    mBinding.carDetailService.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.car_detail_text_size_13));
//                    mBinding.carDetailService.setTextColor(getActivity().getResources().getColor(R.color.white));
//                }
                mBinding.carDetailService.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.car_detail_text_size_11));
                mBinding.carDetailService.setTextColor(getActivity().getResources().getColor(R.color.car_detail_text_color));
                mBinding.carDetailRecommend.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.car_detail_text_size_13));
                mBinding.carDetailRecommend.setTextColor(getActivity().getResources().getColor(R.color.white));

            }

            @Override
            public void onScrolled( RecyclerView recyclerView, int dx, int dy ) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void getRecommend() {
        //创建适配器对象
        List<String> gridlist = new ArrayList<>();
        gridlist.add("创新车险");
        gridlist.add("豹王之战");
        gridlist.add("驾驶报告");
        gridlist.add("爱车保养");
        List<Integer> picList = Arrays.asList(R.drawable.bg_innovative_car_insurance, R.drawable.bg_tora, R.drawable.bg_driver_report, R.drawable.bg_detail_mainten);
        CarDetailOthersAdapter adapter = new CarDetailOthersAdapter(gridlist, picList, getActivity(), this);
        mBinding.carDetailOthers.setAdapter(adapter);
    }

    private void getService(int position) {
        mBinding.carDetailService.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.car_detail_text_size_11));
        mBinding.carDetailService.setTextColor(getActivity().getResources().getColor(R.color.car_detail_text_color));
        mBinding.carDetailRecommend.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.car_detail_text_size_13));
        mBinding.carDetailRecommend.setTextColor(getActivity().getResources().getColor(R.color.white));
        if(adapter == null){
            adapter = new CarDetailOthersAdapter(gridlist, picList, getActivity(), this);
            mBinding.carDetailOthers.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
        linearLayoutManager.scrollToPositionWithOffset(position,0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.car_lisence_no_llyt:
                mBinding.carLisenceNo.performClick();
                break;
            case R.id.menu_icon:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.menu_notices:
                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            case R.id.car_lisence_no_tv:
                if (data_list.size() > 1) {
                    showPopu(data_list);
                }
                break;
            case R.id.car_info_config_left:
                if (!"nullVin".equals(CacheHelper.getVin())) {
                    enterCarInfoShowConfigActivity(0);
                } else {
                    ToastUtil.showToast(BaseApplication.getInstance().getBaseContext(), "无车辆");
                }

                break;
            case R.id.car_info_config_right:
                if (!"nullVin".equals(CacheHelper.getVin())) {
                    enterCarInfoShowConfigActivity(1);
                } else {
                    ToastUtil.showToast(BaseApplication.getInstance().getBaseContext(), "无车辆");
                }

                break;
            case R.id.tv_confirm:
                EventBus.getDefault().post(new HiddenSuccessEvent());
                break;
            case R.id.see_see: //查看控车失败详情
                LookAtControlCarFailtureDetail();
                break;
            case R.id.iv_remove:
                activity.deleteOneControlCarFailHistory(CacheHelper.getVin(), oneControlCarFailHistory.getType());
                break;
            case R.id.car_detail_service:
//                mBinding.carDetailService.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.car_detail_text_size_13));
//                mBinding.carDetailService.setTextColor(getActivity().getResources().getColor(R.color.white));
//                mBinding.carDetailRecommend.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.car_detail_text_size_11));
//                mBinding.carDetailRecommend.setTextColor(getActivity().getResources().getColor(R.color.car_detail_text_color));
                getService(0);
                break;
            case R.id.car_detail_recommend:
                mBinding.carDetailService.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.car_detail_text_size_11));
                mBinding.carDetailService.setTextColor(getActivity().getResources().getColor(R.color.car_detail_text_color));
                mBinding.carDetailRecommend.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.car_detail_text_size_13));
                mBinding.carDetailRecommend.setTextColor(getActivity().getResources().getColor(R.color.white));
                getService(0);
                break;
            case R.id.main_location_address:
                if(!"车门未关闭".equals(mBinding.mainLocationAddress.getText())){
                    activity.switeNaviFragment();
                }
                break;
        }
    }


    /**
     * 查看控车失败详情
     */
    private void LookAtControlCarFailtureDetail() {
        final String content = "您在" + TimeHelper.getTimeByType(oneControlCarFailHistory.getTime(), TimeHelper.FORMAT9) + activity.convertHttpKey(oneControlCarFailHistory.getType()) + "操作没有成功";
        ConfirmDialog dialog = new ConfirmDialog(getActivity()).setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClick(Dialog dialog, String clickPosition) {
                switch (clickPosition) {
                    case ClickPosition.SUBMIT:
                        // TODO1.跳转到失败原因界面 2.删除本地数据库该条失败记录
                        Intent intent = new Intent(getContext(), ControlCarFailureResonActivity.class);
                        intent.putExtra("reason", content);
                        startActivity(intent);
                        activity.deleteOneControlCarFailHistory(oneControlCarFailHistory.getVin(), oneControlCarFailHistory.getType());
                        break;
                    case ClickPosition.CANCEL:
                        activity.deleteOneControlCarFailHistory(CacheHelper.getVin(), oneControlCarFailHistory.getType());
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
    public void deleteCarFailtrueHistory( DeleteFailHistotyEvent event){
        mBinding.rlControlWaite.setVisibility(View.GONE);
        mBinding.rlControlSuccess.setVisibility(View.GONE);
        showControlCarHistory();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void controlCarFailtrue(ControlCarFailtrueEvent event) {
        mBinding.rlControlWaite.setVisibility(View.GONE);
        mBinding.rlControlSuccess.setVisibility(View.GONE);
        showControlCarHistory();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hiddenSuccessBar(HiddenSuccessEvent event) {
        mBinding.rlControlSuccess.setVisibility(View.GONE);
        showControlCarHistory();
    }

    private void showControlCarHistory() {
        oneControlCarFailHistory = activity.getOneControlCarFailHistory(CacheHelper.getVin());
        if (null == oneControlCarFailHistory) {
            mBinding.rlControlFalture.setVisibility(View.GONE);
        } else {
            mBinding.rlControlFalture.setVisibility(View.VISIBLE);
            mBinding.tvControlName.setText(oneControlCarFailHistory.getContent());
        }
    }

    /**
     * 进入车辆信息配置页面
     *
     * @param positon
     */
    private void enterCarInfoShowConfigActivity(int positon) {
        Intent intent = new Intent(getActivity(), HomeCustomActivity.class);
        startActivity(intent);
    }

    private void showPopu(final List<String> list) {
        View popuView = View.inflate(getActivity(), R.layout.layout_car_list, null);
        ListView listView = popuView.findViewById(R.id.list_car);
        listView.setAdapter(new MenuCarListAdapter(getActivity(), list, carsTableList));
        final PopupWindow popupWindow = new PopupWindow(popuView,
                Utils.dp2px(getContext(), 150), RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(popuView);
        popupWindow.setOutsideTouchable(true);
        AnimationUtil.startRoateUp(getActivity(),mBinding.ivTopDown);
        popupWindow.showAsDropDown(mBinding.llCarNo, mBinding.carLisenceNoTv.getWidth() / 2 - popupWindow.getWidth() / 2, 0);//-mBinding.carLisenceNoTv.getWidth()/2 + Utils.dp2px(getActivity(),20)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBinding.carLisenceNoTv.setText(list.get(position));
                if (null != carsTableList && !carsTableList.isEmpty()) {
                    CarsTable carsTable = carsTableList.get(position);
                    mColorModel = carsTable.getModelColor();
                    CacheHelper.setUsualcar(carsTable);
                    LoadingDialogUtils.showVertical(getActivity(), getString(R.string.loading_waitting));
                    CarControlManager.getInstance().conditionReport();
                    EventBus.getDefault().post(new UpdateCarInfo());

                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                AnimationUtil.startRoateDown(getActivity(),mBinding.ivTopDown);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        initCarInfo();
    }

    /**
     * 初始化车辆信息
     */
    private void initCarInfo() {
        String vin = CacheHelper.getVin();
        if (carsTableList != null && carsTableList.size() > 0) {
            for (int i = 0; i < carsTableList.size(); i++) {
                if (carsTableList.get(i) != null
                        && carsTableList.get(i).getVin() != null
                        && carsTableList.get(i).getVin().equals(vin)) {
                    mColorModel = carsTableList.get(i).getModelColor();
                    if (!TextUtils.isEmpty(carsTableList.get(i).getLicensePlate())) {
                        mBinding.carLisenceNoTv.setText(carsTableList.get(i).getLicensePlate());
//                        mBinding.carLisenceNoTv.setText("暂无车牌");
                    } else {
                        mBinding.carLisenceNoTv.setText("暂无车牌");
                    }
                    CarControlManager.getInstance().conditionReport();

                }
            }
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCarInfo(UpdateCarInfo event) {
        showControlCarHistory();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void controCarWaiting(ControlCarWaitEvent event) {
        mBinding.rlControlSuccess.setVisibility(View.GONE);
        mBinding.rlControlFalture.setVisibility(View.GONE);
        mBinding.rlControlWaite.setVisibility(View.VISIBLE);
        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.pull_rotate);
        mBinding.loadingIcon.startAnimation(rotateAnimation);
        switch (event.getmType()) {
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
    public void controlCarSuccess(ControlCarSuccessEvent event) {
        mBinding.rlControlWaite.setVisibility(View.GONE);
        mBinding.rlControlFalture.setVisibility(View.GONE);
        mBinding.rlControlSuccess.setVisibility(View.VISIBLE);
        switch (event.getmType()) {
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageCountSuccess(MessageCountModel model) {
        if(model.getData() > 0){
            mBinding.menuNotices.setImageResource(R.drawable.main_top_notices_red);
        }else{
            mBinding.menuNotices.setImageResource(R.drawable.main_top_notices);
        }

    }

    /**
     * 车辆详情 api 成功
     *
     * @param event 返回数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void conditionReportSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.conditionReport.equals(apiNo)
                || HttpApiKey.getToken.equals(apiNo)

        ))
            return;
        LoadingDialogUtils.dissmiss();
        switch (apiNo) {
            case HttpApiKey.conditionReport:
                processCarInfoData(httpResult);
                break;
            case HttpApiKey.getToken:
                processToken(httpResult);
                break;
        }

    }

    /**
     * 解析token
     *
     * @param httpResult
     */
    private void processToken(HttpResult httpResult) {
        TokenModel model = GsonImplHelp.get().toObject(httpResult.getResult(), TokenModel.class);
        if(model != null){
            if(MainActivity.UBI_FRAGMENT.equals("DetailFragment")){
                if(!TextUtils.isEmpty(model.getData())){
                    String token = model.getData();
                    enterService( token );

                }else{
                    ToastUtil.showToast( getActivity(),"账号异常，请联系经销商解决" );
                }

            }
        }else{
            ToastUtil.showToast( getActivity(),"账号异常，请联系经销商解决" );
        }
    }

    private void enterService( String token ) {
        if("maintanceUrl".equals( service_type )){
             WebviewActivity.enterWebviewActivity(getActivity(), ModuleUrls.maintanceUrl
                .replace("{city}", CacheHelper.getCity())
                .replace("{adCode}", CacheHelper.getCityAdCode())
                .replace("{longitude}", CacheHelper.getLongitude())
                .replace("{latitude}", CacheHelper.getLatitude())
                .replace("{token}", token)
                .replace("{vin}", CacheHelper.getVin()));

        }else{
            WebviewActivity.enterWebviewActivity(getActivity(),"http://webservice.cihon.cn/activity-leopaard/#/?token="+ token +"&vin="
                    + CacheHelper.getVin());
//            WebviewActivity.enterWebviewActivity(getActivity(),"http://123.57.60.91/activity-leopaard/#/?token=testtoken&vin=testvin");

        }
    }



    /**
     * 解析车辆信息数据
     *
     * @param httpResult
     */
    private void processCarInfoData(HttpResult httpResult) {
        final ConditionReportModel model = GsonImplHelp.get().toObject(httpResult.getResult(), ConditionReportModel.class);
        if (null == model || null == model.getData()) {
            CarControlManager.getInstance().setCarDetail(null);
            updateUi(null);
        } else {
            dataBean = model.getData();
            CarControlManager.getInstance().setCarDetail(dataBean);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dataBean.setCarAdress(getString(R.string.unknown_address));
                    try {
                        if (dataBean.getLatitude() != 0.0 && dataBean.getLongitude() != 0.0) {
                            LatLng latLng = new LatLng(dataBean.getLatitude(), dataBean.getLongitude());
                            LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
                            final RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 100,
                                    GeocodeSearch.GPS);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                            GeocodeSearch geocoderSearch = new GeocodeSearch(getActivity());
                            RegeocodeAddress regeocodeAddress = geocoderSearch.getFromLocation(query);// 设置同步逆地理编码请求
                            if (null != regeocodeAddress && !ToolsHelper.isNull(regeocodeAddress.getFormatAddress())) {
                                dataBean.setCarAdress(regeocodeAddress.getFormatAddress());
                            }
                        }

                    } catch (AMapException e) {
                        e.printStackTrace();
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBinding.mainLocationAddress.setText(dataBean.getCarAdress());
                            updateUi(dataBean);
                        }
                    });
                }
            }).start();

        }
    }

    /**
     * 设置数据
     *
     * @param data
     */
    private void updateUi(ConditionReportModel.DataBean data) {
        if (null == data || "nullVin".equals(CacheHelper.getVin())) {
            mBinding.carDetailKm.setTextSize(14);
            mBinding.carDetailOil.setTextSize(14);
            mBinding.carDetailAver.setTextSize(14);
            mBinding.carDetailTotalKm.setTextSize(14);
            mBinding.carDetailKm.setText(getString(R.string.null_string));//续航里程
            mBinding.carDetailKmHint.setVisibility(View.GONE);
            mBinding.ivElectricOil.setImageResource(R.drawable.car_detail_oil);
            mBinding.carDetailOil.setText(getString(R.string.null_string));//剩余电量，油量
            mBinding.carDetailOilHint.setVisibility(View.GONE);
            mBinding.carDetailAver.setText(getString(R.string.null_string));//平均功耗，油耗
            mBinding.carDetailAverHint.setVisibility(View.GONE);
            mBinding.carDetailTotalKm.setText(getString(R.string.null_string));//总里程
            mBinding.carDetailTotalKmHint.setVisibility(View.GONE);
            mBinding.mainLocationAddress.setText(getString(R.string.unknown_address));
            mBinding.mainUpdateTime.setText("");//更新时间

        } else {
            mBinding.carDetailKm.setTextSize(32);
            mBinding.carDetailOil.setTextSize(32);
            mBinding.carDetailAver.setTextSize(32);
            mBinding.carDetailTotalKm.setTextSize(32);
            mBinding.mainUpdateTime.setText(new StringBuilder().append("更新时间：").
                    append(TimeHelper.getTimeByType(System.currentTimeMillis(), TimeHelper.FORMAT8)).toString());//更新时间
            mBinding.mainLocationAddress.setText(data.getCarAdress());//车位置信息
            mBinding.carDetailKmHint.setVisibility(View.VISIBLE);
            mBinding.carDetailOilHint.setVisibility(View.VISIBLE);
            mBinding.carDetailAverHint.setVisibility(View.VISIBLE);
            mBinding.carDetailTotalKmHint.setVisibility(View.VISIBLE);
            int fuelType = CacheHelper.getUsualcar().getFuelType();
            switch (fuelType) {
                case 2://EV车
                    mBinding.carDetailAverText.setText(getString(R.string.aver_text_ev));//平均功耗
                    mBinding.carDetailAverHint.setText(getString(R.string.aver_hint_ev));
                    mBinding.carDetailAverHint.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(data.getChargingStatus()) && !"3".equals(data.getChargingStatus())) {
                        mBinding.ivPlug.setVisibility(View.VISIBLE);
                        mBinding.carDetailOilText.setText("充电中");
                        mBinding.carDetailOilText.setTextColor(Color.parseColor("#32F4FF"));
                    } else {
                        mBinding.ivPlug.setVisibility(View.INVISIBLE);
                        mBinding.carDetailOilText.setText(getString(R.string.enerage_text_ev));
                        mBinding.carDetailOilText.setTextColor(Color.parseColor("#99ffffff"));
                    }
                    mBinding.ivElectricOil.setImageResource(UIHelper.getEvDrawableRes(data.getSoc()));
                    //剩余电量
                    mBinding.carDetailOil.setText(ToolsHelper.toString(data.getSoc()));
                    break;
                default://油车
                    mBinding.carDetailAverText.setText(getString(R.string.aver_text_oil));//平均油耗
                    mBinding.carDetailAverHint.setText(getString(R.string.aver_hint_oil));
                    mBinding.carDetailAverHint.setVisibility(View.VISIBLE);
                    mBinding.carDetailOilText.setText(getString(R.string.enerage_text_oil));//剩余油量
                    mBinding.carDetailOil.setText(ToolsHelper.toString(data.getRemianOil()));
                    mBinding.ivElectricOil.setImageResource(UIHelper.getOilDrawableRes(data.getRemianOil()));
                    mBinding.carDetailOilText.setTextColor(Color.parseColor("#99ffffff"));
                    mBinding.ivPlug.setVisibility(View.INVISIBLE);
                    break;
            }
            double enduranceMileage = data.getEnduranceMileage();
            if (enduranceMileage == 65535) {
                enduranceMileage = 0;
            } else {
                enduranceMileage = enduranceMileage / 10;
            }
            if (!data.isDoorClosed()) {
                //车门未锁
                if("YELLOW".equals(mColorModel)){
                    mBinding.ivCar.setImageResource(R.drawable.bg_home_car_yellow_open);
                }else if("BLUE".equals(mColorModel)){
                    mBinding.ivCar.setImageResource(R.drawable.bg_car_door_unlock);
                }
                mBinding.ivLocationAddress.setImageResource(R.drawable.ic_car_door_unlock);
                mBinding.mainLocationAddress.setText("车门未关闭");
                mBinding.mainLocationAddress.setTextColor(Color.parseColor("#FF2555"));
            } else {
                if("YELLOW".equals(mColorModel)){
                    mBinding.ivCar.setImageResource(R.drawable.bg_home_car_yellow_closed);
                }else if("BLUE".equals(mColorModel)){
                    mBinding.ivCar.setImageResource(R.drawable.car_detail_iv);
                }
                mBinding.ivLocationAddress.setImageResource(R.drawable.car_detail_location);
                mBinding.mainLocationAddress.setTextColor(Color.parseColor("#ffabb3c8"));

            }


            mBinding.carDetailKm.setText(ToolsHelper.toString(df.format(enduranceMileage)));//续航里程
        }
        //显示首页定制显示
        showHomeCustom();
        //初始化控车页面状态值
        initControlFragmentStatus();
    }

    private void initControlFragmentStatus() {
        if(dataBean != null){
            EventBus.getDefault().post(dataBean);
        }
    }

    /**
     * 首页定制显示
     */
    private void showHomeCustom() {
        String loginId = CacheHelper.getLoginId();
        List<HomeCustomTable> customTables = TableHelper.getInstance().getHomeCustomByLoginId(loginId);
        List<String> customNameList = Arrays.asList(getResources().getStringArray(R.array.home_custom));
        if (customTables == null || customTables.size() == 0) {
            TableHelper.getInstance().postHomeCustomByLoginId(loginId, customNameList);
            updateLeftTab(customNameList.get(0));
            updateRightTab(customNameList.get(1));
        } else {
            updateLeftTab(customTables.get(0).getName());
            updateRightTab(customTables.get(1).getName());
        }
    }

    /**
     * 设置车辆信息显示配置
     */
    private void setCarInfoShowConfig() {
        String leftType = CacheHelper.getCarInfoShowConfigLeft();
        String rightType = CacheHelper.getCarInfoShowConfigRight();
        if (TextUtils.isEmpty(leftType)) {
            leftType = "MILEAGE";
        }
        if (TextUtils.isEmpty(rightType)) {
            rightType = "AVERAGE";
        }
        updateLeftTab(leftType);
        updateRightTab(rightType);
    }

    /**
     * 车辆详情 api 失败
     *
     * @param event 返回数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void conditionReportFailed(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.conditionReport.equals(apiNo)
                || HttpApiKey.getToken.equals(apiNo)
        ))
            return;
        LoadingDialogUtils.dissmiss();
        switch (apiNo) {
            case HttpApiKey.conditionReport:
                CarControlManager.getInstance().setCarDetail(null);
                updateUi(null);
                ToolsHelper.showHttpRequestErrorMsg(getActivity(), httpResult);
                break;
            case HttpApiKey.getToken:
                if (MainActivity.UBI_FRAGMENT.equals("DetailFragment")) {
                    ToastUtil.showToast(getActivity(), "账号异常，请联系经销商解决");
                   enterService( "" );
                }
                break;
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 0x02) {
                initView();
            } else if (requestCode == 0) { // 左侧tab 显示车辆配置项目
                String type = data.getStringExtra("TYPE");
                CacheHelper.setCarInfoShowConfigLeft(type);
                updateLeftTab(type);

            } else if (requestCode == 1) {
                String type = data.getStringExtra("TYPE");
                CacheHelper.setCarInfoShowConfigRight(type);
                updateRightTab(type);
            }


        }

    }

    /**
     * 更新右侧车辆信息显示tab
     *
     * @param type
     */
    private void updateRightTab(String type) {
        if (dataBean == null) {
            return;
        }
        if (CacheHelper.getUsualcar() != null) {
            fuelType = CacheHelper.getUsualcar().getFuelType();
        }
        if (fuelType == 2) { //电车
            switch (type) {
                case "MILEAGE":
                    double totalMilege = dataBean.getTotalMileage() / 10;
                    mBinding.carDetailAver.setText(ToolsHelper.toString(totalMilege));//总里程
                    mBinding.carDetailAverHint.setVisibility(View.VISIBLE);
                    mBinding.carDetailAverHint.setText("km");
                    mBinding.carDetailAverText.setText("总里程");
                    break;
                case "AVERAGE":
                    mBinding.carDetailAver.setText(ToolsHelper.toString(Math.abs(dataBean.getAveragePower() / 10)));
                    mBinding.carDetailAverHint.setVisibility(View.VISIBLE);
                    mBinding.carDetailAverHint.setText("kw.h/100km");
                    mBinding.carDetailAverText.setText("平均功率");
                    break;
                case "INSTANTANEOUS":
                    mBinding.carDetailAver.setText(ToolsHelper.toString(Math.abs(dataBean.getInstantanePower() / 10)));
                    mBinding.carDetailAverHint.setVisibility(View.VISIBLE);
                    mBinding.carDetailAverHint.setText("kw.h/100km");
                    mBinding.carDetailAverText.setText("瞬时功率");
                    break;
                case "AIR_CONDITIONER":
                    if (!dataBean.isAirConditionerStatus()) {
                        mBinding.carDetailAver.setText("关闭");
                    } else {
                        mBinding.carDetailAver.setText("打开");
                    }
                    mBinding.carDetailAver.setTextSize(24);
                    mBinding.carDetailAverHint.setVisibility(View.GONE);
                    mBinding.carDetailAverText.setText("空调状态");
                    break;
                case "STATUS_LOCKED":
                    mBinding.carDetailAverHint.setVisibility(View.GONE);
                    mBinding.carDetailAverText.setText("车锁状态");
                    mBinding.carDetailAver.setTextSize(24);
                    if (dataBean.isCarLocked()) {
                        mBinding.carDetailAver.setText("关锁");
                    } else {
                        mBinding.carDetailAver.setText("开锁");
                    }

            }
        } else { //油车
            switch (type) {
                case "MILEAGE":
                    double totalMilege = dataBean.getTotalMileage() / 10;
                    mBinding.carDetailAver.setText(ToolsHelper.toString(totalMilege));//总里程
                    mBinding.carDetailAverHint.setVisibility(View.VISIBLE);
                    mBinding.carDetailAverHint.setText("km");
                    mBinding.carDetailAverText.setText("总里程");
                    break;
                case "AVERAGE":
                    mBinding.carDetailAver.setText(ToolsHelper.toString(Math.abs(dataBean.getAvergeFuleCon() / 10)));
                    mBinding.carDetailAverHint.setVisibility(View.VISIBLE);
                    mBinding.carDetailAverHint.setText("L/100km");
                    mBinding.carDetailAverText.setText("平均油耗");
                    break;
                case "INSTANTANEOUS":
                    mBinding.carDetailAver.setText(ToolsHelper.toString(Math.abs(dataBean.getInstantaneFuleCon() / 10)));
                    mBinding.carDetailAverHint.setVisibility(View.VISIBLE);
                    mBinding.carDetailAverHint.setText(dataBean.getInstantaneFuleConUnit());
                    mBinding.carDetailAverText.setText("瞬时油耗");
                    break;
                case "AIR_CONDITIONER":

                    if (!dataBean.isAirConditionerStatus()) {
                        mBinding.carDetailAver.setText("关闭");
                    } else {
                        mBinding.carDetailAver.setText("打开");
                    }
                    mBinding.carDetailAver.setTextSize(24);
                    mBinding.carDetailAverHint.setVisibility(View.GONE);
                    mBinding.carDetailAverText.setText("空调状态");
                    break;

                case "STATUS_LOCKED":
                    if (dataBean.isCarLocked()) {
                        mBinding.carDetailAver.setText("关锁");
                    } else {
                        mBinding.carDetailAver.setText("开锁");
                    }
                    mBinding.carDetailAver.setTextSize(24);
                    mBinding.carDetailAverHint.setVisibility(View.GONE);
                    mBinding.carDetailAverText.setText("车锁状态");
                    break;


            }
        }


    }

    /**
     * 更新左侧车辆信息显示tab
     *
     * @param type
     */
    private void updateLeftTab(String type) {
        if (dataBean == null) {
            return;
        }
        if (CacheHelper.getUsualcar() != null) {
            fuelType = CacheHelper.getUsualcar().getFuelType();
        }
        if (fuelType == 2) { //电车
            switch (type) {
                case "MILEAGE":
                    double totalMilege = dataBean.getTotalMileage() / 10;
                    mBinding.carDetailTotalKm.setText(ToolsHelper.toString(totalMilege));//总里程
                    mBinding.carDetailTotalKmHint.setVisibility(View.VISIBLE);
                    mBinding.leftTabTv.setText("总里程");
                    break;
                case "AVERAGE":
                    mBinding.carDetailTotalKm.setText(ToolsHelper.toString(Math.abs(dataBean.getAveragePower() / 10)));
                    mBinding.carDetailTotalKmHint.setVisibility(View.VISIBLE);
                    mBinding.carDetailTotalKmHint.setText("kw.h/100km");
                    mBinding.leftTabTv.setText("平均功率");
                    break;
                case "INSTANTANEOUS":
                    mBinding.carDetailTotalKm.setText(ToolsHelper.toString(Math.abs(dataBean.getInstantanePower() / 10)));
                    mBinding.carDetailTotalKmHint.setVisibility(View.VISIBLE);
                    mBinding.carDetailTotalKmHint.setText("kw.h/100km");
                    mBinding.leftTabTv.setText("瞬时功率");
                    break;
                case "AIR_CONDITIONER":
                    if (!dataBean.isAirConditionerStatus()) {
                        mBinding.carDetailTotalKm.setText("关闭");
                    } else {
                        mBinding.carDetailTotalKm.setText("打开");
                    }
                    mBinding.carDetailTotalKm.setTextSize(24);
                    mBinding.carDetailTotalKmHint.setVisibility(View.GONE);
                    mBinding.leftTabTv.setText("空调状态");
                    break;

                case "STATUS_LOCKED":
                    if (dataBean.isCarLocked()) {
                        mBinding.carDetailTotalKm.setText("关锁");
                    } else {
                        mBinding.carDetailTotalKm.setText("开锁");
                    }
                    mBinding.carDetailTotalKm.setTextSize(24);
                    mBinding.carDetailTotalKmHint.setVisibility(View.GONE);
                    mBinding.leftTabTv.setText("车锁状态");
                    break;

            }
        } else { //油车
            switch (type) {
                case "MILEAGE":
                    CacheHelper.setCarInfoShowConfigLeft("MILEAGE");
                    double totalMilege = dataBean.getTotalMileage() / 10;
                    mBinding.carDetailTotalKm.setText(ToolsHelper.toString(totalMilege));//总里程
                    mBinding.carDetailTotalKmHint.setVisibility(View.VISIBLE);
                    mBinding.leftTabTv.setText("总里程");
                    break;
                case "AVERAGE":
                    mBinding.carDetailTotalKm.setText(ToolsHelper.toString(Math.abs(dataBean.getAvergeFuleCon() / 10)));
                    mBinding.carDetailTotalKmHint.setVisibility(View.VISIBLE);
                    mBinding.carDetailTotalKmHint.setText("L/100km");
                    mBinding.leftTabTv.setText("平均油耗");
                    break;
                case "INSTANTANEOUS":
                    mBinding.carDetailTotalKm.setText(ToolsHelper.toString(Math.abs(dataBean.getInstantaneFuleCon() / 10)));
                    mBinding.carDetailTotalKmHint.setVisibility(View.VISIBLE);
                    mBinding.carDetailTotalKmHint.setText(dataBean.getInstantaneFuleConUnit());
                    mBinding.leftTabTv.setText("瞬时油耗");
                    break;
                case "AIR_CONDITIONER":
                    if (!dataBean.isAirConditionerStatus()) {
                        mBinding.carDetailTotalKm.setText("关闭");
                    } else {
                        mBinding.carDetailTotalKm.setText("打开");
                    }
                    mBinding.carDetailTotalKm.setTextSize(24);
                    mBinding.carDetailTotalKmHint.setVisibility(View.GONE);
                    mBinding.leftTabTv.setText("空调状态");
                    break;
                case "STATUS_LOCKED":
                    if (dataBean.isCarLocked()) {
                        mBinding.carDetailTotalKm.setText("关锁");
                    } else {
                        mBinding.carDetailTotalKm.setText("开锁");
                    }
                    mBinding.carDetailTotalKm.setTextSize(24);
                    mBinding.carDetailTotalKmHint.setVisibility(View.GONE);
                    mBinding.leftTabTv.setText("车锁状态");

            }
        }
    }
}
