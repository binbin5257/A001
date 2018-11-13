package cn.lds.widget.dialog;

import android.content.Context;

public class LoadingDialogUtils {

    private static LoadingDialog dialog;

    private LoadingDialogUtils() {

    }

    /**
     * 竖向加载框，图标在文字的上面侧
     *
     * @param context
     *         上下文
     * @param str
     *         加载文字
     */
    public static void showVertical(Context context, String str, boolean isShowMessage, boolean isCancelable, boolean isCancelOutside) {
        dialog = new LoadingDialog.Builder(context)
                .setMessage(str)
                .setShowMessage(isShowMessage)
                .setCancelable(isCancelable)
                .setCancelOutside(isCancelOutside)
                .buildVertical();

        dialog.show();

    }

    /**
     * 竖向加载框，图标在文字的上面侧
     *
     * @param context
     *         上下文
     * @param str
     *         加载文字
     */
    public static void showVertical(Context context, String str) {
        dialog = new LoadingDialog.Builder(context)
                .setMessage(str)
                .setShowMessage(true)
                .setCancelOutside(true)
                .setCancelable(true)
                .buildVertical();
        dialog.show();
    }

    /**
     * 竖向加载框，图标在文字的上面侧
     *
     * @param context
     *         上下文
     * @param str
     *         加载文字
     */
    public static void showVertical(Context context, String str, boolean cancelable) {
        dialog = new LoadingDialog.Builder(context)
                .setMessage(str)
                .setShowMessage(true)
                .setCancelOutside(cancelable)
                .setCancelable(cancelable)
                .buildVertical();
        dialog.show();
    }

    /***
     * 横向加载框，图标在文字的左侧，仿微信正在登录效果
     * @param context 上下文
     * @param str 加载文字
     */
    public static void showHorizontal(Context context, String str) {
        dialog = new LoadingDialog.Builder(context)
                .setMessage(str)
                .setShowMessage(true)
                .setCancelOutside(false)
                .setCancelable(false)
                .buildHorizontal();
        dialog.show();
    }

    public static void dissmiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
