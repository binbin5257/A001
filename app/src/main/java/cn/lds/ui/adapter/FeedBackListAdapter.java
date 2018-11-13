package cn.lds.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.data.FeedBackListModel;
import cn.lds.common.manager.ConfigManager;
import cn.lds.common.manager.ImageManager;
import cn.lds.common.photoselector.entity.Media;
import cn.lds.common.utils.TimeHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.ui.view.MyGridView;
import io.realm.RealmResults;

/**
 * 意见反馈列表适配器
 * Created by leadingsoft on 17/12/5.
 */

public class FeedBackListAdapter extends BaseAdapter {
    private List<FeedBackListModel.DataBean> dataBeans;
    private Context mContext;

    public FeedBackListAdapter(Context mContext, List<FeedBackListModel.DataBean> carsTables) {
        this.dataBeans = carsTables;
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
        return dataBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return dataBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        FeedBackListModel.DataBean dataBean = dataBeans.get(i);
        ViewHodler hodler;
        if (null == view) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_feedback_list_layout, null);
            hodler = new ViewHodler();
            hodler.content = view.findViewById(R.id.item_feedback_content);
            hodler.createDate = view.findViewById(R.id.item_feedback_createdate);
            hodler.tspContent = view.findViewById(R.id.item_feedback_tspcontent);
            view.setTag(hodler);
        } else {
            hodler = (ViewHodler) view.getTag();
        }
        hodler.content.setText(dataBean.getContent());
        hodler.createDate.setText(TimeHelper.getTimeByType(dataBean.getCreatedDate(), TimeHelper.FORMAT7));
        if(TextUtils.isEmpty(dataBean.getTspContent())){
            hodler.tspContent.setText("受理中");
        }else{
            hodler.tspContent.setText("已回复");
        }

        List<String> list = dataBean.getPictures();
        if (null == list || list.isEmpty()) {
            return view;
        } else {
            for (int j = 0; j < list.size(); j++) {
                String no = list.get(j);
                String url = ModuleUrls.downloadFile;
                url = String.format("%s/%s", ConfigManager.getInstance().getBaseUrl(), url.replace("{no}", no));
                Uri uri = Uri.parse(url);
//                switch (j) {
//                    case 0:
//                        hodler.pic1.setImageURI(uri);
//                        break;
//                    case 1:
//                        hodler.pic2.setImageURI(uri);
//                        break;
//                    case 2:
//                        hodler.pic3.setImageURI(uri);
//                        break;
//                    case 3:
//                        hodler.pic4.setImageURI(uri);
//                        break;
//                }
            }
        }
        return view;
    }


    private class ViewHodler {
        TextView content;
        TextView createDate;
        TextView tspContent;
//        SimpleDraweeView pic1;
//        SimpleDraweeView pic2;
//        SimpleDraweeView pic3;
//        SimpleDraweeView pic4;
    }

    public void setDataBeans(List<FeedBackListModel.DataBean> dataBeans) {
        this.dataBeans = dataBeans;
        this.notifyDataSetChanged();
    }
}
