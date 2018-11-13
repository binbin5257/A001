package cn.lds.widget.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.lds.widget.R;
import cn.lds.widget.dialog.base.CenterNormalDialog;

/**
 * 控车提示对话框
 * Created by sibinbin on 18-5-7.
 */

public class ControlCarPromptDialog extends CenterNormalDialog {

    private final Context mContext;
    private boolean isPrompt = false;
    private TextView nextPrompt;
    private Drawable selectDrawable;
    private Drawable unSelcetDrawable;

    public ControlCarPromptDialog( Context context ) {
        super(context, R.style.MyDialogStyle);
        mContext = context;
        init();
    }

    private void init() {
        nextPrompt = (TextView) findView(R.id.next_prompt);
        Button knowBtn = (Button)findViewById(R.id.bt_know);
        selectDrawable = mContext.getResources().getDrawable(
                R.drawable.bg_remeber_pas_select);
        unSelcetDrawable = mContext.getResources().getDrawable(
                R.drawable.bg_remeber_pas_unselect);
        selectDrawable.setBounds(0, 0, selectDrawable.getMinimumWidth(), selectDrawable.getMinimumHeight());
        unSelcetDrawable.setBounds(0, 0, unSelcetDrawable.getMinimumWidth(), unSelcetDrawable.getMinimumHeight());
        setOnCilckListener(R.id.next_prompt,R.id.bt_know);
        nextPrompt.setCompoundDrawables(unSelcetDrawable,null,null,null);

    }

    @Override
    public void onClick( View v ) {
        int i = v.getId();
        if (i == R.id.next_prompt) {
            if(isPrompt){
                isPrompt = false;
                nextPrompt.setCompoundDrawables(unSelcetDrawable,null,null,null);
            }else{
                isPrompt = true;
                nextPrompt.setCompoundDrawables(selectDrawable,null,null,null);

            }
        }else if(i == R.id.bt_know){
            mOnImKnowClickListenter.onClick(this,isPrompt);
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.dialog_control_car_prompt;
    }

    @Override
    public void onCreateData() {

    }

    @Override
    public void onClick( View v, int id ) {

    }

    private OnImKnowClickListenter mOnImKnowClickListenter;
    public void setOnImKnowClickListenter(OnImKnowClickListenter onImKnowClickListenter){
        mOnImKnowClickListenter = onImKnowClickListenter;
    }
    public interface OnImKnowClickListenter{
        void onClick(ControlCarPromptDialog dialog,boolean isPrompt);
    }
}
