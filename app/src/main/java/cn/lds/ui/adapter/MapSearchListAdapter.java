package cn.lds.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.amap.api.services.core.PoiItem;

import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.model.FixedViewInfo;
import cn.lds.common.data.CollectionsModel;
import cn.lds.common.manager.CarControlManager;
import cn.lds.common.utils.OnItemClickListener;
import cn.lds.common.utils.OnItemLongClickListener;
import cn.lds.widget.dialog.LoadingDialogUtils;


/**
 * 地图搜索页面list适配器
 * Created by leadingsoft on 2017/7/5.
 */

public class MapSearchListAdapter extends RecyclerView.Adapter<MapSearchListAdapter.Viewholder> {

    List<PoiItem> poiItems;
    Context context;
    OnItemClickListener itemClickListener;
    OnItemLongClickListener itemLongClickListener;
    private ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<>();


    public MapSearchListAdapter(List<PoiItem> list, Context context, OnItemClickListener itemClickListener, OnItemLongClickListener longClickListener) {
        this.poiItems = list;
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = longClickListener;
    }


    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mapsearch_list, parent, false);
        Viewholder holder = new Viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Viewholder holder, final int position) {
        final PoiItem poiItem = poiItems.get(position);


        holder.name.setText(poiItem.getTitle());
        holder.address.setText(poiItem.getSnippet());

        if (null != itemClickListener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(poiItem, position);
                }
            });
        }
        if (null != itemLongClickListener) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return itemLongClickListener.onItemLongClick(poiItem, position);
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

    //添加分页数据
    public void add(List<PoiItem> list) {
        poiItems.addAll(list);
        notifyDataSetChanged();
    }

    //移除指定数据
    public void remove(int position) {
        poiItems.remove(position);
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
        private ImageView postPoi;
        private TextView name;
        private TextView address;
        private CheckBox checkBox;

        private Viewholder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.map_search_poi_name);
            address = itemView.findViewById(R.id.map_search_poi_address);
            checkBox = itemView.findViewById(R.id.map_search_collect);
            postPoi = itemView.findViewById(R.id.map_search_post_poi);
        }
    }

    public void clear() {
        poiItems.clear();
    }
}
