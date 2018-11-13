package cn.lds.common.base;


import cn.lds.common.manager.UMengManager;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by leadingsoft on 17/12/11.
 */

public class BaseFragment extends Fragment {
    protected String className;
    public Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    public void setClassName(String className) {
        this.className = className;

    }

    @Override
    public void onResume() {
        super.onResume();
//        UMengManager.getInstance().onResumePage(this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
//        UMengManager.getInstance().onResumePage(this.getClass().getSimpleName());
    }
}
