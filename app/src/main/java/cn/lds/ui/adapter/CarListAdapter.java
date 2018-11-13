package cn.lds.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.lds.R;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.data.UpdateCarInfo;
import cn.lds.common.manager.ConfigManager;
import cn.lds.common.manager.ImageManager;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.table.CarsTable;
import cn.lds.common.table.People;
import cn.lds.common.utils.CacheHelper;
import cn.lds.ui.CarDetailActivity;
import cn.lds.widget.captcha.Utils;
import io.realm.RealmResults;

/**
 * Created by leadingsoft on 17/12/5.
 */

public class CarListAdapter extends BaseAdapter {
    private RealmResults<CarsTable> carsTables;
    private Context mContext;
    private final Drawable defaultSetting;
    private final Drawable unDefaultSetting;

    public CarListAdapter(Context mContext, RealmResults<CarsTable> carsTables) {
        this.carsTables = carsTables;
        this.mContext = mContext;
        defaultSetting = mContext.getResources().getDrawable(
                R.drawable.bg_selected_car_default);
        unDefaultSetting = mContext.getResources().getDrawable(
                R.drawable.bg_unselect_car);
        defaultSetting.setBounds(0, 0, defaultSetting.getMinimumWidth(), defaultSetting.getMinimumHeight());
        unDefaultSetting.setBounds(0, 0, unDefaultSetting.getMinimumWidth(), unDefaultSetting.getMinimumHeight());
    }


    @Override
    public int getCount() {
        return carsTables.size();
    }

    @Override
    public Object getItem(int i) {
        return carsTables.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final CarsTable people = carsTables.get(i);
        ViewHodler hodler;
        if (null == view) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_carlist, null);
            hodler = new ViewHodler();
            hodler.no = view.findViewById(R.id.car_lisence_no);
            hodler.model = view.findViewById(R.id.car_model);
            hodler.vin = view.findViewById(R.id.car_vin);
            hodler.carIv = view.findViewById(R.id.iv_car);
            hodler.isSelect = view.findViewById(R.id.car_is_select);
            hodler.editor = view.findViewById(R.id.car_editor);
            view.setTag(hodler);
        } else {
            hodler = (ViewHodler) view.getTag();
        }
        hodler.no.setText("车牌号：" + people.getLicensePlate());
        if (!TextUtils.isEmpty(people.getMode())) {
            hodler.model.setText(people.getMode());
        }
        hodler.vin.setText(people.getVin());
//        hodler.carIv.setBackgroundResource(R.drawable.bg_car_list_default);
        String requestUrl = String.format("%s/%s", ConfigManager.getInstance().getBaseUrl(), ModuleUrls.downloadFile.replace("{no}",people.getImage()));
        ImageManager.getInstance().loadImage(requestUrl,hodler.carIv);
        hodler.editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if(people != null){
                    Intent intent = new Intent(mContext,CarDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("CAR_MODE",people.getMode());
                    bundle.putString("CAR_LICENSEPLATE",people.getLicensePlate());
                    bundle.putString("CAR_VIN",people.getVin());
                    bundle.putString("CAR_IMAGE_ID",people.getImage());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            }
        });
        if (CacheHelper.getVin().equals(people.getVin())) {
            hodler.isSelect.setText("默认车辆");
            hodler.isSelect.setCompoundDrawables(defaultSetting,null,null,null);
            hodler.isSelect.setCompoundDrawablePadding(Utils.dp2px(mContext,10));
        } else {
            hodler.isSelect.setText("设为默认");
            hodler.isSelect.setCompoundDrawables(unDefaultSetting,null,null,null);
            hodler.isSelect.setCompoundDrawablePadding(Utils.dp2px(mContext,10));
            hodler.isSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CacheHelper.setVin(people.getVin());
                    //更新数据车辆信息
                    notifyDataSetChanged();
                    EventBus.getDefault().post(new UpdateCarInfo());

                }
            });
        }
        return view;
    }


    private class ViewHodler {
        TextView no;
        TextView model;
        TextView vin;
        TextView isSelect;
        TextView editor;
        SimpleDraweeView carIv;
    }

    private OnCarSelectedListener onCarSelectedListener;

    public void setOnCarSelectedListener( OnCarSelectedListener onCarSelectedListener ) {
        this.onCarSelectedListener = onCarSelectedListener;
    }

    public interface OnCarSelectedListener{
        void onCarSelcetFinish(String vin);
    }

}
