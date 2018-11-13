package cn.lds.common.image;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.lds.common.R;
import cn.lds.common.databinding.FragmentGifBinding;
import cn.lds.common.manager.ImageManager;


/**
 * gif 显示界面
 */
public class GifFragment extends Fragment {
    private FragmentGifBinding mBinding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_gif, container, false);
//        String url = "";
//        if (null != getArguments()) {
//            url = getArguments().getString("url");
//        }
//        if (ToolsHelper.isNull(url)) {
//            url = "http://img.zcool.cn/community/018003591d063cb5b3086ed4627878.gif";
//        }
//        mBinding.gifImage.setImageURI(Uri.parse(url));

        ImageManager.getInstance().loadLocalGif(R.drawable.test, mBinding.gifImage);
        return mBinding.getRoot();
    }

    public static Fragment newInstance(String url) {
        GifFragment gifFragment = new GifFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        gifFragment.setArguments(bundle);
        return gifFragment;
    }

}
