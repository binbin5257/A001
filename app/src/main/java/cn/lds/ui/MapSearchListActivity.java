package cn.lds.ui;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.data.HomeAndCompanyModel;
import cn.lds.common.enums.MapSearchGridType;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.CarControlManager;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.table.SearchPoiTitleTable;
import cn.lds.common.table.base.DBManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.OnItemClickListener;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityMapSearchListBinding;
import cn.lds.ui.adapter.MapSearchGridAdapter;
import cn.lds.ui.adapter.MapSearchListAdapterNew;
import cn.lds.ui.view.MapSerarchListView;
import cn.lds.widget.PullToRefreshLayout;
import cn.lds.widget.dialog.CenterListDialog;
import cn.lds.widget.dialog.LoadingDialogUtils;
import cn.lds.widget.dialog.callback.OnDialogOnItemClickListener;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MapSearchListActivity extends BaseActivity implements OnItemClickListener, PoiSearch.OnPoiSearchListener, View.OnClickListener {

    private ActivityMapSearchListBinding mBinding;
    private final int REQUESTMORE = 999;
    private boolean needJummp = false;//是否需要跳转，到poi定位页面；
    private Realm realm;
    private List<PoiItem> mPoiItemList = new ArrayList<>();
    private MapSearchListAdapterNew adapterNew;
    private int currentPage = 0;
    private String keyWord;
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PoiResult poiResult; // poi返回的结果
    private MapSerarchListView lv;
    private PullToRefreshLayout pr;
    private boolean isLoadMore = false;
    private boolean isBound = false; // 是否增加搜索范围


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search_list);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_map_search_list);
        initView();
        initListener();
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
     * 请求
     *
     * @param event
     *         返回数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.dealer.equals(apiNo)
        ))
            return;
        LoadingDialogUtils.dissmiss();
        switch (apiNo){
            case HttpApiKey.dealer:
//                Intent intent = new Intent(mContext,DealerListActivity.class);
//                intent.putExtra("dealer_data",httpResult.getResult());
//                startActivity(intent);
                break;
        }

    }

    @Override
    public void initView() {
        //创建一个GridLayout管理器,设置为4列
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        //设置GridView方向为:垂直方向
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //添加到RecyclerView容器里面
        mBinding.mapSearchGrid.setLayoutManager(layoutManager);
        //设置自动适应配置的大小
        mBinding.mapSearchGrid.setHasFixedSize(true);
        //创建适配器对象
        MapSearchGridAdapter adapter = new MapSearchGridAdapter(MapSearchGridType.getList(), mContext, this);
        mBinding.mapSearchGrid.setAdapter(adapter);

        adapterNew = new MapSearchListAdapterNew(mPoiItemList,this);
        lv = findViewById(R.id.poi_list);
        lv.setAdapter(adapterNew);
        readPoiHistory();
        pr = findViewById(R.id.pull_to_refresh_view);
        pr.setFooterBackgroundRes(R.color.white);
        pr.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh( PullToRefreshLayout pullToRefreshLayout ) {

            }

            @Override
            public void onLoadMore( PullToRefreshLayout pullToRefreshLayout ) {
                if(isLoadMore){
                    currentPage ++;
                    doSearchQuery();
                }else{
                    pr.loadmoreFinish(PullToRefreshLayout.FAIL_NO_DATA);
                }

            }
        });

    }
    /**
     * 点击事件
     *
     * @param view
     *         点击的view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.top_menu_lyt:
                if (0 == mBinding.mapSearchEdit.getText().length()) {
                    ToolsHelper.showInfo(mContext, "请输入关键词");
                    return;
                }
                break;
        }
    }

    /**
     * 初始化 请求数据
     */
    private void getDealerListData() {
        LoadingDialogUtils.showVertical(mContext, "请稍候");
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

    @Override
    public void initListener() {
        mBinding.topBackIv.setOnClickListener(this);
        mBinding.topMenuLyt.setOnClickListener(this);
        mBinding.mapSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                currentPage = 0;//分页重置
                if (0 == charSequence.length()) {//显示历史记录
                    isLoadMore = false;
                    readPoiHistory();
                } else {
                    isLoadMore = true;
                    keyWord = charSequence.toString();
                    doSearchQuery();//输入检索
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                final PoiItem poiItem =mPoiItemList.get(position);
                if (null == poiItem.getLatLonPoint()) {//为空表示为 历史记录
                    searchData(poiItem.getTitle());
                    return;
                }
                Intent intent = new Intent(mContext, PoiLocatedActivity.class);
                intent.setAction(PoiLocatedActivity.ACTIONSINGLEPOI);
                intent.putExtra("poiItem", poiItem);
                intent.putExtra("keyWord", keyWord);
                startActivity(intent);
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            private CenterListDialog centerListDialog;

            @Override
            public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {
                if (ToolsHelper.isNull(keyWord)) {//表示历史列表被点击，弹出删除选项
                    final PoiItem poiItem = mPoiItemList.get(position);
                    ArrayList<String> strings = new ArrayList<>();
                    strings.add("删除");
                    centerListDialog = new CenterListDialog(MapSearchListActivity.this, mContext, strings).setOnDialogOnItemClickListener(new OnDialogOnItemClickListener() {
                        @Override
                        public void onDialogItemClick( Dialog dialog, final int position) {
                            switch (position) {
                                case 0:
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            SearchPoiTitleTable titleTable = realm.where(SearchPoiTitleTable.class).
                                                    contains("title", poiItem.getTitle()).findFirst();
                                            if (null != titleTable) {
                                                titleTable.deleteFromRealm();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mPoiItemList.remove(position);
                                                        adapterNew.notifyDataSetChanged();
                                                    }
                                                });

                                            }
                                        }
                                    });
                                    break;
                            }
                            centerListDialog.dismiss();
                        }
                    });
                    centerListDialog.show();
                    return true;
                } else
                    return false;

            }
        });
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(30);// 设置每页最多返回多少条poiitem
        query.setCityLimit(true);
        query.setPageNum(currentPage);// 设置查第一页
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);

        if(isBound){
            //已人为中心
            isBound = false;
            double lng = Double.parseDouble(CacheHelper.getLongitude());
            double lat = Double.parseDouble(CacheHelper.getLatitude());
            LatLonPoint lp = new LatLonPoint(lat, lng);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 50*1000, true)); //设置周边搜索的中心点以及区域 50公里
        }
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onItemClick( Object data, int position ) {
        MapSearchGridType gridType = (MapSearchGridType) data;
        switch (gridType) {
            case SHOUCANGJIA:
                startActivity(new Intent(mContext, CollectionsActivity.class));
                break;
            case JINGXIAOSHANG:
                getDealerListData();
                break;
            case GENGDUO:
                Intent intent = new Intent(mContext, PoiLocatedActivity.class);
                intent.setAction(PoiLocatedActivity.ACTIONMORE);
                startActivityForResult(intent, REQUESTMORE);
                break;
            default:
                isBound = true;
                searchData(gridType.getValue());
                needJummp = true;
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUESTMORE:
                    if(data != null){
                        String key = data.getStringExtra("keyWord");
                        searchData(key);
                    }else{
                        searchData("");
                    }

                    break;
                default:
                    searchData("");
                    break;
            }
        }
    }

    /**
     * 搜索poi
     *
     * @param s
     *         关键词
     */
    private void searchData(String s) {
        mBinding.mapSearchEdit.setText(s);
        mBinding.mapSearchEdit.setSelection(s.length());
    }

    /**
     * 读取搜索记录
     */
    private void readPoiHistory() {
        if (null == realm) {
            realm = DBManager.getInstance().getRealm();
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<SearchPoiTitleTable> poiTitleTables = realm.where(SearchPoiTitleTable.class).equalTo("loginId", CacheHelper.getLoginId()).findAllSorted("time", Sort.DESCENDING);
                List<PoiItem> list = new ArrayList<>();
                for (int i = 0; i < poiTitleTables.size(); i++) {
                    if (i >= 10) {
                        break;
                    }
                    SearchPoiTitleTable titleTable = poiTitleTables.get(i);
                    PoiItem poiItem = new PoiItem(titleTable.getTitle(), null, titleTable.getTitle(), titleTable.getSnippet());
                    list.add(poiItem);
                    if(mPoiItemList.size() != 0){
                        mPoiItemList.clear();
                    }
                    mPoiItemList.addAll(list);
                    adapterNew.notifyDataSetChanged();

                }

            }
        });

    }

    @Override
    public void onPoiSearched( PoiResult result, int rCode ) {
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    if (!ToolsHelper.isNull(keyWord)) {//如果关键字为空，不更新poi数据。否则影响 历史记录显示问题
                        poiResult = result;
                        // 取得搜索到的poiitems有多少页
                        ArrayList<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
//                    List<SuggestionCity> suggestionCities = poiResult
//                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                        if (0 == currentPage) {
                            mPoiItemList.clear();
                        }

                        mPoiItemList.addAll(poiItems);
                        adapterNew.notifyDataSetChanged();
                        pr.loadmoreFinish(PullToRefreshLayout.SUCCEED);


                        if (needJummp) {//是否需要跳转到定位页
                            Intent defaultIntent = new Intent(mContext, PoiLocatedActivity.class);
                            defaultIntent.setAction(PoiLocatedActivity.ACTIONPOILIST);
                            defaultIntent.putExtra("keyWord", keyWord);
                            defaultIntent.putParcelableArrayListExtra("list", poiItems);
                            startActivity(defaultIntent);
                            needJummp = false;//跳转后开关，关闭
                        }
                    }
                } else {
//                    ToolsHelper.showInfo(mContext, "搜索结果为空");
                    mPoiItemList.clear();
                    adapterNew.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onPoiItemSearched( PoiItem poiItem, int i ) {

    }

    @Override
    public void onPointerCaptureChanged( boolean hasCapture ) {

    }
}
