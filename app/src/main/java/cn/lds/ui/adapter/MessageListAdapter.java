package cn.lds.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.lds.R;
import cn.lds.common.data.ControlHistoryListModel;
import cn.lds.common.data.MessagesModel;
import cn.lds.common.enums.MsgType;
import cn.lds.common.utils.TimeHelper;
import cn.lds.common.utils.ToolsHelper;

/**
 * Created by leadingsoft on 17/12/5.
 * 消息列表适配器
 */

public class MessageListAdapter extends BaseAdapter {
    private List<MessagesModel.DataBean> controlList;
    private Context mContext;

    public MessageListAdapter(Context mContext, List<MessagesModel.DataBean> controlList) {
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
        MessagesModel.DataBean dataBean = controlList.get(i);
        ViewHodler hodler;
        if (null == view) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_message_list, null);
            hodler = new ViewHodler();
            hodler.title = view.findViewById(R.id.item_message_name);
            hodler.content = view.findViewById(R.id.item_message_content);
            hodler.time = view.findViewById(R.id.item_message_time);
            hodler.msgType = view.findViewById(R.id.item_message_type);
            hodler.status = view.findViewById(R.id.iv_status);
            view.setTag(hodler);
        } else {
            hodler = (ViewHodler) view.getTag();
        }
        if(ToolsHelper.isNull(dataBean.getTitle())){
            if(dataBean.getMessageType() == null){
                hodler.title.setText("未知消息类型");
            }else{
                if(dataBean.getMessageType().equals(MsgType.ABNORMAL_MOVEMENT)){
                    hodler.title.setText("车辆盗警");
                }else if(dataBean.getMessageType().equals(MsgType.REMOTE_FAULT)){
                    hodler.title.setText("远程故障");
                }else if(dataBean.getMessageType().equals(MsgType.CARE_NOTIFACTION)){
                    hodler.title.setText("维保提醒");
                }else if(dataBean.getMessageType().equals(MsgType. SYSTEM_NOTIFYCATION)){
                    hodler.title.setText("系统消息");
                }else if(dataBean.getMessageType().equals(MsgType.TEXT_APPLICATION)){
                    hodler.title.setText("文本申请");
                }else if(dataBean.getMessageType().equals(MsgType.IMAGE_APPLICATION)){
                    hodler.title.setText("图片申请");
                }else if(dataBean.getMessageType().equals(MsgType.ORDER)){
                    hodler.title.setText("订单消息");
                }else if(dataBean.getMessageType().equals(MsgType.CODE_TSP_MSG_FENCE_WARNING)){
                    hodler.title.setText("围栏报警");
                }
            }

        }else{
            hodler.title.setText(dataBean.getTitle());
        }
        if(dataBean.isChecked()){
            hodler.status.setImageResource(R.drawable.bg_read);
        }else{
            if(dataBean.getMessageType() != null && dataBean.getMessageType().equals(MsgType.ABNORMAL_MOVEMENT)
                    || dataBean.getMessageType().equals(MsgType.REMOTE_FAULT)
                    || dataBean.getMessageType().equals(MsgType.CODE_TSP_MSG_FENCE_WARNING)
                    || dataBean.getMessageType().equals(MsgType.CARE_NOTIFACTION)) {
                hodler.status.setImageResource(R.drawable.bg_unread_important);
            }else{
                hodler.status.setImageResource(R.drawable.bg_unread_normal);
            }
        }
        hodler.content.setText(dataBean.getContent());
        hodler.time.setText(TimeHelper.getTimeByType(dataBean.getSendTime(), TimeHelper.FORMAT10));
//        hodler.msgType.setText(dataBean.getMessageType().name());
        return view;
    }


    private class ViewHodler {
        TextView title;
        ImageView status;
        TextView content;
        TextView msgType;
        TextView time;
    }

    public void setControlList(List<MessagesModel.DataBean> controlList) {
        this.controlList = controlList;
        notifyDataSetChanged();
    }
}
