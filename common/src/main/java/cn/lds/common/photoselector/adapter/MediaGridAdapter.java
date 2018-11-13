package cn.lds.common.photoselector.adapter;


import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import cn.lds.common.R;
import cn.lds.common.photoselector.PickerConfig;
import cn.lds.common.photoselector.entity.Media;
import cn.lds.common.photoselector.utils.FileUtils;
import cn.lds.common.photoselector.utils.ScreenUtils;


/**
 * Created by dmcBig on 2017/7/5.
 */

public class MediaGridAdapter extends RecyclerView.Adapter<MediaGridAdapter.MyViewHolder> {

    ArrayList<Media> medias;
    Context context;
    FileUtils fileUtils = new FileUtils();
    ArrayList<Media> selectMedias = new ArrayList<>();
    long maxSelect, maxSize;

    public MediaGridAdapter(ArrayList<Media> list, Context context, ArrayList<Media> select, int max, long maxSize) {
        if (select != null) {
            this.selectMedias = select;
        }
        this.maxSelect = max;
        this.maxSize = maxSize;
        this.medias = list;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView media_image;
        public ImageView check_image;
        public View mask_view;
        public TextView textView_size;
        public RelativeLayout video_info;

        public MyViewHolder(View view) {
            super(view);
            media_image = (SimpleDraweeView) view.findViewById(R.id.media_image);
            check_image = (ImageView) view.findViewById(R.id.check_image);
            mask_view = view.findViewById(R.id.mask_view);
            video_info = (RelativeLayout) view.findViewById(R.id.video_info);
            textView_size = (TextView) view.findViewById(R.id.textView_size);
            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getItemWidth())); //让图片是个正方形
        }
    }

    int getItemWidth() {
        return (ScreenUtils.getScreenWidth(context) / PickerConfig.GridSpanCount) - PickerConfig.GridSpanCount;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.media_view_item, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Media media = medias.get(position);
        Uri mediaUri = Uri.parse("file://" + media.path);
        holder.media_image.setImageURI(mediaUri);
        if (media.mediaType == 3) {
            holder.video_info.setVisibility(View.VISIBLE);
            holder.textView_size.setText(fileUtils.getSizeByUnit(media.size));
        } else {
            holder.video_info.setVisibility(View.INVISIBLE);
        }

        int isSelect = isSelect(media);
        holder.mask_view.setVisibility(isSelect >= 0 ? View.VISIBLE : View.INVISIBLE);
        holder.check_image.setImageDrawable(isSelect >= 0 ? ContextCompat.getDrawable(context, R.drawable.btn_selected) : ContextCompat.getDrawable(context, R.drawable.btn_unselected));


        holder.media_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int isSelect = isSelect(media);
                if (selectMedias.size() >= maxSelect && isSelect < 0) {
                    Toast.makeText(context, context.getString(R.string.msg_amount_limit), Toast.LENGTH_SHORT).show();
                } else {
                    if (media.size > maxSize) {
                        Toast.makeText(context, context.getString(R.string.msg_size_limit) + (FileUtils.fileSize(maxSize)), Toast.LENGTH_LONG).show();
                    } else {
                        holder.mask_view.setVisibility(isSelect >= 0 ? View.INVISIBLE : View.VISIBLE);
                        holder.check_image.setImageDrawable(isSelect >= 0 ? ContextCompat.getDrawable(context, R.drawable.btn_unselected) : ContextCompat.getDrawable(context, R.drawable.btn_selected));
                        setSelectMedias(media);
                        mOnItemClickListener.onItemClick(v, media, selectMedias);
                    }
                }

            }
        });
    }


    public void setSelectMedias(Media media) {
        int index = isSelect(media);
        if (index == -1) {
            selectMedias.add(media);
        } else {
            selectMedias.remove(index);
        }
    }

    /**
     * @param media
     * @return 大于等于0 就是表示以选择，返回的是在selectMedias中的下标
     */
    public int isSelect(Media media) {
        int is = -1;
        if (selectMedias.size() <= 0) {
            return is;
        }
        for (int i = 0; i < selectMedias.size(); i++) {
            Media m = selectMedias.get(i);
            if (m.path.equals(media.path)) {
                is = i;
                break;
            }
        }
        return is;
    }

    public void updateSelectAdapter(ArrayList<Media> select) {
        if (select != null) {
            this.selectMedias = select;
        }
        notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<Media> list) {
        this.medias = list;
        notifyDataSetChanged();
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public ArrayList<Media> getSelectMedias() {
        return selectMedias;
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Media data, ArrayList<Media> selectMedias);
    }
}
