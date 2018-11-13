package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.databinding.ActivityTripListBinding;
import cn.lds.databinding.ActivityTripListNewBinding;

/**
 * Created by sibinbin on 18-3-5.
 */

public class TripListNewActivity extends BaseActivity implements View.OnClickListener {

    private ActivityTripListNewBinding tripListBinding;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        tripListBinding = DataBindingUtil.setContentView(this, R.layout.activity_trip_list_new);
        TextView titile = tripListBinding.getRoot().findViewById(R.id.top_title_tv);
        titile.setText("行程历史");

        Date date = new Date(System.currentTimeMillis());
        Calendar minCalendar = Calendar.getInstance();
        minCalendar.setTime(date);
        minCalendar.add(minCalendar.DATE,-30);
        Calendar maxCalendar = Calendar.getInstance();
        int minYear = minCalendar.get(minCalendar.YEAR);
        int minMonth = minCalendar.get(minCalendar.MONTH) + 1;
        int maxYear = maxCalendar.get(maxCalendar.YEAR);
        int maxMonth = maxCalendar.get(maxCalendar.MONTH) + 1;
        tripListBinding.calendarView.setRange(minYear,minMonth,maxYear,maxMonth);
        final int year = tripListBinding.calendarView.getCurYear();
        final int month = tripListBinding.calendarView.getCurMonth();
        List greenDays = new ArrayList<>();
        greenDays.add(setGreenTripDate(year, month, 3, 0xFF40db25, "假"));
        greenDays.add(setGreenTripDate(year, month, 6, 0xFFe69138, "事"));
        greenDays.add(setGreenTripDate(year, month, 9, 0xFFdf1356, "议"));
        greenDays.add(setGreenTripDate(year, month, 13, 0xFFedc56d, "记"));
        greenDays.add(setGreenTripDate(year, month, 14, 0xFFedc56d, "记"));
        greenDays.add(setGreenTripDate(year, month, 15, 0xFFaacc44, "假"));
        greenDays.add(setGreenTripDate(year, month, 18, 0xFFbc13f0, "记"));
        greenDays.add(setGreenTripDate(year, month, 25, 0xFF13acf0, "假"));
        setGreenTripDate(greenDays);

    }

    /**
     * 标记绿色出行
     */
    private cn.lds.ui.view.calendar.Calendar setGreenTripDate(int year, int month, int day, int color, String text) {
        cn.lds.ui.view.calendar.Calendar calendar = new cn.lds.ui.view.calendar.Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }
    private void setGreenTripDate( List<cn.lds.ui.view.calendar.Calendar> greenDateList){
        tripListBinding.calendarView.setSchemeDate(greenDateList);
    }

    @Override
    public void initListener() {
        tripListBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
    }

    @Override
    public void onClick( View v ) {
        switch (v.getId()){
            case R.id.top_back_iv:
                finish();
                break;
        }
    }
}
