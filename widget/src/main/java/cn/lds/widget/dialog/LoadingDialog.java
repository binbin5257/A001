package cn.lds.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cn.lds.widget.R;


public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder{
        private Context context;
        private String message;
        private boolean isShowMessage = true;
        private boolean isCancelable = true;
        private boolean isCancelOutside = true;
        public Builder(Context context){
            this.context = context;
        }

        /**
         * 设置提示消息
         */
        public Builder setMessage(String message){
            this.message = message;
            return this;
        }

        /**
         * 设置是否显示提示信息
         */
        public Builder setShowMessage(boolean isShowMessage){
            this.isShowMessage = isShowMessage;
            return this;
        }
        /**
         * 按返回键是否可以取消
         */
        public Builder setCancelable(boolean isCancelable){
            this.isCancelable = isCancelable;
            return this;
        }
        /**
         * 点击外部是否可以取消
         */
        public Builder setCancelOutside(boolean isCancelOutside){
            this.isCancelOutside = isCancelOutside;
            return this;
        }

        public LoadingDialog buildVertical(){
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_loading,null);
            LoadingDialog loadingDailog =new LoadingDialog(context,R.style.MyDialogStyle);
            TextView msgText= view.findViewById(R.id.tipTextView);
            if(isShowMessage){
                msgText.setText(message);
            }else{
                msgText.setVisibility(View.GONE);
            }
            loadingDailog.setContentView(view);
            loadingDailog.setCancelable(isCancelable);
            loadingDailog.setCanceledOnTouchOutside(isCancelOutside);
            return loadingDailog;
        }
        public LoadingDialog buildHorizontal(){
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_loading_horizontal,null);
            LoadingDialog loadingDailog=new LoadingDialog(context,R.style.MyDialogStyle);
            TextView msgText= view.findViewById(R.id.tipTextView);
            if(isShowMessage){
                msgText.setText(message);
            }else{
                msgText.setVisibility(View.GONE);
            }
            loadingDailog.setContentView(view);
            loadingDailog.setCancelable(isCancelable);
            loadingDailog.setCanceledOnTouchOutside(isCancelOutside);
            return loadingDailog;
        }

    }
}
