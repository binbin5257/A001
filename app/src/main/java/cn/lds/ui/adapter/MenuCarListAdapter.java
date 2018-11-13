package cn.lds.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.lds.R;
import cn.lds.common.table.CarsTable;
import cn.lds.common.utils.CacheHelper;

/**
 * Created by sibinbin on 18-3-13.
 */

public class MenuCarListAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mList;
    private final LayoutInflater inflater;
    List<CarsTable> mCarsTableList;
    private final String mVin;

    public MenuCarListAdapter( Context context, List<String> list, List<CarsTable> carsTableList ){
        this.mContext = context;
        this.mList = list;
        this.mCarsTableList = carsTableList;
        inflater = LayoutInflater.from(mContext);
        mVin = CacheHelper.getVin();
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem( int position ) {
        return mList.get(position);
    }

    @Override
    public long getItemId( int position ) {
        return position;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        View view = inflater.inflate(R.layout.item_menu_car_list,null);
        TextView content = view.findViewById(R.id.content);
        ImageView selectedIv = view.findViewById(R.id.iv_selected);
        View line = view.findViewById(R.id.line);
        content.setText(mList.get(position));
        if(mCarsTableList.get(position).getVin().equals(mVin)){
            content.setTextColor(Color.parseColor("#00C4CF"));
            selectedIv.setVisibility(View.VISIBLE);
        }else{
            content.setTextColor(Color.parseColor("#446583"));
            selectedIv.setVisibility(View.GONE);
        }
        if(position == mList.size() - 1){
            line.setVisibility(View.INVISIBLE);
        }else{
            line.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
