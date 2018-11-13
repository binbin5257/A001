package cn.lds.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.lds.R;
import cn.lds.common.table.HomeCustomTable;
import cn.lds.common.utils.CacheHelper;

/**
 * Created by sibinbin on 18-4-25.
 */

public class HomeCustomMoreAadapter extends RecyclerView.Adapter<HomeCustomMoreAadapter.ViewHolder> {

    private List<HomeCustomTable> mHomeCustomTables;
    private int fuelType = 2;
    private Context context;

    public HomeCustomMoreAadapter( List<HomeCustomTable> homeCustomTables) {
        this.mHomeCustomTables = homeCustomTables;
        if(CacheHelper.getUsualcar() != null){
            fuelType = CacheHelper.getUsualcar().getFuelType();

        }
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_custom,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, final int position ) {
        holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.line_bg));
        HomeCustomTable table = mHomeCustomTables.get(position);
        switch (fuelType) {
            case 2://EV车
                handleEvData(table,holder.titleTv);
                break;
            default://油车
                handleOilData(table,holder.titleTv);
                break;
        }
        holder.iconIv.setImageResource(R.drawable.ic_add_circle);
        holder.iconLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if(onButtonClickListener != null){
                    onButtonClickListener.addShowItem(position);
                }
            }
        });

    }

    private void handleOilData( HomeCustomTable table,TextView titleTv) {
        switch (table.getName()){
            case "MILEAGE":
                titleTv.setText("总里程");
                break;
            case "AVERAGE":
                titleTv.setText("平均油耗");
                break;
            case "INSTANTANEOUS":
                titleTv.setText("瞬时油耗");
                break;
            case "AIR_CONDITIONER":
                titleTv.setText("空调状态");
                break;
            case "STATUS_TRUNK":
                titleTv.setText("后备箱状态");
                break;
            case "STATUS_LOCKED":
                titleTv.setText("车锁状态");
                break;
        }
    }

    /**
     *
     * @param table
     */
    private void handleEvData( HomeCustomTable table,TextView titleTv ) {
        switch (table.getName()){
            case "MILEAGE":
                titleTv.setText("总里程");
                break;
            case "AVERAGE":
                titleTv.setText("平均功率");
                break;
            case "INSTANTANEOUS":
                titleTv.setText("瞬时功率");
                break;
            case "AIR_CONDITIONER":
                titleTv.setText("空调状态");
                break;
            case "STATUS_TRUNK":
                titleTv.setText("后备箱状态");
                break;
            case "STATUS_LOCKED":
                titleTv.setText("车锁状态");
                break;
        }
    }
    @Override
    public int getItemCount() {
        return mHomeCustomTables.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iconIv;
        TextView titleTv;
        TextView contentTv;
        LinearLayout iconLl;

        public ViewHolder(View view) {
            super(view);
            iconIv = (ImageView) view.findViewById(R.id.iv_icon);
            iconLl = (LinearLayout) view.findViewById(R.id.ll_icon);
            titleTv = (TextView) view.findViewById(R.id.title);
            contentTv = (TextView) view.findViewById(R.id.content);
        }
    }

    private OnButtonClickListener onButtonClickListener;

    public void setOnButtonClickListener( OnButtonClickListener onButtonClickListener ) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public interface OnButtonClickListener{
        void addShowItem( int position );
    }


}
