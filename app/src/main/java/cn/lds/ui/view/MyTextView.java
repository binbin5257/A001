package cn.lds.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by colin on 18/1/12.
 */

public class MyTextView extends android.support.v7.widget.AppCompatTextView {
    Typeface typeface;

    public MyTextView(Context context) {
        super(context);
        if (null == typeface)
            typeface = Typeface.createFromAsset(context.getAssets(), "DINCond-Bold.otf");
        this.setTypeface(typeface);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (null == typeface)
            typeface = Typeface.createFromAsset(context.getAssets(), "DINCond-Bold.otf");
        this.setTypeface(typeface);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (null == typeface)
            typeface = Typeface.createFromAsset(context.getAssets(), "DINCond-Bold.otf");
        this.setTypeface(typeface);
    }

}
