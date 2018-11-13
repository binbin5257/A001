package cn.lds.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.lds.R;
import cn.lds.ui.view.RatingBar;

public class CarCheckAdapter extends BaseAdapter {

    private Context mContext;
    private List<Integer> mList;
    private final LayoutInflater inflater;
    private boolean UPDATE = false;

    public CarCheckAdapter(Context context,List<Integer> list) {
        this.mContext = context;
        this.mList = list;
        inflater = LayoutInflater.from(context);
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
        View view = inflater.inflate(R.layout.item_car_check,null);
        RatingBar star = view.findViewById(R.id.star);
        ImageView icon = view.findViewById(R.id.icon);
        TextView title = view.findViewById(R.id.title);
        TextView content = view.findViewById(R.id.content);
        if(position == 0){
            initData(R.drawable.bg_power,"动力系统",
                    "动力系统 ！",
                    "车辆发动机，变速箱，等传动系统",
                    "动力系统异常，请到4S店维修！",
                    position,
                    star,
                    icon,
                    title,
                    content);
        }else if(position == 1){
            initData(R.drawable.bg_car_check_security,"安全系统",
                    "安全系统 ！",
                    "车辆安全气囊，车身雷达等",
                    "安全系统异常，请到4S店维修！",
                    position,
                    star,
                    icon,
                    title,
                    content);
        }else if(position == 2){
            initData(R.drawable.bg_braking,"制动系统",
                    "制动系统 ！",
                    "。。。等",
                    "制动系统异常，请到4S店维修！",
                    position,
                    star,
                    icon,
                    title,
                    content);
        }else if(position == 3){
            initData(R.drawable.bg_steering,"转向系统",
                    "转向系统 ！",
                    "车辆转向机，转向助力等",
                    "转向系统异常，请到4S店维修！",
                    position,
                    star,
                    icon,
                    title,
                    content);
        }else if(position == 4){
            initData(R.drawable.bg_car_body,"车身控制系统",
                    "车身控制系统 ！",
                    "车身稳定控制系统，制动防抱死系统等",
                    "车身控制系统异常，请到4S店维修！",
                    position,
                    star,
                    icon,
                    title,
                    content);
        }

        return view;
    }
    public void updateList(){
        UPDATE = true;
        notifyDataSetChanged();
    }

    private void initData( int imagRes,String checkingTitle,String checkErrorTitle,String contentSuccess,String contentError,int position, RatingBar star, ImageView icon, TextView title, TextView content ) {
        title.setText(checkingTitle);
        title.setTextColor(mContext.getResources().getColor(R.color.white));
        icon.setImageResource(imagRes);
        content.setText(contentSuccess);
        star.setVisibility(View.INVISIBLE);
        //        if(UPDATE){
//            try {
//                title.setText(checkingTitle);
//                icon.setImageResource(imagRes);
//                content.setText("检测中");
//                content.setTextColor(Color.parseColor("#FF5B00"));
//                title.setTextColor(Color.parseColor("#FF3939"));
//                star.setVisibility(View.INVISIBLE);
//                star.setStar(mList.get(position));
//                Thread.sleep(2000);
//                star.setStar(View.VISIBLE);
//                if(mList.get(position) > 2){
//                    title.setTextColor(Color.parseColor("#FFFFFF"));
//                    content.setText(contentSuccess);
//                    content.setTextColor(Color.parseColor("#FFFFFF"));
//                }else{
//                    title.setText(checkErrorTitle);
//                    content.setText(contentError);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }else{
//            title.setText(checkingTitle);
//            title.setTextColor(mContext.getResources().getColor(R.color.white));
//            icon.setImageResource(imagRes);
//            content.setText(contentSuccess);
//            star.setVisibility(View.INVISIBLE);
//        }


    }


}
