package cn.lds.ui.adapter;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;


import com.amap.api.services.core.PoiItem;

import java.util.Arrays;
import java.util.List;

import cn.lds.R;
import cn.lds.common.data.CollectionsModel;
import cn.lds.common.manager.AccountManager;
import cn.lds.common.manager.CarControlManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.OnItemClickListener;
import cn.lds.ui.PoiLocatedActivity;
import cn.lds.widget.dialog.CenterListDialog;
import cn.lds.widget.dialog.CommonInputDialog;
import cn.lds.widget.dialog.LoadingDialogUtils;
import cn.lds.widget.dialog.callback.OnDialogClickListener;
import cn.lds.widget.dialog.callback.OnDialogOnItemClickListener;


/**
 * 收藏夹适配器
 * Created by leadingsoft on 2017/7/5.
 */

public class CollectiosAdapter extends RecyclerView.Adapter<CollectiosAdapter.ViewHolder> implements OnItemClickListener {

    List<CollectionsModel.DataBean> dataBeans;
    Context context;
    private Activity mActivity;
    private CenterListDialog centerListDialog;
    private CollectionsModel.DataBean selectBean;


    public CollectiosAdapter(List<CollectionsModel.DataBean> list, Context context,Activity act) {
        this.dataBeans = list;
        this.context = context;
        this.mActivity = act;
        initSettingDialog(context);
    }

    /**
     * 初始化设置对话框
     * @param context
     */
    private void initSettingDialog( Context context ) {
        centerListDialog = new CenterListDialog(mActivity,context, Arrays.asList("设为家","设为公司","编辑","删除")).setOnDialogOnItemClickListener(new OnDialogOnItemClickListener() {
            @Override
            public void onDialogItemClick( Dialog dialog, int position ) {
                dialog.dismiss();
                switch (position){
                    case 0: //设置为家
                        setHome(selectBean);
                        break;
                    case 1: //设置为公司
                        setCompy(selectBean);
                        break;
                    case 2: //编辑收藏
                        editCollection(selectBean);
                        break;
                    case 3: //取消收藏
                        deleteCollection(selectBean);
                        break;
                }
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collections_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CollectionsModel.DataBean dataBean = dataBeans.get(position);
        if(!TextUtils.isEmpty(dataBean.getTypeCode())){
            if("1".equals(dataBean.getTypeCode())){
                holder.collectionsName.setText(dataBean.getName() + "(家)");
            }else if("2".equals(dataBean.getTypeCode())){
                holder.collectionsName.setText(dataBean.getName() + "(公司)");
            }
        }else{
            holder.collectionsName.setText(dataBean.getName());
        }
        holder.collectionsAddress.setText(dataBean.getAddress());
        holder.postPoiIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                LoadingDialogUtils.showVertical(context, context.getString(R.string.loading_waitting));
                CarControlManager.getInstance().postPoi(dataBean);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
//                Intent intent = new Intent(context, PoiLocatedActivity.class);
//                PoiItem item = new PoiItem();
//                item.setPoiId(dataBean.getCollectId());
//                item.setLatitude(dataBean.getLatitude());
//                item.setLongitude(dataBean.getLongitude());
//                item.setAdname(dataBean.getName());
//                item.setAddress(dataBean.getAddress());
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("poiItem",item);
//                intent.putExtras(bundle);
//
//                intent.setAction(PoiLocatedActivity.ACTIONSINGLEPOI);
//                context.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //TODO 弹出对话框,设置为家,公司,取消收藏
                selectBean = dataBeans.get(position);
                if(!TextUtils.isEmpty(selectBean.getTypeCode())){
                    //显示取消删除对话框
                    showCancelAndDelDialog(selectBean.getTypeCode());
                }else{
                    showSettingDialog();
                }

                return true;
            }

        });
    }

    /**
     * 取消删除对话框
     * @param typeCode 1 家 , 2 公司
     */
    private void showCancelAndDelDialog( final String typeCode) {
        List<String> list = null;
        if("1".equals(typeCode)){
            list = Arrays.asList("取消设置为家","编辑", "删除");
        }else if("2".equals(typeCode)){
            list = Arrays.asList("取消设置为公司","编辑", "删除");
        }

        CenterListDialog cancelAndDel = new CenterListDialog(mActivity,context,list);
        cancelAndDel.setOnDialogOnItemClickListener(new OnDialogOnItemClickListener() {
            @Override
            public void onDialogItemClick( Dialog dialog, int position ) {
                if(position == 0){
                    cancelSetHomeAndCompy(selectBean);
                }else if(position == 1) {
                    editCollection(selectBean);
                }else if(position == 2){
                    deleteCollection(selectBean);
                }
                dialog.dismiss();

            }
        });
        cancelAndDel.show();
    }

    /**
     * 编辑收藏名称
     * @param selectBean
     */
    private void editCollection( final CollectionsModel.DataBean selectBean ) {
        CommonInputDialog inputDialog = new CommonInputDialog(mActivity,"修改POI名称");
        inputDialog.setOnSubmitInputListener(new CommonInputDialog.OnSubmitInputListener() {
            @Override
            public void onSubmitInput( String content ) {
                LoadingDialogUtils.showVertical(context, context.getString(R.string.loading_waitting));
                CarControlManager.getInstance().modifyCollected(content,selectBean.getCollectId());
            }

            @Override
            public void onCancelDialog() {

            }
        });
        inputDialog.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        inputDialog.setContent(selectBean.getName());
        inputDialog.show();

    }

    /**
     * 显示设置对话框
     */
    private void showSettingDialog() {
        centerListDialog.show();
    }
    /**
     * 设置为家
     * @param dataBean
     */
    private void setHome( CollectionsModel.DataBean dataBean ) {
        LoadingDialogUtils.showVertical(context, context.getString(R.string.loading_waitting));
        CarControlManager.getInstance().setHomeAndCompany(dataBean.getCollectId(), CacheHelper.getVin(),1);
        notifyDataSetChanged();
    }

    /**
     * 设置为公司
     * @param dataBean
     */
    private void setCompy( CollectionsModel.DataBean dataBean ) {
        LoadingDialogUtils.showVertical(context, context.getString(R.string.loading_waitting));
        CarControlManager.getInstance().setHomeAndCompany(dataBean.getCollectId(), CacheHelper.getVin(),2);
    }
    /**
     * 取消设置为家或公司
     * @param dataBean
     */
    private void cancelSetHomeAndCompy( CollectionsModel.DataBean dataBean ) {
        LoadingDialogUtils.showVertical(context, context.getString(R.string.loading_waitting));
        CarControlManager.getInstance().cancelSetHomeAndCompy(dataBean.getCollectId());
    }
    /**
     * 删除收藏
     * @param dataBean
     */
    private void deleteCollection( CollectionsModel.DataBean dataBean ) {
        LoadingDialogUtils.showVertical(context, context.getString(R.string.loading_waitting));
        CarControlManager.getInstance().deleCollection(dataBean.getCollectId());
    }

    public void updateAdapter(List<CollectionsModel.DataBean> list) {
        this.dataBeans = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return dataBeans.size();
    }

    @Override
    public void onItemClick(Object data, int position) {
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView collectionsName;
        public TextView collectionsAddress;
        public ImageView postPoiIv;

        public ViewHolder(View itemView) {
            super(itemView);
            collectionsName = itemView.findViewById(R.id.item_collections_name);
            collectionsAddress = itemView.findViewById(R.id.item_collections_address);
            postPoiIv = itemView.findViewById(R.id.iv_post_poi);

        }
    }
}
