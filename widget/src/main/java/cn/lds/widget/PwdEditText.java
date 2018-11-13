package cn.lds.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.EditText;

import cn.lds.widget.captcha.Utils;

/**
 *
 * 自定义密码输入框
 *
 * @author zhangke
 *
 */
public class PwdEditText extends EditText {

    /**
     * 间隔
     */
    private final int PWD_SPACING = Utils.dp2px(getContext(),16);
    /**
     * 密码大小
     */
    private final int PWD_SIZE = 4;
    /**
     * 密码长度
     */
    private final int PWD_LENGTH = 4;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 宽度
     */
    private int mWidth;
    /**
     * 高度
     */
    private int mHeight;
    /**
     * 密码框
     */
    private RectF mRect;

    /**
     * 密码画笔
     */
    private Paint mPwdPaint;

    /**
     * 密码框画笔
     */
    private Paint mRectPaint;
    /**
     * 输入的密码长度
     */
    private int mInputLength;

    /**
     * 输入结束监听
     */
    private OnInputFinishListener mOnInputFinishListener;
    private String textStr;
    private int left;
    private int right;
    private int backgroundColor;
    private int lineColor;

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     */
    public PwdEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);


    }

    public PwdEditText( Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, 0);

    }

    private void init( Context context, AttributeSet attrs, int defStyleAttr ) {
        @SuppressLint("Recycle") TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PwdEditText_attr,
                defStyleAttr, 0);
        backgroundColor = a.getColor(
                R.styleable.PwdEditText_attr_bg_color, Color.parseColor("#FF121420"));
        lineColor = a.getColor(
                R.styleable.PwdEditText_attr_line_color, Color.parseColor("#FF414665"));
        // 初始化密码画笔
        mPwdPaint = new Paint();
        mPwdPaint.setColor(Color.WHITE);
        mPwdPaint.setStyle(Paint.Style.FILL);
        mPwdPaint.setAntiAlias(true);
        // 初始化密码框
        mRectPaint = new Paint();
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStrokeWidth(Utils.dp2px(getContext(),2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();

        // 这三行代码非常关键，大家可以注释点在看看效果
        Paint paint = new Paint();
        paint.setColor(backgroundColor);
        canvas.drawRect(0, 0, mWidth, mHeight, paint);

        // 计算每个密码框宽度
        int rectWidth = (mWidth - PWD_SPACING * (PWD_LENGTH - 1)) / PWD_LENGTH;
        mRectPaint.setColor(lineColor);
        // 绘制密码框
        for (int i = 0; i < PWD_LENGTH; i++) {
            if(i == 0){
                left = (rectWidth + PWD_SPACING) * i + Utils.dp2px(getContext(),1);
            }else{
                left = (rectWidth + PWD_SPACING) * i;
            }

            int top = 2;
            if(i == PWD_LENGTH - 1){
                right = left + rectWidth - Utils.dp2px(getContext(),1);
            }else{
                right = left + rectWidth;            }
            int bottom = mHeight - top;
            mRect = new RectF(left, top, right, bottom);
//            canvas.drawRect(mRect, mRectPaint);
            canvas.drawRoundRect(mRect,Utils.dp2px(getContext(),6),Utils.dp2px(getContext(),4),mRectPaint);
        }

        // 绘制密码
        for (int i = 0; i < mInputLength; i++) {
//            int cx = rectWidth / 2 + (rectWidth + PWD_SPACING) * i;
            mPwdPaint.setTextSize(Utils.dp2px(getContext(),16));
            int cx = rectWidth / 2 - Utils.dp2px(getContext(),5) + (rectWidth + PWD_SPACING) * i;
            int cy = mHeight / 2 +  Utils.dp2px(getContext(),5);
//            canvas.drawCircle(cx, cy, PWD_SIZE, mPwdPaint);

            canvas.drawText(String.valueOf(textStr.charAt(i)),cx,cy,mPwdPaint);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start,
                                 int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        textStr = text.toString();
        this.mInputLength = text.toString().length();
        if(mOnTextChangeListener != null){
            mOnTextChangeListener.onTextLengthChange(mInputLength);

        }
        invalidate();
        if (mInputLength == PWD_LENGTH && mOnInputFinishListener != null) {
            mOnInputFinishListener.onInputFinish(text.toString());
        }
    }
    private OnTextChangeListener mOnTextChangeListener;

    public void setOnTextChangeListener( OnTextChangeListener onTextChangeListener ) {
        this.mOnTextChangeListener = onTextChangeListener;
    }

    public interface OnTextChangeListener{
        /**
         * 输入长度监听
         */
        void onTextLengthChange(int length);
    }

    public interface OnInputFinishListener {
        /**
         * 密码输入结束监听
         *
         * @param password
         */
        void onInputFinish(String password);


    }

    /**
     * 设置输入完成监听
     *
     * @param onInputFinishListener
     */
    public void setOnInputFinishListener(
            OnInputFinishListener onInputFinishListener) {
        this.mOnInputFinishListener = onInputFinishListener;

    }

}
