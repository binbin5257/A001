package cn.lds.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.common.base.BaseFragment;
import cn.lds.common.table.CarsTable;
import cn.lds.common.table.base.DBManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.databinding.FragmentCarDetailBinding;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 车况页面
 * Created by sibinbin on 18-4-24.
 */

public class CarConditionFragment extends BaseFragment {

    private FragmentCarDetailBinding mBinding;
    private List<String> carNoList = new ArrayList<>();
    private List<CarsTable> carsTableList;

    @Nullable
    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_car_detail, null, false);
        return mBinding.getRoot();
    }
    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated(view, savedInstanceState);
        initData(); //初始化页面数据
        initListener(); //初始化页面点击事件
    }
    /**
     * 初始化页面数据
     */
    private void initData() {
        //获取车辆列表数据
        DBManager.getInstance().getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute( Realm realm ) {
                RealmResults<CarsTable> carsTables = realm.where(CarsTable.class).equalTo("account",CacheHelper.getAccount()).findAll();
                carsTableList = realm.copyFromRealm(carsTables);
                for (int i = 0; i < carsTables.size(); i++) {
                    CarsTable carsTable = carsTables.get(i);
                    if (carsTable != null) {
                        String text = "无车牌";
                        if (ToolsHelper.isNull(carsTable.getLicensePlate())) {
                            if (!ToolsHelper.isNull(carsTable.getMode())) {
                                text = carsTable.getMode();
                            }
                        } else {
                            text = carsTable.getLicensePlate();
                        }
                        if (!TextUtils.isEmpty(CacheHelper.getVin())) {
                            if (carsTable.getVin().equals(CacheHelper.getVin())) {
                                mBinding.carLisenceNoTv.setText(text);
                            }
                        }
                        carNoList.add(text);
                    }
                }
            }
        });
        if(carsTableList != null && carsTableList.size() > 0){
            if(TextUtils.isEmpty(CacheHelper.getVin())){
                CacheHelper.setUsualcar(carsTableList.get(0));
                mBinding.carLisenceNoTv.setText(carNoList.get(0));
            }
        }


    }


    /**
     * 初始化页面点击事件
     */
    private void initListener() {
    }
}
