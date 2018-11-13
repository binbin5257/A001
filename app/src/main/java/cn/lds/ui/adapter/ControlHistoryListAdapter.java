package cn.lds.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.lds.R;
import cn.lds.common.data.ControlHistoryListModel;
import cn.lds.common.utils.TimeHelper;

/**
 * Created by leadingsoft on 17/12/5.
 * tsp操作历史记录适配器
 */

public class ControlHistoryListAdapter extends BaseAdapter {
    private List<ControlHistoryListModel.DataBean> controlList;
    private Context mContext;

    public ControlHistoryListAdapter(Context mContext, List<ControlHistoryListModel.DataBean> controlList) {
        this.controlList = controlList;
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
        return controlList.size();
    }

    @Override
    public Object getItem(int i) {
        return controlList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        ControlHistoryListModel.DataBean dataBean = controlList.get(i);
//        ViewHodler hodler;
//        if (null == view) {
//            view = LayoutInflater.from(mContext).inflate(R.layout.item_control_history_list, null);
//            hodler = new ViewHodler();
//            hodler.name = view.findViewById(R.id.item_tsp_log_name);
//            hodler.time = view.findViewById(R.id.item_tsp_log_time);
//            hodler.result = view.findViewById(R.id.item_tsp_log_result);
//            view.setTag(hodler);
//        } else {
//            hodler = (ViewHodler) view.getTag();
//        }
//        hodler.name.setText(dataBean.getCommandType().getValue());
//        hodler.time.setText(TimeHelper.getTimeByType(dataBean.getLastUpdateTime(), TimeHelper.FORMAT1));
//        hodler.result.setText(dataBean.getCommandResult().getValue());
        return view;
    }


    private class ViewHodler {
        TextView name;
        TextView result;
        TextView time;
    }

    public void setControlList(List<ControlHistoryListModel.DataBean> controlList) {
        this.controlList = controlList;
        notifyDataSetChanged();
    }
}
