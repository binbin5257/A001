package cn.lds.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.lds.common.base.BaseFragment;

/**
 * 消息tab碎片
 * Created by sibinbin on 18-5-5.
 */

public class MessageFragment extends BaseFragment {

    public static final String ARGUMENT = "argument";
    /**
     * 弄一个静态工厂的方法调用 用于传参
     * @param key
     * @return
     */
    public static MessageFragment newInStance(String key){
        MessageFragment fragment = new MessageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT,key);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {


        return super.onCreateView(inflater, container, savedInstanceState);


    }
}
