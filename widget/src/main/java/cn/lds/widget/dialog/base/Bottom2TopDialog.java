package cn.lds.widget.dialog.base;

import android.content.Context;

import cn.lds.widget.R;
import cn.lds.widget.dialog.annotation.AnimType;


public abstract class Bottom2TopDialog<D extends Bottom2TopDialog> extends SimpleDialog<D> {
    public Bottom2TopDialog(Context context,int theme) {
        super(context, theme);
    }
    /**
     * 显示对话框，无动画
     */
    @Override
    public void show() {
        show(AnimType.BOTTOM_2_TOP);
    }
    /**
     * 显示对话框，强制转换对话框的动画类型
     *
     * @param animType
     */
    @Override
    public void show(@AnimType int animType) {
        super.show(animType);
    }
}