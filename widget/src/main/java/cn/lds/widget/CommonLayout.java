package cn.lds.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lds.widget.listener.OnEnditorListener;

/**
 * 通用条目布局
 * Created by sibinbin on 17-12-13.
 */

public class CommonLayout extends LinearLayout {
    private Context mContext;
    private int mNameColor;
    private int mContentColor;
    private boolean iconVisiblity;
    private float textSize;
    private int contentMaxLenght;
    private String name;
    private String content;
    private ImageView editorIv;
    private TextView nameTv;
    private final String inputType;
    private final boolean bottomLineVisiblity;
    private View bottomLineView;
    private boolean isEnable = false;
    private ImageView arrowRightIv;
    private final boolean arrowRightVisiblity;
    private TextView contentTv;


    public CommonLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CommonLayoutView,
                0,0);

        try {
            mNameColor = a.getColor(R.styleable.CommonLayoutView_nameColor, Color.parseColor("#6B6B6B"));
            mContentColor = a.getColor(R.styleable.CommonLayoutView_contentColor, Color.parseColor("#6B6B6B"));
            iconVisiblity = a.getBoolean(R.styleable.CommonLayoutView_iconVisiblity, false);
            bottomLineVisiblity = a.getBoolean(R.styleable.CommonLayoutView_bottomLineVisiblity, true);
            textSize = a.getFloat(R.styleable.CommonLayoutView_textSize, dp2px(14));
            contentMaxLenght = a.getInt(R.styleable.CommonLayoutView_contentMaxLength, dp2px(30));
            name = a.getString(R.styleable.CommonLayoutView_name);
            content = a.getString(R.styleable.CommonLayoutView_content);
            inputType = a.getString(R.styleable.CommonLayoutView_inputType);
            arrowRightVisiblity = a.getBoolean(R.styleable.CommonLayoutView_arrowRightVisiblity,false);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item, this);
        nameTv = view.findViewById(R.id.name);
        contentTv = view.findViewById(R.id.tv_content);
        bottomLineView = view.findViewById(R.id.bottom_line);
        arrowRightIv = view.findViewById(R.id.arrow_right);
        nameTv.setText(name);
        contentTv.setText(content);
        nameTv.setTextColor(mNameColor);
        contentTv.setTextColor(mContentColor);
        bottomLineView.setVisibility(bottomLineVisiblity ? View.VISIBLE : View.GONE);
        arrowRightIv.setVisibility(arrowRightVisiblity ? View.VISIBLE : View.GONE);

//        editorIv.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String content = contentEt.getText().toString();
//                if(isEnable){
//                    isEnable = false;
//                    contentEt.setTextColor(mContext.getResources().getColor(R.color.gray_text));
//                    contentEt.setEnabled(isEnable);
//                    if(onEnditorListener != null){
//                        onEnditorListener.endEditor(content,contentEt);
//                    }
//
//
//                }else{
//                    isEnable = true;
//                    contentEt.setTextColor(mContext.getResources().getColor(R.color.black));
//                    contentEt.setEnabled(isEnable);
//                    contentEt.requestFocus();
//                    contentEt.setSelection(contentEt.length());
//                    if(onEnditorListener != null){
//                        onEnditorListener.startEditor(content,contentEt);
//                    }
//
//                }
//                invalidate();
//            }
//        });

    }

    private int dp2px(float dp) {
        return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    public void setNameColor(int color) {
        nameTv.setTextColor(color);
        invalidate();
    }

    public void setContentColor(int color) {
        contentTv.setTextColor(color);
        invalidate();
    }

    public void setIconVisiblity(boolean iconVisiblity) {
        if(iconVisiblity){
            editorIv.setVisibility(VISIBLE);
        }else{
            editorIv.setVisibility(GONE);
        }
        invalidate();
    }

    public void setTextSize(float textSize) {
        nameTv.setTextSize(textSize);
        contentTv.setTextSize(textSize);
        invalidate();
    }

    public void setContentMaxLenght(int contentMaxLenght) {
        contentTv.setMaxHeight(contentMaxLenght);
        invalidate();
    }

    public void setName(String name) {
        nameTv.setText(name);
        invalidate();
    }

    public void setContent(String content) {
        contentTv.setText(content);
        invalidate();
    }

    public String getContent(){
        return contentTv.getText().toString().trim();
    }

    private OnEnditorListener onEnditorListener;

    public void setOnEnditorListener(OnEnditorListener onEnditorListener) {
        this.onEnditorListener = onEnditorListener;
    }

}
