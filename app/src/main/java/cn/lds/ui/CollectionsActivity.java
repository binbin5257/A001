package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.data.CollectionsModel;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.CarControlManager;
import cn.lds.common.manager.UMengManager;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityCollectionsBinding;
import cn.lds.ui.adapter.CollectiosAdapter;
import cn.lds.widget.dialog.LoadingDialogUtils;

/**
 * 收藏列表
 *
 * @author leadingsoft
 */
public class CollectionsActivity extends BaseActivity implements View.OnClickListener {
    ActivityCollectionsBinding mBinding;
    private CollectiosAdapter collectiosAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_collections);
        initView();
        initListener();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        UMengManager.getInstance().onResumePage("showFavorite");
        LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
        CarControlManager.getInstance().getCollections();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMengManager.getInstance().onPausePage("showFavorite");

    }

    /**
     * 初始化view
     */
    @Override
    public void initView() {
        TextView titile = mBinding.getRoot().findViewById(R.id.top_title_tv);
//        ImageView imageView = mBinding.getRoot().findViewById(R.id.top_menu_iv);
//        imageView.setImageResource(R.mipmap.ic_launcher);
//        imageView.setOnClickListener(this);
        titile.setText("收藏夹");
        List<CollectionsModel.DataBean> collectionList = new ArrayList<>();
        collectiosAdapter = new CollectiosAdapter(collectionList, mContext,this);
        mBinding.collectListview.setAdapter(collectiosAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mBinding.collectListview.setLayoutManager(layoutManager);
    }

    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        mBinding.jia.setOnClickListener(this);
        mBinding.company.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.top_menu_iv:
                LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
                CollectionsModel.DataBean dataBean = new CollectionsModel.DataBean();
                dataBean.setAddress("阜外大街22号");
                dataBean.setCollectId("BV10951039");
                dataBean.setDesc("联通智网");
                dataBean.setLatitude(1111);
                dataBean.setLongitude(222);
                dataBean.setName("联通" + System.currentTimeMillis());
                dataBean.setTel("!2312314324");
                dataBean.setTypeCode("");
                CarControlManager.getInstance().addCollection(dataBean, null);
                break;
            case R.id.jia:
                LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
                CollectionsModel.DataBean jia = new CollectionsModel.DataBean();
                jia.setAddress("阜外大街22号");
                jia.setCollectId("BV10951059");
                jia.setDesc("联通智网");
                jia.setLatitude(1111);
                jia.setLongitude(222);
                jia.setName("家");
                jia.setTel("!2312314324");
                jia.setTypeCode("");
                Map<String, String> ex = new HashMap<>();
                ex.put("type", "home");
                CarControlManager.getInstance().addCollection(jia, ex);
                break;
            case R.id.company:
                LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
                CollectionsModel.DataBean company = new CollectionsModel.DataBean();
                company.setAddress("阜外大街22号");
                company.setCollectId("BV10951049");
                company.setDesc("联通智网");
                company.setLatitude(1111);
                company.setLongitude(222);
                company.setName("公司");
                company.setTel("!2312314324");
                company.setTypeCode("");
                Map<String, String> ex2 = new HashMap<>();
                ex2.put("type", "company");
                CarControlManager.getInstance().addCollection(company, ex2);
                break;
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
        if (!(HttpApiKey.getCollections.equals(apiNo) ||
                HttpApiKey.deleteCollections.equals(apiNo) ||
                HttpApiKey.postPoi.equals(apiNo) ||
                HttpApiKey.addCollections.equals(apiNo) ||
                HttpApiKey.cancelSetHomeAndCompy.equals(apiNo) ||
                HttpApiKey.modifyCollected.equals(apiNo) ||
                HttpApiKey.setHomeAndCompany.equals(apiNo))

                )
            return;
        LoadingDialogUtils.dissmiss();
        if (HttpApiKey.getCollections.equals(apiNo)
                || HttpApiKey.setHomeAndCompany.equals(apiNo)
                || HttpApiKey.modifyCollected.equals(apiNo)
                || HttpApiKey.cancelSetHomeAndCompy.equals(apiNo)) {
            CollectionsModel collectionsModel = GsonImplHelp.get().toObject(httpResult.getResult(), CollectionsModel.class);
            if (null == collectionsModel || null == collectionsModel.getData() || collectionsModel.getData().isEmpty())
                return;
            List<CollectionsModel.DataBean> collectionList = collectionsModel.getData();
            collectiosAdapter.updateAdapter(collectionList);
        } else if (HttpApiKey.postPoi.equals(apiNo)) {
            ToolsHelper.showInfo(mContext, "下发成功");
        } else if(HttpApiKey.setHomeAndCompany.equals(apiNo)){
            collectiosAdapter.notifyDataSetChanged();
        }
        // TODO: 18/2/28 根据接口返回，对应的poiidtable 进行一个增删操作；
//        else if (HttpApiKey.addCollections.equals(apiNo)) {
//            PoiIdTable poiIdTable = new PoiIdTable(poiItem.getPoiId());
//            realm.copyToRealmOrUpdate(poiIdTable);
//            mBinding.mapSearchCollect.performClick();
//        } else if (HttpApiKey.deleteCollections.equals(apiNo)) {
//            PoiIdTable poiIdTable = realm.where(PoiIdTable.class).
//                    contains("poiId", poiItem.getPoiId()).findFirst();
//            if (null != poiIdTable) {
//                poiIdTable.deleteFromRealm();
//                mBinding.mapSearchCollect.performClick();
//            }
//        }
        else {
            initData();
            Map<String, String> map = event.getResult().getExtras();
            if (null == map || map.isEmpty())
                return;
            String type = map.get("type");
            if (!ToolsHelper.isNull(type)) {
                CarControlManager.getInstance().getHomeAndCompany();
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
        if (!(HttpApiKey.getCollections.equals(apiNo) ||
                HttpApiKey.deleteCollections.equals(apiNo) ||
                HttpApiKey.postPoi.equals(apiNo) ||
                HttpApiKey.setHomeAndCompany.equals(apiNo) ||
                HttpApiKey.modifyCollected.equals(apiNo) ||
                HttpApiKey.cancelSetHomeAndCompy.equals(apiNo) ||
                HttpApiKey.addCollections.equals(apiNo))
                )
            return;
        LoadingDialogUtils.dissmiss();
        ToolsHelper.showHttpRequestErrorMsg(mContext, httpResult);
    }
}
