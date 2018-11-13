package cn.lds.ui.view.calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import cn.lds.R;

/**
 * 多彩周视图
 * Created by huanghaibin on 2017/11/29.
 */

public class ColorfulWeekView extends WeekView {

    private int mRadius;

    public ColorfulWeekView(Context context) {
        super(context);
    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
    }

    /**
     * 如果需要点击Scheme没有效果，则return true
     *
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return false 则不绘制onDrawScheme，因为这里背景色是互斥的
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasScheme) {
//        if(hasScheme){
//            int cx = x + mItemWidth / 2;
//            int cy = mItemHeight / 2;
//            canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
//        }
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);

        return false;
    }

    @Override
    protected void onDrawScheme( Canvas canvas, Calendar calendar, int x ) {

    }


    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x,boolean isSelected) {

        if(!isSelected){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_calendar_schme_bg);
            int cx = x + (mItemWidth / 2 - bitmap.getWidth() / 2);
            int cy = (mItemHeight / 2 - bitmap.getHeight() / 2);
            canvas.drawBitmap(bitmap, cx, cy, mSchemePaint);
        }

    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine;
        int cx = x + mItemWidth / 2;
        if (isSelected || hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    mSelectTextPaint);
        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mOtherMonthTextPaint : mOtherMonthTextPaint);
        }
    }
}
