package cn.lds.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.model.LeftMenuBean;


/**
 * 左侧滑菜单列表适配器
 */

public class LeftMenuAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<LeftMenuBean> menuBeanList;

    public LeftMenuAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.menuBeanList = new ArrayList<>();
        getMenuListData();

    }

    @Override
    public int getCount() {
        return menuBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.item_left_menu,null);
            holder = new ViewHolder();
            holder.menuIconIv = (ImageView) convertView.findViewById(R.id.menu_icon);
            holder.menuNameTv = (TextView) convertView.findViewById(R.id.menu_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        LeftMenuBean leftMenuBean = menuBeanList.get(position);
        holder.menuNameTv.setText(leftMenuBean.getName());
        return convertView;
    }
    class ViewHolder{
        private TextView menuNameTv;
        private ImageView menuIconIv;
    }
    /**
     * 侧滑栏菜单列表数据
     * @return
     */
    public List<LeftMenuBean> getMenuListData() {

        LeftMenuBean bean1 = new LeftMenuBean();
        bean1.setName("车辆管理");
        LeftMenuBean bean2 = new LeftMenuBean();
        bean2.setName("行驶历史");
        LeftMenuBean bean3 = new LeftMenuBean();
        bean3.setName("操作历史");
        LeftMenuBean bean4 = new LeftMenuBean();
        bean4.setName("车钱包");
        LeftMenuBean bean5 = new LeftMenuBean();
        bean5.setName("开机提示语");
        LeftMenuBean bean6 = new LeftMenuBean();
        bean6.setName("安全信息管理");
        LeftMenuBean bean7 = new LeftMenuBean();
        bean7.setName("设置");
        menuBeanList.clear();
        menuBeanList.add(bean1);
        menuBeanList.add(bean2);
        menuBeanList.add(bean3);
        menuBeanList.add(bean4);
        menuBeanList.add(bean5);
        menuBeanList.add(bean6);
        menuBeanList.add(bean7);
        return menuBeanList;
    }
}
