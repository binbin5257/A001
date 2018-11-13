package cn.lds.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;

import java.util.List;

import cn.lds.R;

/**
 * Created by sibinbin on 18-4-17.
 */

public class MapSearchListAdapterNew extends BaseAdapter {

    private Context mContext;
    private List<PoiItem> mList;
    private final LayoutInflater inflater;

    public MapSearchListAdapterNew( List<PoiItem> list, Context context) {
        this.mContext = context;
        this.mList = list;
        inflater = LayoutInflater.from(mContext);
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
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_mapsearch_list, null);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.map_search_poi_name);
            holder.address = convertView.findViewById(R.id.map_search_poi_address);
            holder.checkBox = convertView.findViewById(R.id.map_search_collect);
            holder.postPoi = convertView.findViewById(R.id.map_search_post_poi);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final PoiItem poiItem = mList.get(position);
        holder.name.setText(poiItem.getTitle());
        holder.address.setText(poiItem.getSnippet());
        return convertView;
    }

    public class ViewHolder{
        private ImageView postPoi;
        private TextView name;
        private TextView address;
        private CheckBox checkBox;
    }
}
