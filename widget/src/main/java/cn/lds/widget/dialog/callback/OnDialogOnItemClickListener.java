package cn.lds.widget.dialog.callback;

import android.app.Dialog;

import cn.lds.widget.dialog.annotation.ClickPosition;


@SuppressWarnings("all")
public interface OnDialogOnItemClickListener<D extends Dialog>
{
	public  void onDialogItemClick(D dialog, int position);
}
