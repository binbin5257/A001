package cn.lds.widget.dialog.callback;

import android.app.Dialog;

@SuppressWarnings("all")
public interface OnWait2DismissListener<D extends Dialog> {
    /**
     * @param second 为零的时候  对话框结束
     */
    public void onDismiss(D dialog, int second);
}
