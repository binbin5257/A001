package cn.lds.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.InputStream;
import java.util.List;

import cn.lds.R;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.manager.ImageManager;
import cn.lds.common.manager.RequestManager;
import cn.lds.ui.MainActivity;
import cn.lds.ui.view.GlideRoundTransform;
import okhttp3.HttpUrl;

/**
 * Created by sibinbin on 18-3-21.
 */

public class FeedBackDetaiGridAdapter extends BaseAdapter {


    private Context mContext;
    private List<String> mList;
    private final LayoutInflater inflater;

    public FeedBackDetaiGridAdapter(Context context, List<String> pictures){
        this.mContext = context;
        this.mList = pictures;
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
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_feed_back_detail,null);
            holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.icon.setImageURI(ModuleUrls.displayFile + mList.get(position));
        return convertView;
    }
    public class ViewHolder{
        private SimpleDraweeView icon;
    }
}
