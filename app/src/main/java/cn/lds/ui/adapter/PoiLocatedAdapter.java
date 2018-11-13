package cn.lds.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;

import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.model.FixedViewInfo;
import cn.lds.common.data.CollectionsModel;
import cn.lds.common.manager.CarControlManager;
import cn.lds.common.utils.OnItemClickListener;
import cn.lds.widget.dialog.LoadingDialogUtils;


/**
 * poi列表，定位界面适配器
 * Created by leadingsoft on 2017/7/5.
 */

public class PoiLocatedAdapter extends RecyclerView.Adapter<PoiLocatedAdapter.Viewholder> {

    List<PoiItem> poiItems;
    Context context;
    OnItemClickListener itemClickListener;
    private ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<>();


    public PoiLocatedAdapter(List<PoiItem> list, Context context, OnItemClickListener itemClickListener) {
        this.poiItems = list;
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
        final PoiItem poiItem = poiItems.get(position);


        holder.name.setText(poiItem.getTitle());
        holder.address.setText(poiItem.getSnippet());

        int distance = poiItem.getDistance();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("距您");
        if (distance >= 1000) {
            stringBuilder.append(distance / 1000).append("km");
        } else {
            stringBuilder.append(distance).append("m");
        }
        holder.distance.setText(stringBuilder.toString());


        if (null != itemClickListener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(poiItem, position);
                }
            });
        }

        holder.postPoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingDialogUtils.showVertical(context, context.getString(R.string.loading_waitting));
                CollectionsModel.DataBean dataBean = new CollectionsModel.DataBean();
                dataBean.setTypeCode(poiItem.getTypeCode());
                dataBean.setTel(poiItem.getTel());
                dataBean.setName(poiItem.getTitle());
                dataBean.setLongitude(poiItem.getLatLonPoint().getLongitude());
                dataBean.setLatitude(poiItem.getLatLonPoint().getLatitude());
                dataBean.setDesc(poiItem.getTypeDes());
                dataBean.setCollectId(poiItem.getPoiId());
                dataBean.setAddress(poiItem.getSnippet());
                CarControlManager.getInstance().postPoi(dataBean);
            }
        });
    }

    public void updateAdapter(List<PoiItem> list) {
        this.poiItems = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mHeaderViewInfos.size() + poiItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return mHeaderViewInfos.get(position).viewType;
        } else {
            return super.getItemViewType(position);
        }

    }

    private boolean isHeaderPosition(int position) {
        return position < mHeaderViewInfos.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView postPoi;
        private TextView name;
        private TextView distance;
        private TextView address;

        private Viewholder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.poi_name);
            distance = itemView.findViewById(R.id.poi_km);
            address = itemView.findViewById(R.id.poi_address);
            postPoi = itemView.findViewById(R.id.poi_located_post_poi);
        }
    }

    public void clear() {
        poiItems.clear();
    }
}
