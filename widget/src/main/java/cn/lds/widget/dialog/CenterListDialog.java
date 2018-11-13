package cn.lds.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.lds.widget.R;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.base.CenterNormalDialog;

/**
 * 屏幕中间显示列表对话框（仿微信发送位置对话框）
 */

public class CenterListDialog extends CenterNormalDialog<CenterListDialog> {

    private List<String> mList;
    private Context mContext;
    private Activity activity;

    public CenterListDialog(Activity act, Context context, List<String> list) {
        super(context,R.style.MyDialogStyle);
        this.mList = list;
        this.mContext = context;
        this.activity = act;
        init();
    }
    @Override
    public int getLayoutRes() {
        return R.layout.layout_list;
    }

    @Override
    public void onCreateData() {
        setCanceledOnTouchOutside(true);
        setCancelable(true);

    }

    @Override
    public void onClick(View v, int id) {
    }

    @Override
    public void show() {
//        setBackGroundColor(activity,0.7f);
        super.show();
    }

    private void init() {
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(new MyListAdapter());
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                setBackGroundColor(activity,1.0f);
            }
        });
    }
    public class MyListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_text, null);
            TextView tv = view.findViewById(R.id.text);
            tv.setText(mList.get(position));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDialogListClickListener(position);
                }
            });
            return view;
        }
    }
    /**
     * 设置窗体透明度
     */
    public void setBackGroundColor(Activity act, float alpha){
        WindowManager.LayoutParams lp = act.getWindow().getAttributes();
        lp.alpha = alpha;
        act.getWindow().setAttributes(lp);
    }
}
