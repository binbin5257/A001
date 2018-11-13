package cn.lds.widget.dialog.callback;

import android.app.Dialog;

import cn.lds.widget.dialog.annotation.ClickPosition;


@SuppressWarnings("all")
public interface OnDialogClickListener<D extends Dialog>
{
	public  void onDialogClick(D dialog, @ClickPosition String clickPosition);
}
