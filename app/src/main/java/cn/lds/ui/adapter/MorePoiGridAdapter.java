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
 * 更多grid适配器
 * Created by leadingsoft on 2017/7/5.
 */

public class MorePoiGridAdapter extends RecyclerView.Adapter<MorePoiGridAdapter.Viewholder> {

    List<String> controls;
    Context context;
    OnItemClickListener itemClickListener;


    public MorePoiGridAdapter(List<String> list, Context context, OnItemClickListener itemClickListener) {
        this.controls = list;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }


    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_poi_more_list, parent, false);
        Viewholder holder = new Viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Viewholder holder, final int position) {
        final String title = controls.get(position);
        holder.title.setText(title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(title, position);
            }
        });
    }

    public void updateAdapter(ArrayList<String> list) {
        this.controls = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return controls.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView title;

        private Viewholder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_poi_more_list);
        }
    }


}
