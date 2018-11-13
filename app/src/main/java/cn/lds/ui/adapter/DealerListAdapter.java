package cn.lds.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.data.CollectionsModel;
import cn.lds.common.data.DealerListModel;
import cn.lds.common.data.postbody.PoiPostBean;
import cn.lds.common.manager.CarControlManager;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.OnItemClickListener;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.widget.dialog.LoadingDialogUtils;


/**
 * 经销商列表 适配器
 * Created by leadingsoft on 2017/7/5.
 */

public class DealerListAdapter extends RecyclerView.Adapter<DealerListAdapter.Viewholder> {

    List<DealerListModel.DataBean> controls;
    Context context;
    OnItemClickListener itemClickListener;


    public DealerListAdapter(List<DealerListModel.DataBean> list, Context context, OnItemClickListener itemClickListener) {
        this.controls = list;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }


    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_poi_list, parent, false);
        Viewholder holder = new Viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Viewholder holder, final int position) {
        final DealerListModel.DataBean dataBean = controls.get(position);
        holder.name.setText(dataBean.getDealerName());
        holder.address.setText(dataBean.getAddress());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("距您");
//        if (dataBean.getDistance() >= 1000) {
//            stringBuilder.append(dataBean.getDistance() / 1000).append("km");
//        } else {
//            stringBuilder.append(dataBean.getDistance()).append("m");
//        }
        holder.km.setText("5km");
        holder.postPoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                LoadingDialogUtils.showVertical(context, context.getString(R.string.loading_waitting));
                String url = ModuleUrls.postPoi.replace("{vin}", CacheHelper.getVin());
                PoiPostBean postPoi = new PoiPostBean();
                PoiPostBean.PoiNodeBean poiNodeBean = new PoiPostBean.PoiNodeBean();
                poiNodeBean.setDestinations(dataBean.getDealerCode());
                poiNodeBean.setLatitude(dataBean.getLatitude());
                poiNodeBean.setLongitude(dataBean.getLongitude());
                postPoi.setPoiNode(poiNodeBean);
                String json = GsonImplHelp.get().toJson(postPoi);
                RequestManager.getInstance().post(url, HttpApiKey.postPoi, json);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(dataBean, position);
            }
        });
    }

    public void updateAdapter(List<DealerListModel.DataBean> list) {
        this.controls = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return controls.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView address;
        private TextView km;
        private TextView postPoi;

        private Viewholder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.poi_name);
            address = itemView.findViewById(R.id.poi_address);
            km = itemView.findViewById(R.id.poi_km);
            postPoi = itemView.findViewById(R.id.poi_located_post_poi);
        }
    }


}
