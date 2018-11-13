package cn.lds.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lds.widget.PwdEditText;
import cn.lds.widget.R;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.base.Bottom2TopDialog;
import cn.lds.widget.keyboard.CustomKeyboardView;

/**
 * Created by sibinbin on 18-5-7.
 */

public class KeyboardDialog extends Bottom2TopDialog<KeyboardDialog> implements PwdEditText.OnInputFinishListener, CustomKeyboardView.IOnKeyboardListener, DialogInterface.OnDismissListener, PwdEditText.OnTextChangeListener {

    private Context mContext;
    private String mPin;
    private PwdEditText inputPinEt;


    private OnKeyboardListener mOnKeyboardListener;
    private int pinLength;
    private TextView forgetPinTv;
    private TextView titleTv;
    //    private View lineView;

    public void setTitleText(String text){
        titleTv.setText(text);
    }
    public void setOnKeyboardListener(OnKeyboardListener onKeyboardListener){
        this.mOnKeyboardListener = onKeyboardListener;
    }

    @Override
    public void onTextLengthChange( int length ) {
        pinLength = length;
    }


    public interface OnKeyboardListener{
        void inputFinsh(String pin);
        void forgetPin();
    }

    public KeyboardDialog( Context context, int theme ) {
        super(context, theme);
        this.mContext = context;
    }

    public void setForgetPinTextVisibility(int visibility){
        forgetPinTv.setVisibility(visibility);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.dialog_keyboard;
    }

    @Override
    public void onCreateData() {
        CustomKeyboardView keyboardView = findViewById(R.id.view_keyboard);
        forgetPinTv = findViewById(R.id.tv_forget_pin);
        titleTv = findViewById(R.id.title);
        ImageView disDialogIv = findViewById(R.id.dis_dialog);
        ImageView submitIv = findViewById(R.id.submit);
        inputPinEt = findViewById(R.id.pin_code);

        submitIv.setOnClickListener(this);
        disDialogIv.setOnClickListener(this);
        forgetPinTv.setOnClickListener(this);
        inputPinEt.setOnInputFinishListener(this);
        inputPinEt.setOnTextChangeListener(this);
        keyboardView.setIOnKeyboardListener(this);
        setOnDismissListener(this);
    }

    @Override
    public void onDismiss( DialogInterface dialog ) {
        inputPinEt.setText("");
    }

    @Override
    public void onInputFinish( String pin ) {
        mPin = pin;
        if(mPin.length() == 4){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOnKeyboardListener.inputFinsh(mPin);
                    dismiss();

                }
            },200);
        }
    }

    @Override
    public void onInsertKeyEvent( String text ) {
        inputPinEt.append(text);

    }

    @Override
    public void onDeleteKeyEvent() {
        int start = inputPinEt.length() - 1;
        if (start >= 0) {
            inputPinEt.getText().delete(start, start + 1);
        }
    }


    @Override
    public void onClick( View v, int id ) {
        if (id == R.id.submit) {
            if(!TextUtils.isEmpty(mPin) && pinLength == 4){
                if(mOnKeyboardListener != null){
                    mOnKeyboardListener.inputFinsh(mPin);
                    dismiss();
                }
            }else{
                ToastUtil.showToast(getContext(),"请输入4位PIN码");
            }

        }else if(id == R.id.dis_dialog){
            dismiss();
        }else if(id == R.id.tv_forget_pin){
            if(mOnKeyboardListener != null){
                mOnKeyboardListener.forgetPin();
                dismiss();
            }
        }
    }

}
