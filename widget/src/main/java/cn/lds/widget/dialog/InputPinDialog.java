package cn.lds.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.lds.widget.R;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.base.CenterNormalDialog;

/**
 * 版本更新对话框
 * Created by leading123 on 17-12-8.
 */

public class InputPinDialog extends CenterNormalDialog<InputPinDialog> {

    private String updateContent = "更新内容";
    private View midline;
    private TextView cancelTv;
    private TextView inputPinHint;
    private EditText contentTv;

    public InputPinDialog(Context context) {
        super(context);
        init();
    }


    @Override
    public int getLayoutRes() {
        return R.layout.layout_input_pin;
    }

    @Override
    public void onCreateData() {
        setCanceledOnTouchOutside(false);

    }

    @Override
    public void onClick(View v, int id) {
        if (id == R.id.update_confirm) {
            if (0 == contentTv.getText().length()) {
                ToastUtil.showToast(context, "请输入PIN码");
                return;
            }
            onDialogClickListener(ClickPosition.SUBMIT);
        } else if (id == R.id.update_cancel) {
            onDialogClickListener(ClickPosition.CANCEL);
        }
    }

    @Override
    public void show() {
        cancelTv.setVisibility(View.VISIBLE);
        midline.setVisibility(View.VISIBLE);
        super.show();
    }

    /**
     * 初始化
     */
    private void init() {
        contentTv = findViewById(R.id.edittext);
        cancelTv = findViewById(R.id.update_cancel);
        midline = findViewById(R.id.update_midline);
        inputPinHint = findViewById(R.id.input_pin_hint);
        setOnCilckListener(R.id.update_confirm, R.id.update_cancel);
    }

    public String getPin() {
        return contentTv.getText().toString();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        contentTv.setText("");
    }

    public void setHint(String s) {
        inputPinHint.setText(s);
    }
}
