package cn.lds.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import cn.lds.common.constants.Constants;
import cn.lds.common.manager.ActivityManager;
import cn.lds.common.manager.UMengManager;
import cn.lds.widget.ToastUtil;

public class BaseActivity extends Activity implements UIInitListener {

    protected Context mContext;
    protected boolean canTouch = true;
    private static final int REQUEST_CODE = 1;
    private static IPermission mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ToastUtil.showToast(this,getClass().getSimpleName());
        UMengManager.getInstance().onResumePage(getClass().getSimpleName());
        MobclickAgent.onEvent(mContext,getClass().getSimpleName());
        mContext = this;
        ActivityManager.addActivity(this);


//        throw new NullPointerException("空指针异常。。。我崩溃啦。。。");

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!canTouch)
            return super.dispatchTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideKeyboard(v);
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        ActivityManager.removeActivity(this);
        UMengManager.getInstance().onPausePage(getClass().getSimpleName());
        super.onDestroy();

    }

    /**
     * 强制隐藏键盘
     *
     * @param v
     */
    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null == v || null == v.getWindowToken()) {
        } else {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMengManager.getInstance().onResumeSession(mContext);

    }

    @Override
    protected void onPause() {
        super.onPause();
        UMengManager.getInstance().onPauseSession(mContext);

    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }

    /**
     *请求运行时权限
     * @param permissions
     * @param listener
     */
    public static void requestRunTimePermission(String[] permissions, IPermission listener) {
        Activity topActivity = ActivityManager.getTopActivity();
        mListener = listener;
        if (topActivity == null) {
            return;
        }
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(topActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(topActivity, permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE);
        } else {
            //doSomething
            mListener.onGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        mListener.onGranted();
                    } else {
                        mListener.onDenied(deniedPermissions);
                    }
                }
                break;
            default:
                break;
        }
    }
    @Override
    protected void onRestoreInstanceState( Bundle savedInstanceState ) {
        BaseApplication.getInstance().sendLogoutBroadcast(Constants.SYS_CONFIG_LOGOUT_FLITER, "用户认证失败");
        super.onRestoreInstanceState(savedInstanceState);
    }

}
