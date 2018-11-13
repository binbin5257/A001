package cn.lds.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.lds.widget.R;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.base.CenterNormalDialog;

/**
 * 确认框
 * Created by leading123 on 17-12-8.
 */

public class ConfirmDialog extends CenterNormalDialog<ConfirmDialog> {

    private boolean isMustUpdate = false;
    private View midline;
    private TextView cancelTv;
    private TextView confirmTv;
    private TextView contentTv;
    private TextView titleTv;

    public ConfirmDialog(Context context) {
        super(context,R.style.MyDialogStyle);
        init();
    }

    public void setContent(String updateContent) {
        contentTv.setText(updateContent);
    }

    public void setTitle(String updateContent) {
        titleTv.setText(updateContent);
    }

    public void setLeftButtonText(String text){
        cancelTv.setText(text);
    }

    public void setTitleVisibilty(int visibilty){
        titleTv.setVisibility(visibilty);
    }

    public void setRightButtonText(String text){

        confirmTv.setText(text);
    }


    @Override
    public int getLayoutRes() {
        return R.layout.layout_confirm;
    }

    @Override
    public void onCreateData() {
        setCancelable(isMustUpdate);
        setCanceledOnTouchOutside(false);

    }

    @Override
    public void onClick(View v, int id) {
        if (id == R.id.update_confirm) {
            onDialogClickListener(ClickPosition.SUBMIT);
        } else if (id == R.id.update_cancel) {
            onDialogClickListener(ClickPosition.CANCEL);
        }
    }

    @Override
    public void show() {
        if (isMustUpdate) {
            cancelTv.setVisibility(View.GONE);
            midline.setVisibility(View.GONE);
        } else {
            cancelTv.setVisibility(View.VISIBLE);
            midline.setVisibility(View.VISIBLE);
        }
        super.show();
    }

    /**
     * 初始化
     */
    private void init() {
        contentTv = findViewById(R.id.update_content);
        cancelTv = findViewById(R.id.update_cancel);
        confirmTv = findViewById(R.id.update_confirm);
        midline = findViewById(R.id.update_midline);
        titleTv = findViewById(R.id.update_title);
        setOnCilckListener(R.id.update_confirm, R.id.update_cancel);
    }
}
