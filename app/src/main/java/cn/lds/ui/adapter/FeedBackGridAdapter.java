package cn.lds.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.lds.R;
import cn.lds.common.api.ModuleUrls;
import cn.lds.ui.select_image.MainConstant;
import cn.lds.ui.view.GlideRoundTransform;

/**
 * Created by sibinbin on 18-3-21.
 */

public class FeedBackGridAdapter extends BaseAdapter {


    private Context mContext;
    private List<String> mList;
    private final LayoutInflater inflater;
    private final RequestOptions myOptions;

    public FeedBackGridAdapter( Context context,List<String> pictures){
        this.mContext = context;
        this.mList = pictures;
        inflater = LayoutInflater.from(mContext);
        myOptions = new RequestOptions()
                .transform(new GlideRoundTransform(mContext,2));
    }
    @Override
    public int getCount() {
        int count = mList == null ? 1 : mList.size() + 1;
        if (count > MainConstant.MAX_SELECT_PIC_NUM) {
            return mList.size();
        } else {
            return count;
        }
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
        convertView = inflater.inflate(R.layout.item_feed_back, null);
        SimpleDraweeView icon = convertView.findViewById(R.id.icon);
        LinearLayout addPic = convertView.findViewById(R.id.ll_add_pic);
        if (position < mList.size()) {
            //代表+号之前的需要正常显示图片
            if (addPic.getVisibility() == View.VISIBLE) {
                addPic.setVisibility(View.GONE);
            }
                String picUrl = mList.get(position); //图片路径
                icon.setImageURI(picUrl);
            } else {
                if(addPic.getVisibility() == View.GONE){
                    addPic.setVisibility(View.VISIBLE);
                }
            }
        return convertView;
    }

//        ViewHolder holder;
//        if(convertView == null){
//            convertView = inflater.inflate(R.layout.item_feed_back,null);
//            holder = new ViewHolder();
//            holder.icon = convertView.findViewById(R.id.icon);
//            holder.addPic = convertView.findViewById(R.id.ll_add_pic);
//            convertView.setTag(holder);
//        }else{
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        if (position < mList.size()) {
//            if( holder.addPic.getVisibility() == View.VISIBLE){
//                holder.addPic.setVisibility(View.GONE);
//            }
//
//            //代表+号之前的需要正常显示图片
//            String picUrl = mList.get(position); //图片路径
//            holder.icon.setImageBitmap(getBitmap(picUrl));
//
////            Glide.with(mContext).load(picUrl).apply(myOptions).into(holder.icon);
//        } else {
////            holder.addPic.setVisibility(View.VISIBLE);
//            if( holder.addPic.getVisibility() == View.GONE){
//                holder.addPic.setVisibility(View.VISIBLE);
//            }
////            holder.icon.setImageResource(R.drawable.btn_add_pic);//最后一个显示加号图片
//        }
//        return convertView;
//    }
//    public class ViewHolder{
//        private SimpleDraweeView icon;
//        private LinearLayout addPic;
//    }

    public Bitmap getBitmap(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        return BitmapFactory.decodeFile(path, options);

    }
}
