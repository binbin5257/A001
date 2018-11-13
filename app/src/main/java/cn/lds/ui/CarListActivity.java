package cn.lds.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.manager.UMengManager;
import cn.lds.common.table.CarsTable;
import cn.lds.common.table.base.DBManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.databinding.ActivityCarListBinding;
import cn.lds.ui.adapter.CarListAdapter;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 车辆管理列表界面
 */
public class CarListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ActivityCarListBinding mBinding;
    private RealmResults<CarsTable> carsTables;
    private CarListAdapter carListAdapter;
    private Realm realm;
    private ImageView backIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_car_list);
        UMengManager.getInstance().onResumePage("carManagement");

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        initListener();
    }

    @Override
    public void initView() {
        TextView titile = mBinding.getRoot().findViewById(R.id.top_title_tv);
        backIv = findViewById(R.id.top_back_iv);
        titile.setText("车辆管理");

        realm = DBManager.getInstance().getRealm();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                carsTables = realm.where(CarsTable.class).findAll();
            }
        });
        carListAdapter = new CarListAdapter(mContext, carsTables);
        mBinding.carList.setAdapter(carListAdapter);

    }
    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        backIv.setOnClickListener(this);
        mBinding.carList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.top_back_iv:
                backMeFragment();
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            backMeFragment();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMengManager.getInstance().onPausePage("carManagement");



    }

    private void backMeFragment() {
        String vin = CacheHelper.getVin();
        if(carsTables != null && carsTables.size() > 0){
            for(CarsTable table:carsTables){
                if(!TextUtils.isEmpty(table.getVin())&& table.getVin().equals(vin)){
                    Intent intent = new Intent();
                    intent.putExtra("no",table.getLicensePlate());
                    intent.putExtra("mode",table.getMode());
                    setResult(RESULT_OK,intent);
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        CarsTable table = carsTables.get(position);
//        if(table != null){
//            Intent intent = new Intent(this,CarDetailActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("CAR_MODE",table.getMode());
//            bundle.putString("CAR_LICENSEPLATE",table.getLicensePlate());
//            bundle.putString("CAR_VIN",table.getVin());
//            bundle.putString("CAR_IMAGE_ID",table.getImage());
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }

    }
}
