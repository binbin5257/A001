package cn.lds.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.data.CarInfoShowConfigModel;
import cn.lds.common.data.ConditionReportModel;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.databinding.ActivityCarInfoConfigBinding;

/**
 * 车辆信息显示设置
 * Created by sibinbin on 18-3-26.
 */

public class CarInfoShowConfigActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backBtn;

    private List<CarInfoShowConfigModel> mList = new ArrayList<>();
    private List<CarInfoShowConfigModel> models;
    private ActivityCarInfoConfigBinding mBinding;
    private CarInfoShowConfigAdapter adapter;
    private int seleceIndex = 0;
    private String configType;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_car_info_config);
        TextView title = mBinding.getRoot().findViewById(R.id.top_title_tv);
        title.setText("车辆信息显示设置");
        backBtn = mBinding.getRoot().findViewById(R.id.top_back_iv);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            ConditionReportModel.DataBean dataBean = (ConditionReportModel.DataBean) bundle.getSerializable("data");
            int position = bundle.getInt("position");
            if(dataBean != null){
                int fuelType = CacheHelper.getUsualcar().getFuelType();
                switch (fuelType) {
                    case 2://EV车
                        models = creatEvCarListData(dataBean,position);
                        break;
                    default://油车
                        models = creatOilListData(dataBean,position);
                        break;
                }
            }
        }
        adapter = new CarInfoShowConfigAdapter(this);
        mBinding.listView.setAdapter(adapter);

    }

    private List<CarInfoShowConfigModel> creatOilListData( ConditionReportModel.DataBean dataBean, int position ) {
        CarInfoShowConfigModel mileageModel = new CarInfoShowConfigModel();
        mileageModel.setName("总里程");
        mileageModel.setValue(dataBean.getTotalMileage() / 10 + "km");

        CarInfoShowConfigModel averageModel = new CarInfoShowConfigModel();
        averageModel.setName("平均油耗");
        averageModel.setValue(Math.abs(Math.abs(dataBean.getTripAAvergeFuleCon() / 10)) + "L/km");


        CarInfoShowConfigModel instantaneousModel = new CarInfoShowConfigModel();
        instantaneousModel.setName("瞬时油耗");
        instantaneousModel.setValue(Math.abs(dataBean.getInstantaneFuleCon() / 10) + "L/km");

        CarInfoShowConfigModel airConditionerModel = new CarInfoShowConfigModel();
        airConditionerModel.setName("空调状态");
        airConditionerModel.setValue("关闭");
        setSelectStatus(position, mileageModel, averageModel, instantaneousModel, airConditionerModel);
        mList.add(mileageModel);
        mList.add(averageModel);
        mList.add(instantaneousModel);
        mList.add(airConditionerModel);
        return mList;
    }

    private List<CarInfoShowConfigModel> creatEvCarListData( ConditionReportModel.DataBean dataBean, int position ) {
        CarInfoShowConfigModel mileageModel = new CarInfoShowConfigModel();
        mileageModel.setName("总里程");
        mileageModel.setValue(dataBean.getTotalMileage() / 10 + "km");

        CarInfoShowConfigModel averageModel = new CarInfoShowConfigModel();
        averageModel.setName("平均功耗");
        averageModel.setValue(Math.abs(dataBean.getAveragePower() / 10) + "W/km");

        CarInfoShowConfigModel instantaneousModel = new CarInfoShowConfigModel();
        instantaneousModel.setName("瞬时功耗");
        instantaneousModel.setValue(Math.abs(dataBean.getInstantanePower() / 10) + "W/km");

        CarInfoShowConfigModel airConditionerModel = new CarInfoShowConfigModel();
        airConditionerModel.setName("空调状态");
        airConditionerModel.setValue("关闭");

        setSelectStatus(position, mileageModel, averageModel, instantaneousModel, airConditionerModel);
        mList.add(mileageModel);
        mList.add(averageModel);
        mList.add(instantaneousModel);
        mList.add(airConditionerModel);

        return mList;
    }

    /**
     * 初始化选中状态
     * @param position
     * @param mileageModel
     * @param averageModel
     * @param instantaneousModel
     * @param airConditionerModel
     */
    private void setSelectStatus( int position, CarInfoShowConfigModel mileageModel, CarInfoShowConfigModel averageModel, CarInfoShowConfigModel instantaneousModel, CarInfoShowConfigModel airConditionerModel ) {
        if(position == 0){
            configType = CacheHelper.getCarInfoShowConfigLeft();
            if(TextUtils.isEmpty(configType)){
                configType = "MILEAGE";
            }
        }else if(position == 1){
            configType = CacheHelper.getCarInfoShowConfigRight();
            if(TextUtils.isEmpty(configType)){
                configType = "AVERAGE";
            }
        }
        switch (configType){
            case "MILEAGE":
                seleceIndex = 0;
                mileageModel.setSelect(true);
                averageModel.setSelect(false);
                instantaneousModel.setSelect(false);
                airConditionerModel.setSelect(false);
                break;
            case "AVERAGE":
                seleceIndex = 1;
                mileageModel.setSelect(false);
                averageModel.setSelect(true);
                instantaneousModel.setSelect(false);
                airConditionerModel.setSelect(false);
                break;
            case "INSTANTANEOUS":
                seleceIndex = 2;
                mileageModel.setSelect(false);
                averageModel.setSelect(false);
                instantaneousModel.setSelect(true);
                airConditionerModel.setSelect(false);
                break;
            case "AIR_CONDITIONER":
                seleceIndex = 3;
                mileageModel.setSelect(false);
                averageModel.setSelect(false);
                instantaneousModel.setSelect(false);
                airConditionerModel.setSelect(true);
                break;
        }
    }


    @Override
    public void initListener() {
        backBtn.setOnClickListener(this);
        mBinding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                seleceIndex = position;
                CarInfoShowConfigModel model = models.get(position);
                for(int i = 0; i < models.size(); i++){
                    if(i != position && models.get(i).isSelect()){
                        models.get(i).setSelect(false);
                    }
                }
                if(!model.isSelect()){
                    model.setSelect(true);
                    adapter.notifyDataSetChanged();
                }


            }
        });

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

    private void backPre() {
        Intent intent = new Intent();
        if(seleceIndex == 0){
            intent.putExtra("TYPE","MILEAGE");
        }else if(seleceIndex == 1){
            intent.putExtra("TYPE","AVERAGE");
        }else if(seleceIndex == 2){
            intent.putExtra("TYPE","INSTANTANEOUS");

        }else if(seleceIndex == 3){
            intent.putExtra("TYPE","AIR_CONDITIONER");
        }
        setResult(RESULT_OK,intent);
        finish();
    }

    public class CarInfoShowConfigAdapter extends BaseAdapter{

        private Context mContext;
        public CarInfoShowConfigAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return models.size();
        }

        @Override
        public Object getItem( int position ) {
            return models.get(position);
        }

        @Override
        public long getItemId( int position ) {
            return position;
        }

        @Override
        public View getView( int position, View convertView, ViewGroup parent ) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_car_info_show_config,null);
            TextView nameTv = view.findViewById(R.id.name);
            TextView valueTv = view.findViewById(R.id.value);
            CheckBox status = view.findViewById(R.id.status);
            CarInfoShowConfigModel model = models.get(position);
            nameTv.setText(model.getName());
            valueTv.setText(model.getValue());
            status.setChecked(model.isSelect());
            return view;
        }
    }
}
