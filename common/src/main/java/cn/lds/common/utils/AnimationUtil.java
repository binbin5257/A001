package cn.lds.common.utils;

import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import cn.lds.common.R;

/**
 * Created by sibinbin on 18-6-1.
 */

public class AnimationUtil {



    /**
     * 执行向上180度旋转的动画
     */
    public static void startRoateUp( Activity act, ImageView iv) {

        Animation upAnimation = AnimationUtils.loadAnimation(act, R.anim.rotate_up_180);
        upAnimation.setFillAfter(true);
        iv.startAnimation(upAnimation);
    }
    /**
     * 执行向下180度旋转的动画
     */
    public static void startRoateDown(Activity act, ImageView iv) {
        Animation downAnimation = AnimationUtils.loadAnimation(act, R.anim.rotate_down_180);
        downAnimation.setFillAfter(true);
        iv.startAnimation(downAnimation);
    }
}
