package cn.lds.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.common.enums.MapSearchGridType;
import cn.lds.common.utils.OnItemClickListener;


/**
 * 地图搜索页面grid适配器
 * Created by leadingsoft on 2017/7/5.
 */

public class MapSearchGridAdapter extends RecyclerView.Adapter<MapSearchGridAdapter.Viewholder> {

    List<MapSearchGridType> controls;
    Context context;
    OnItemClickListener itemClickListener;


    public MapSearchGridAdapter(List<MapSearchGridType> list, Context context, OnItemClickListener itemClickListener) {
        this.controls = list;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }


    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mapsearch_gird, parent, false);
        Viewholder holder = new Viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Viewholder holder, final int position) {
        final MapSearchGridType gridType = controls.get(position);
        holder.title.setText(gridType.getValue());
        switch (gridType) {
            case CHONGDIANZHUANG:
                holder.iv.setImageResource(R.drawable.map_search_cdz);
                break;
            case TINGCHECHANG:
                holder.iv.setImageResource(R.drawable.map_search_tcc);
                break;
            case SHANGCHANG:
                holder.iv.setImageResource(R.drawable.map_search_sc);
                break;
            case JIUDIAN:
                holder.iv.setImageResource(R.drawable.map_search_jd);
                break;
            case SHOUCANGJIA:
                holder.iv.setImageResource(R.drawable.map_search_scj);
                break;
            case JIAYOUZHAN:
                holder.iv.setImageResource(R.drawable.map_search_jyz);
                break;
            case JINGXIAOSHANG:
                holder.iv.setImageResource(R.drawable.map_search_jxs);
                break;
            case GENGDUO:
                holder.iv.setImageResource(R.drawable.map_search_gd);
                break;
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(gridType, position);
            }
        });
    }

    public void updateAdapter(ArrayList<MapSearchGridType> list) {
        this.controls = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return controls.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView iv;
        private TextView title;

        private Viewholder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.item_mapsearch_grid_iv);
            title = itemView.findViewById(R.id.item_mapsearch_grid_tx);
        }
    }


}
