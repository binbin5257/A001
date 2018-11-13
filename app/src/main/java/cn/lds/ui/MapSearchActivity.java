package cn.lds.ui;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.enums.MapSearchGridType;
import cn.lds.common.table.SearchPoiTitleTable;
import cn.lds.common.table.base.DBManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.OnItemClickListener;
import cn.lds.common.utils.OnItemLongClickListener;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.databinding.ActivityMapSearchBinding;
import cn.lds.ui.adapter.MapSearchGridAdapter;
import cn.lds.ui.adapter.MapSearchListAdapter;
import cn.lds.widget.dialog.CenterListDialog;
import cn.lds.widget.dialog.callback.OnDialogOnItemClickListener;
import cn.lds.widget.pullToRefresh.PullToRefreshBase;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * 地图搜索poi 界面
 * 列表默认显示收藏夹数据。
 */
public class MapSearchActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener, PoiSearch.OnPoiSearchListener {
    private ActivityMapSearchBinding mBinding;

    private String keyWord = "";// 要输入的poi搜索关键字
    private String cityStr = CacheHelper.getCity();// 要输入的城市名字或者城市区号

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索


    private MapSearchListAdapter listAdapter;

    private final int REQUESTMORE = 999;

    private Realm realm;//数据库 实例

    private boolean needJummp = false;//是否需要跳转，到poi定位页面；
    private CenterListDialog centerListDialog;
    private List<PoiItem> mList = new ArrayList<PoiItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_map_search);
        initView();
        initListener();
    }

    /**
     * 初始化界面
     */
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mBinding.mapSearchList.setLayoutManager(linearLayoutManager);
        listAdapter = new MapSearchListAdapter(mList, mContext, new OnItemClickListener() {
            @Override
            public void onItemClick(Object data, int position) {
                final PoiItem poiItem = (PoiItem) data;
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
        }, new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(Object data, final int listPosition) {
                if (ToolsHelper.isNull(keyWord)) {//表示历史列表被点击，弹出删除选项
                    final PoiItem poiItem = (PoiItem) data;
                    ArrayList<String> strings = new ArrayList<>();
                    strings.add("删除");
                    centerListDialog = new CenterListDialog(MapSearchActivity.this, mContext, strings).setOnDialogOnItemClickListener(new OnDialogOnItemClickListener() {
                        @Override
                        public void onDialogItemClick(Dialog dialog, int position) {
                            switch (position) {
                                case 0:
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            SearchPoiTitleTable titleTable = realm.where(SearchPoiTitleTable.class).
                                                    contains("title", poiItem.getTitle()).findFirst();
                                            if (null != titleTable) {
                                                titleTable.deleteFromRealm();
                                                listAdapter.remove(listPosition);
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
        mBinding.mapSearchList.setAdapter(listAdapter);
        readPoiHistory();
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
                RealmResults<SearchPoiTitleTable> poiTitleTables = realm.where(SearchPoiTitleTable.class).equalTo("loginId",CacheHelper.getLoginId()).findAllSorted("time", Sort.DESCENDING);
                List<PoiItem> list = new ArrayList<>();
                for (int i = 0; i < poiTitleTables.size(); i++) {
                    if (i >= 10) {
                        break;
                    }
                    SearchPoiTitleTable titleTable = poiTitleTables.get(i);
                    PoiItem poiItem = new PoiItem(titleTable.getTitle(), null, titleTable.getTitle(), titleTable.getSnippet());
                    list.add(poiItem);
                }
                listAdapter.updateAdapter(list);
            }
        });

    }

    /**
     * 初始化监听
     */
    @Override
    public void initListener() {
        mBinding.topBackIv.setOnClickListener(this);
        mBinding.topMenuLyt.setOnClickListener(this);
        mBinding.pullScrollView.setMode(PullToRefreshBase.Mode.DISABLED);//上拉加载更多
        mBinding.pullScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                refreshView.onRefreshComplete();
                if (ToolsHelper.isNull(keyWord))
                    return;
                currentPage++;
                doSearchQuery();//分页加载
            }
        });

        mBinding.mapSearchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (0 == mBinding.mapSearchEdit.getText().length()) {
                    ToolsHelper.showInfo(mContext, "请输入关键词");
                }

                return true;
            }
        });

        mBinding.mapSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                currentPage = 0;//分页重置
                if (0 == charSequence.length()) {//显示历史记录
                    mBinding.pullScrollView.setMode(PullToRefreshBase.Mode.DISABLED);
                    readPoiHistory();
                } else {
                    mBinding.pullScrollView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
                    keyWord = charSequence.toString();
                    doSearchQuery();//输入检索
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
     * 搜索poi
     *
     * @param data
     *         点击的数据
     * @param position
     *         点击位置
     */
    @Override
    public void onItemClick(Object data, int position) {
        MapSearchGridType gridType = (MapSearchGridType) data;
        switch (gridType) {
            case SHOUCANGJIA:
                startActivity(new Intent(mContext, CollectionsActivity.class));
                break;
            case JINGXIAOSHANG:
                startActivity(new Intent(mContext, DealerListActivity.class));
                break;
            case GENGDUO:
                Intent intent = new Intent(mContext, PoiLocatedActivity.class);
                intent.setAction(PoiLocatedActivity.ACTIONMORE);
                startActivityForResult(intent, REQUESTMORE);
                break;
            default:
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
                    String key = data.getStringExtra("keyWord");
                    searchData(key);
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
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(30);// 设置每页最多返回多少条poiitem
        query.setCityLimit(true);
        query.setPageNum(currentPage);// 设置查第一页
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);

        //已人为中心
        double lng = Double.parseDouble(CacheHelper.getLongitude());
        double lat = Double.parseDouble(CacheHelper.getLatitude());
        LatLonPoint lp = new LatLonPoint(lat, lng);
//        poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true)); //设置周边搜索的中心点以及区域 5000米-5公里
        poiSearch.searchPOIAsyn();
    }

    /**
     * POI信息查询回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
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
                            listAdapter.updateAdapter(poiItems);
                        } else {
                            listAdapter.add(poiItems);
                        }


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
                    listAdapter.clear();
                }
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != realm) {
            realm.close();
            realm = null;
        }
        centerListDialog = null;
    }
}
