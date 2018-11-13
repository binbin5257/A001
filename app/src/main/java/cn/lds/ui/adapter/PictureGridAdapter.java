package cn.lds.ui.adapter;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.manager.ImageManager;
import cn.lds.common.photoselector.PickerConfig;
import cn.lds.common.photoselector.utils.ScreenUtils;


/**
 * 图片grid适配器
 * Created by leadingsoft on 2017/7/5.
 */

public class PictureGridAdapter extends RecyclerView.Adapter<PictureGridAdapter.PictureHolder> {

    List<String> medias;
    Context context;


    public PictureGridAdapter(List<String> list, Context context) {
        this.medias = list;
        this.context = context;
    }


    int getItemWidth() {
        return (ScreenUtils.getScreenWidth(context) / PickerConfig.GridSpanCount) - PickerConfig.GridSpanCount;
    }


    @Override
    public PictureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_picture_grid, parent, false);
        PictureHolder holder = new PictureHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PictureHolder holder, int position) {
        final String media = medias.get(position);
        if (media.startsWith("file://")) {
            Uri mediaUri = Uri.parse(media);
            holder.media_image.setImageURI(mediaUri);
        } else {
            String url = ModuleUrls.downloadFile;
            url = url.replace("{no}", media);
            ImageManager.getInstance().loadImage(url, holder.media_image);
        }
    }

    public void updateAdapter(ArrayList<String> list) {
        this.medias = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return medias.size();
    }

    public class PictureHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView media_image;

        public PictureHolder(View itemView) {
            super(itemView);
            media_image = itemView.findViewById(R.id.item_picture_view);
            this.itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getItemWidth())); //让图片是个正方形
        }
    }
}
