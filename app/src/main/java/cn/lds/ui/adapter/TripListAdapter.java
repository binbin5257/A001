package cn.lds.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cn.lds.R;
import cn.lds.common.data.TripListModel;
import cn.lds.common.table.CarsTable;
import cn.lds.common.utils.OnItemClickListener;
import cn.lds.common.utils.TimeHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.ui.view.group.BaseRecyclerAdapter;
import cn.lds.ui.view.group.GroupRecyclerAdapter;
import io.realm.RealmResults;

/**
 * Created by leadingsoft on 17/12/5.
 */

public class TripListAdapter extends GroupRecyclerAdapter<String, TripListModel.DataBean> {


    public TripListAdapter(Context context) {
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        TripListHolder holder = new TripListHolder(mInflater.inflate(R.layout.item_triplist, parent, false));
        return holder;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, TripListModel.DataBean item, int position) {
        TripListHolder h = (TripListHolder) holder;
        h.starAddress.setText(item.getStartAddress());
        h.endAddress.setText(item.getEndAddress());
        String startTime = TimeHelper.getTimeByType(item.getCreateTime(), TimeHelper.FORMAT4);
        String endTime = TimeHelper.getTimeByType(item.getModifyTime(), TimeHelper.FORMAT4);
        h.tripTime.setText(startTime + "-" + endTime);
    }

    private static class TripListHolder extends RecyclerView.ViewHolder {

        private TextView starAddress;
        private TextView endAddress;
        private TextView tripTime;

        private TripListHolder(View itemView) {
            super(itemView);
            starAddress = itemView.findViewById(R.id.start_address);
            endAddress = itemView.findViewById(R.id.end_address);
            tripTime = itemView.findViewById(R.id.trip_time);
        }
    }

    public void updateAdapter(List<TripListModel.DataBean> dataBeans) {
        LinkedHashMap<String, List<TripListModel.DataBean>> map = new LinkedHashMap<>();
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < dataBeans.size(); i++) {
            TripListModel.DataBean dataBean = dataBeans.get(i);
            String time = TimeHelper.getTimeByType(dataBean.getCreateTime(), TimeHelper.FORMAT3);
            if (map.containsKey(time)) {
                map.get(time).add(dataBean);
            } else {
                List<TripListModel.DataBean> list = new ArrayList<>();
                list.add(dataBean);
                map.put(time, list);
                titles.add(time);
            }
        }

        resetGroups(map, titles);
    }




}