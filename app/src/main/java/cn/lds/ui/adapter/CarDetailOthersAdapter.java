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
import cn.lds.common.utils.OnItemClickListener;


/**
 * 首页，为你推荐列表
 * Created by leadingsoft on 2017/7/5.
 */

public class CarDetailOthersAdapter extends RecyclerView.Adapter<CarDetailOthersAdapter.Viewholder> {

    List<String> others;
    List<Integer> mPicList;
    Context context;
    OnItemClickListener itemClickListener;


    public CarDetailOthersAdapter(List<String> list, List<Integer> picList,Context context, OnItemClickListener itemClickListener) {
        this.others = list;
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.mPicList = picList;
    }


    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_detail_others, parent, false);
        Viewholder holder = new Viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Viewholder holder, final int position) {
        final String text = others.get(position);
        holder.title.setText(text);
        holder.iv.setImageResource(mPicList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(text, position);
            }
        });
    }

    public void updateAdapter(ArrayList<String> list) {
        this.others = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return others.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView iv;
        private TextView title;

        private Viewholder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.item_detail_other_iv);
            title = itemView.findViewById(R.id.item_detail_other_tx);
        }
    }


}
