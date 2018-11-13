package cn.lds.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import cn.lds.MyApplication;
import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.base.BaseFragmentActivity;
import cn.lds.common.manager.ImageManager;
import cn.lds.common.manager.UMengManager;
import cn.lds.common.manager.VersionManager;
import cn.lds.common.table.base.DBManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.ui.fragment.WelcomeFragment;
import cn.lds.widget.viewpager.bean.PageBean;
import cn.lds.widget.viewpager.callback.PageHelperListener;
import cn.lds.widget.viewpager.indicator.TransIndicator;
import cn.lds.widget.viewpager.view.GlideViewPager;
import io.realm.Realm;

/**
 * 欢迎界面
 * 消息tag列表需要登录鉴权之后才能够获取，因此自动登录代码，注释，与1期保持一直
 */
public class WelcomeActivity extends BaseFragmentActivity implements ViewPager.OnPageChangeListener {

        private static final Integer[] RES = {R.mipmap.bg_navi_one,R.mipmap.bg_navi_second,R.mipmap.bg_navi_third,R.mipmap.bg_navi_four};
    private List<View> mViewList = new ArrayList<>();
    private GlideViewPager viewPager;
    private TransIndicator linearLayout;
    private Button button;
    private TextView jumpTv;

    /**
     * 初始化界面
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();
        isFirstOpenApp();

    }

    private void initView() {
        jumpTv = findViewById(R.id.tv_jump);
        viewPager = (GlideViewPager) findViewById(R.id.splase_viewpager);
        linearLayout = (TransIndicator) findViewById(R.id.splase_bottom_layout);
        //点击跳转的按钮
        button = (Button) findViewById(R.id.splase_start_btn);
        ImageView intoOneIcon = (ImageView) findViewById(R.id.icon_into_one);
        ImageView intoTwoIcon = (ImageView) findViewById(R.id.icon_into_two);
        ImageView intoThreeIcon = (ImageView) findViewById(R.id.icon_into_three);
        ImageView intoFourIcon = (ImageView) findViewById(R.id.icon_into_four);
        mViewList.add(intoOneIcon);
        mViewList.add(intoTwoIcon);
        mViewList.add(intoThreeIcon);
        mViewList.add(intoFourIcon);
        jumpTv.setVisibility(View.GONE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                startLoginActivity();
            }
        });
        jumpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                startLoginActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        UMengManager.getInstance().onResumePage("WelcomeActivity");
        UMengManager.getInstance().onClick("WelcomeActivity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        UMengManager.getInstance().onPausePage("WelcomeActivity");
    }

    /**
     * 判断应用是否首次打开
     */
    private void isFirstOpenApp() {
        MyApplication.getInstance().runOnUiThreadDelay(runnable, 1000);

    }

    /**
     * 显示导航页面
     */
    private void showNaviView() {
        //先把本地的图片 id 装进 list 容器中
        List<Integer> images = new ArrayList<>();
        for (int i = 0; i < RES.length; i++) {
            images.add(RES[i]);

        }
        //配置pagerbean，这里主要是为了viewpager的指示器的作用，注意记得写上泛型
        PageBean bean = new PageBean.Builder<Integer>()
                .setDataObjects(images)
                .setIndicator(linearLayout)
                .setInToView(mViewList)
                .setOpenView(button)
                .builder();
        viewPager.setPageListener(bean, R.layout.image_layout, new PageHelperListener() {
            @Override
            public void getItemView(View view, Object data,int positon) {
                ImageView imageView = view.findViewById(R.id.icon);
                imageView.setImageResource((Integer) data);
            }
        });
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String cachVersionCode = CacheHelper.getVersionCode();
            int localVersion = VersionManager.getLocalVersion(WelcomeActivity.this);
            if(CacheHelper.getIsFirstOpen() || (!TextUtils.isEmpty(cachVersionCode) && localVersion > Integer.parseInt(cachVersionCode))){
                //加载导航页面
                jumpTv.setVisibility(View.VISIBLE);
                clearNavtiveData();
                showNaviView();
                CacheHelper.setIsFirstOpen(false);
                CacheHelper.setVersionCode(String.valueOf(localVersion));


            }else{
                startLoginActivity();
            }

        }
    };

    /**
     * 清除本地数据
     */
    private void clearNavtiveData() {
        DBManager.getInstance().getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
        BaseApplication.getInstance().getCache().clear();
    }

    /**
     * 跳转登录界面
     */
    private void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().getHandler().removeCallbacks(runnable);
        runnable = null;
    }

    @Override
    public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ) {

    }

    @Override
    public void onPageSelected( int position ) {

    }

    @Override
    public void onPageScrollStateChanged( int state ) {

    }
}
