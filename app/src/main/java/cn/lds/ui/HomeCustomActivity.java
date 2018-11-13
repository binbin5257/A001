package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.lds.R;
import cn.lds.amap.util.ToastUtil;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.table.HomeCustomTable;
import cn.lds.common.table.TableHelper;
import cn.lds.common.utils.CacheHelper;
import cn.lds.databinding.ActivityHomeCustomBinding;
import cn.lds.ui.adapter.HomeCustomMoreAadapter;
import cn.lds.ui.adapter.HomeCustomShowAadapter;

/**
 * 首页定制页面
 * Created by sibinbin on 18-4-24.
 */

public class HomeCustomActivity extends BaseActivity implements View.OnClickListener {

    private ActivityHomeCustomBinding mBinding;
    private ImageView backBtn;
    private HomeCustomShowAadapter showAadapter;
    private HomeCustomMoreAadapter moreAadapter;
    private List<HomeCustomTable> showList;
    private List<HomeCustomTable> moreList;
    private List<HomeCustomTable> mAllList = new ArrayList<>();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();

    }

    private void initData() {
    }

    @Override
    public void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_custom);
        TextView title = mBinding.getRoot().findViewById(R.id.top_title_tv);
        title.setText("首页定制");
        backBtn = mBinding.getRoot().findViewById(R.id.top_back_iv);
        String loginId = CacheHelper.getLoginId();
        showList = TableHelper.getInstance().getHomeCustomFromGropByLoginId(loginId, 0);
        moreList = TableHelper.getInstance().getHomeCustomFromGropByLoginId(loginId, 1);
            //创建LinearLayoutManager 对象
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(this);
        mLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.showRecyclerView.setLayoutManager(mLayoutManager);
        mBinding.moreRecyclerView.setLayoutManager(mLayoutManager2);
        showAadapter = new HomeCustomShowAadapter(showList);
        moreAadapter = new HomeCustomMoreAadapter(moreList);
        mBinding.showRecyclerView.setAdapter(showAadapter);
        mBinding.moreRecyclerView.setAdapter(moreAadapter);


    }

    @Override
    public void initListener() {
        backBtn.setOnClickListener(this);
        showAadapter.setOnButtonClickListener(new HomeCustomShowAadapter.OnButtonClickListener() {
            @Override
            public void removeShowItem( int position ) {
                HomeCustomTable homeCustomTable = showList.get(position);
                homeCustomTable.setGroup(1);
                moreList.add(0,homeCustomTable);
                showList.remove(homeCustomTable);
                showAadapter.notifyDataSetChanged();
                moreAadapter.notifyDataSetChanged();

            }
        });
        moreAadapter.setOnButtonClickListener(new HomeCustomMoreAadapter.OnButtonClickListener() {
            @Override
            public void addShowItem( int position ) {
                HomeCustomTable homeCustomTable = moreList.get(position);
                homeCustomTable.setGroup(0);
                showList.add(homeCustomTable);
                moreList.remove(position);
                showAadapter.notifyDataSetChanged();
                moreAadapter.notifyDataSetChanged();
            }
        });
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags( RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder ) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove( RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target ) {

                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(showList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(showList, i, i - 1);
                    }
                }
                showAadapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped( RecyclerView.ViewHolder viewHolder, int direction ) {

            }

            @Override
            public void onSelectedChanged( RecyclerView.ViewHolder viewHolder, int actionState ) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.line_bg));
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView( RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder ) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.line_bg));
            }
        });
        mItemTouchHelper.attachToRecyclerView(mBinding.showRecyclerView);
    }

    @Override
    public void onClick( View v ) {
        switch (v.getId()){
            case R.id.top_back_iv:
                backPre();
                break;
        }
    }

    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event ) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            backPre();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 返回上一页
     */
    private void backPre() {
        if(showList.size() != 2){
//            showList.clear();
//            moreList.clear();
//            showList.addAll(TableHelper.getInstance().getHomeCustomFromGropByLoginId(CacheHelper.getLoginId(), 0));
//            moreList.addAll(TableHelper.getInstance().getHomeCustomFromGropByLoginId(CacheHelper.getLoginId(), 1));
//            showAadapter.notifyDataSetChanged();
//            moreAadapter.notifyDataSetChanged();
            ToastUtil.show(mContext,"首页只能显示两项");
        }else{
            mAllList.clear();
            mAllList.addAll(showList);
            mAllList.addAll(moreList);
            TableHelper.getInstance().postHomeCustomListByLoginId(CacheHelper.getLoginId(),mAllList);

        }
        finish();


    }

}
