package cn.lds.widget.dialog.base;

import android.content.Context;

import cn.lds.widget.R;
import cn.lds.widget.dialog.annotation.AnimType;


public abstract class CenterScaleDialog<D extends CenterScaleDialog> extends SimpleDialog<D> {
    public CenterScaleDialog(Context context) {
        super(context, R.style.alex_dialog_base_light_style);

    }

    /**
     * 显示对话框，无动画
     */
    @Override
    public void show() {
        show(AnimType.CENTER_SCALE);
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
