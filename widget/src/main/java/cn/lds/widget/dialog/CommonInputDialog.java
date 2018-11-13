package cn.lds.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import cn.lds.widget.R;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.base.CenterNormalDialog;

/**
 * 验证登录密码对话框
 * Created by sibinbin on 18-2-28.
 */

public class CommonInputDialog extends CenterNormalDialog<CommonInputDialog> {

    private View midline;
    private TextView cancelTv;
    private TextView inputPinHint;
    private EditText contentTv;
    public String pasw;
    private String mTitle;
    private Activity mContext;

    public String getPasw() {
        return contentTv.getText().toString();
    }

    public CommonInputDialog( Activity context, String title) {
        super(context);
        this.mTitle = title;
        this.mContext = context;
        init();
    }

    public void setContent(String text){
        contentTv.setText(text);
    }

    public EditText getEditText(){
        return contentTv;
    }

    public void setInputType(int inputType){
        contentTv.setInputType(inputType);
    }


    @Override
    public int getLayoutRes() {
        return R.layout.layout_ver_login_psw;
    }

    @Override
    public void onCreateData() {
        setCanceledOnTouchOutside(true);

    }

    @Override
    public void onClick(View v, int id) {
        if (id == R.id.update_confirm) {
            if (0 == contentTv.getText().length()) {
                ToastUtil.showToast(context, mTitle);
                return;
            }
            onSubmitInputListener.onSubmitInput(contentTv.getText().toString());
            dismiss();
        } else if (id == R.id.update_cancel) {
            hideKeyBoard();
            onSubmitInputListener.onCancelDialog();
           dismiss();
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
        inputPinHint.setText(mTitle);
        setOnCilckListener(R.id.update_confirm, R.id.update_cancel);
        showInputSoft();

    }

    private void showInputSoft() {
        //解决dilaog中EditText无法弹出输入的问题
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //弹出对话框后直接弹出键盘
        contentTv.setFocusableInTouchMode(true);
        contentTv.requestFocus();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) contentTv.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(contentTv, 0);

            }
        },100);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        contentTv.setText("");
    }
    private OnSubmitInputListener onSubmitInputListener;

    public void setOnSubmitInputListener( OnSubmitInputListener submitInputListener ) {
        this.onSubmitInputListener = submitInputListener;

    }

    public interface OnSubmitInputListener{

        void onSubmitInput(String psw);

        void onCancelDialog();

    }

    public void hideKeyBoard(){
        //隐藏输入法
        InputMethodManager manager= (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow( this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
