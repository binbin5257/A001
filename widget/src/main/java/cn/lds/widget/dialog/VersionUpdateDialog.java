package cn.lds.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.lds.widget.R;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.base.CenterNormalDialog;

/**
 * 版本更新对话框
 * Created by leading123 on 17-12-8.
 */

public class VersionUpdateDialog extends CenterNormalDialog<VersionUpdateDialog> {

    private boolean isMustUpdate = false;
    private String updateContent = "更新内容";
    private View midline;
    private TextView cancelTv;
    private TextView contentTv;
    private TextView confirmTv;
    private TextView titleTv;

    public VersionUpdateDialog(Context context) {
        super(context,R.style.MyDialogStyle);
        init();
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
        contentTv.setText(updateContent);
    }

    public void setMustUpdate(boolean mustUpdate) {
        isMustUpdate = mustUpdate;
    }


    @Override
    public int getLayoutRes() {
        return R.layout.layout_version_update;
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

    public void setRightButtonText(String text){
        confirmTv.setText(text);
    }

    public void setTitle(String title){
        titleTv.setText(title);
    }

    /**
     * 初始化
     */
    private void init() {
        contentTv = findViewById(R.id.update_content);
        cancelTv = findViewById(R.id.update_cancel);
        midline = findViewById(R.id.update_midline);
        confirmTv = findViewById(R.id.update_confirm);
        titleTv = findViewById(R.id.update_title);
        setOnCilckListener(R.id.update_confirm, R.id.update_cancel);
    }
}
