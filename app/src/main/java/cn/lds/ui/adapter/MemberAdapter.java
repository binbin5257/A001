package cn.lds.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.lds.R;
import cn.lds.common.table.People;

/**
 * Created by leadingsoft on 17/12/5.
 */

public class MemberAdapter extends BaseAdapter {
    private List<People> list;
    private Context mContext;

    public MemberAdapter(Context mContext, List<People> list) {
        this.list = list;
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        People people = list.get(i);
        ViewHodler hodler;
        if (null == view) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_member, null);
            hodler = new ViewHodler();
            hodler.name = view.findViewById(R.id.item_member_name);
            hodler.favorate = view.findViewById(R.id.item_member_favorate);
            view.setTag(hodler);
        } else {
            hodler = (ViewHodler) view.getTag();
        }
        hodler.name.setText(people.getName());
        hodler.favorate.setText(people.getFavorateDog());
        return view;
    }


    private class ViewHodler {
        TextView name;
        TextView favorate;
    }

}
