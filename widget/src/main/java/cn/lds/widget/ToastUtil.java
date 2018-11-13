package cn.lds.widget;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Toast 工具类
 */

public class ToastUtil {

    /**
     * 普通Toast工具类
     * @param context 上下文
     * @param str 提示文本
     */
    public static void showToast(Context context,String str){
        if(!TextUtils.isEmpty(str)){
            Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"请输入提示文本",Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * 普通Toast工具类
     * @param context 上下文
     * @param resId 提示文本的资源id
     */
    public static void showToast(Context context,int resId){
        Toast.makeText(context,context.getResources().getString(resId),Toast.LENGTH_SHORT).show();
    }
}
