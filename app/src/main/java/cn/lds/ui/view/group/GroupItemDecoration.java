package cn.lds.ui.view.group;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

import cn.lds.R;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.ui.TripListActivity;

/**
 * 分组浮动的ItemDecoration
 * Created by haibin on 2017/5/15.
 */
@SuppressWarnings("all")
public class GroupItemDecoration<Group, Child> extends RecyclerView.ItemDecoration {
    protected int mGroupHeight;
    protected int mGroutBackground;
    protected Paint mBackgroundPaint;
    protected Paint mTextPaint;
    protected Paint mLinePaint;
    protected float mTextBaseLine;
    protected int mPaddingLeft, mPaddingRight;
    protected boolean isCenter;
    protected boolean isHasHeader;
    protected int mChildItemOffset;
    @SuppressLint("UseSparseArrays")
    protected Map<Integer, Group> mGroup = new HashMap<>();
    private Context mContext;
    private Paint bitmapPaint;

    public GroupItemDecoration( Context context) {
        super();
        this.mContext = context;
        init();
    }


    private void init() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xFFf5f7f8);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(0xFF353535);
        mTextPaint.setAntiAlias(true);

        mLinePaint = new Paint();
        mLinePaint.setColor(0xFF24253C);
        mLinePaint.setAntiAlias(true);

        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 先于RecyclerView的Item onDraw调用
     *
     * @param c
     *         RecyclerView canvas
     * @param parent
     *         RecyclerView
     * @param state
     *         stare
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        onDrawGroup(c, parent);
    }

    /**
     * 绘制分组Group
     *
     * @param c
     *         Canvas
     * @param parent
     *         RecyclerView
     */
    protected void onDrawGroup(Canvas c, RecyclerView parent) {
        int paddingLeft = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int top, bottom;
        int count = parent.getChildCount();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int key = params.getViewLayoutPosition();
            if (mGroup.containsKey(key)) {

                top = child.getTop() - params.topMargin - mGroupHeight;
                bottom = top + mGroupHeight;
                c.drawRect(paddingLeft, top, right, bottom, mBackgroundPaint);
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_calendar_date_bg);
                String group = mGroup.get(params.getViewLayoutPosition()).toString();
                float x;
                float y = top + mTextBaseLine;
                if (isCenter) {
                    x = parent.getMeasuredWidth() / 2 - getTextX(group);
                } else {
                    x = mPaddingLeft;
                }
//                c.drawBitmap(bitmap, ,mGroupHeight/2 - bitmap.getHeight()/2, bitmapPaint);
                c.drawBitmap(bitmap, x, y - bitmap.getHeight()+ ToolsHelper.dpToPx(2), mTextPaint);
                c.drawText(group, x + bitmap.getWidth() + ToolsHelper.dpToPx(16), y, mTextPaint);

                float startX = x + bitmap.getWidth() + getTextX(group) + ToolsHelper.dpToPx(64);

                c.drawLine(startX,
                        y - getTextY(group)/2-ToolsHelper.dpToPx(1),
                        parent.getMeasuredWidth(),
                        y -  getTextY(group)/2-ToolsHelper.dpToPx(1), mLinePaint);


            }
        }

    }

    public int getmGroupHeight() {
        return mGroupHeight;
    }

    /**
     * 后于RecyclerView的Item onDraw调用
     *
     * @param c      RecyclerView canvas
     * @param parent RecyclerView
     * @param state  stare
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        onDrawOverGroup(c, parent);
//        onDrawGroup(c, parent);
    }

    /**
     * 绘制悬浮组
     *
     * @param c
     *         Canvas
     * @param parent
     *         RecyclerView
     */
    protected void onDrawOverGroup(Canvas c, RecyclerView parent) {
        int firstVisiblePosition = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
        if (firstVisiblePosition == RecyclerView.NO_POSITION) {
            return;
        }
        Group group = getCroup(firstVisiblePosition);
        if (group == null)
            return;
        String groupTitle = group.toString();
        if (TextUtils.isEmpty(groupTitle)) {
            return;
        }
        boolean isRestore = false;
        Group nextGroup = getCroup(firstVisiblePosition + 1);
        if (nextGroup != null && !group.equals(nextGroup)) {
            //说明是当前组最后一个元素，但不一定碰撞了
            View child = parent.findViewHolderForAdapterPosition(firstVisiblePosition).itemView;
            if (child.getTop() + child.getMeasuredHeight() < mGroupHeight) {
                //进一步检测碰撞
                c.save();//保存画布当前的状态
                isRestore = true;
                c.translate(0, child.getTop() + child.getMeasuredHeight() - mGroupHeight);
            }
        }
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int top = parent.getPaddingTop();
        int bottom = top + mGroupHeight;
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_calendar_date_bg);



        c.drawRect(left, top, right, bottom, mBackgroundPaint);
        float x;
        float y = top + mTextBaseLine;
        if (isCenter) {
            x = parent.getMeasuredWidth() / 2 - getTextX(groupTitle);
        } else {
            x = mPaddingLeft;
        }
        c.drawBitmap(bitmap, x, y - bitmap.getHeight()+ ToolsHelper.dpToPx(2), mTextPaint);
        c.drawText(groupTitle, x + bitmap.getWidth() + ToolsHelper.dpToPx(16), y, mTextPaint);
//        float startX = x + bitmap.getWidth() + getTextX(group) + ToolsHelper.dpToPx(64);
//
//        c.drawLine(startX,
//                y - getTextY(group)/2-ToolsHelper.dpToPx(1),
//                parent.getMeasuredWidth(),
//                y -  getTextY(group)/2-ToolsHelper.dpToPx(1), mLinePaint);
        if (isRestore) {
            //还原画布为初始状态
            c.restore();
        }
    }

    /**
     * 设置item的上下左右偏移量
     *
     * @param outRect
     *         rect
     * @param view
     *         item
     * @param parent
     *         RecyclerView
     * @param state
     *         stare
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        getItemOffsets(outRect, view, parent, parent.getChildViewHolder(view).getAdapterPosition());
    }

    /**
     * 设置item的上下左右偏移量，不做任何处理就是默认状态
     *
     * @param outRect
     *         outRect
     * @param view
     *         view
     * @param parent
     *         RecyclerView
     * @param adapterPosition
     *         position
     */
    protected void getItemOffsets(Rect outRect, View view, RecyclerView parent, int adapterPosition) {
        if (mGroup.containsKey(adapterPosition)) {
            outRect.set(0, mGroupHeight, 0, mGroup.containsKey(adapterPosition + 1) ? 0 : mChildItemOffset);
        } else {
            outRect.set(0, 0, 0, mGroup.containsKey(adapterPosition + 1) ? 0 : mChildItemOffset);
        }
    }

    /**
     * 获得当前ViewPosition所在的组
     *
     * @param position
     *         当前View的position
     * @return 当前ViewPosition所在的组
     */
    protected Group getCroup(int position) {
        while (position >= 0) {
            if (mGroup.containsKey(position)) {
                return mGroup.get(position);
            }
            position--;
        }
        return null;
    }

    /**
     * 通知更新分组信息
     *
     * @param adapter
     *         GroupRecyclerAdapter
     */
    public void notifyDataSetChanged(GroupRecyclerAdapter<Group, Child> adapter) {
        mGroup.clear();
        if (adapter == null) return;
        int key = 0;
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            if (i == 0) {
                mGroup.put(isHasHeader ? 1 : 0, adapter.getGroup(i));
                key += adapter.getChildCount(i) + (isHasHeader ? 1 : 0);
                ;
            } else {
                mGroup.put(key, adapter.getGroup(i));
                key += adapter.getChildCount(i);
            }
        }
    }

    public void setChildItemOffset(int childItemOffset) {
        this.mChildItemOffset = childItemOffset;
    }

    public void setBackground(int groupBackground) {
        mBackgroundPaint.setColor(groupBackground);
    }

    public void setTextColor(int textColor) {
        mTextPaint.setColor(textColor);
    }

    public void setTextSize(float textSize) {
        mTextPaint.setTextSize(textSize);
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        mTextBaseLine = mGroupHeight / 2 - metrics.descent + (metrics.bottom - metrics.top) / 2;
    }

    public void setGroupHeight(int groupHeight) {
        mGroupHeight = groupHeight;
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        mTextBaseLine = mGroupHeight / 2 - metrics.descent + (metrics.bottom - metrics.top) / 2;
    }

    public void setPadding(int mPaddingLeft, int mPaddingRight) {
        this.mPaddingLeft = mPaddingLeft;
        this.mPaddingRight = mPaddingRight;
    }

    public void setCenter(boolean isCenter) {
        this.isCenter = isCenter;
    }

    public void setHasHeader(boolean hasHeader) {
        isHasHeader = hasHeader;
    }

    /**
     * 获取文本的x坐标起点
     *
     * @param str
     *         文本
     * @return x
     */
    protected float getTextX(String str) {
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(str, 0, str.length(), bounds);
        return bounds.width() / 2;
    }
    protected float getTextY(String str) {
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(str, 0, str.length(), bounds);
        return bounds.height() / 2;
    }


    /**
     * 获取文本的长度像素
     *
     * @param str
     *         文本
     * @return px
     */
    protected float getTextLenghtPx(String str) {
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(str, 0, str.length(), bounds);
        return bounds.width();
    }
}
