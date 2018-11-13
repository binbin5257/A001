package cn.lds.ui.view;

import cn.lds.R;

/**
 * Created by sibinbin on 18-6-1.
 */

public class UIHelper {

    /**
     * 获取油车油箱图片资源
     * @return
     */
    public static int getOilDrawableRes(int remianOil) {
        if (remianOil == 0) {
            return R.drawable.bg_oil_0;
        } else if (remianOil > 0 && remianOil <= 10) {
            return R.drawable.bg_oil_10;
        } else if (remianOil > 10 && remianOil <= 20) {
            return R.drawable.bg_oil_10;
        } else if (remianOil > 20 && remianOil <= 30) {
            return R.drawable.bg_oil_20;
        } else if (remianOil > 30 && remianOil <= 40) {
            return R.drawable.bg_oil_30;
        } else if (remianOil > 40 && remianOil <= 50) {
            return R.drawable.bg_oil_40;
        } else if (remianOil > 50 && remianOil <= 60) {
            return R.drawable.bg_oil_50;
        } else if (remianOil > 60 && remianOil <= 70) {
            return R.drawable.bg_oil_60;
        } else if (remianOil > 70 && remianOil <= 80) {
            return R.drawable.bg_oil_70;
        } else if (remianOil > 80 && remianOil <= 90) {
            return R.drawable.bg_oil_80;
        } else if (remianOil > 90 && remianOil < 100) {
            return R.drawable.bg_oil_90;
        } else if (remianOil == 100) {
            return R.drawable.bg_oil_100;
        }
        return R.drawable.bg_oil_100;
    }
    /**
     * 获取电车电瓶图片资源
     * @param soc
     * @return
     */
    public static int getEvDrawableRes(int soc) {
        if (soc == 0) {
            return R.drawable.bg_ev_0;
        } else if (soc > 0 && soc <= 10) {
            return R.drawable.bg_ev_10;
        } else if (soc > 10 && soc <= 20) {
            return R.drawable.bg_ev_10;
        } else if (soc > 20 && soc <= 30) {
            return R.drawable.bg_ev_20;
        } else if (soc > 30 && soc <= 40) {
            return R.drawable.bg_ev_30;
        } else if (soc > 40 && soc <= 50) {
            return R.drawable.bg_ev_40;
        } else if (soc > 50 && soc <= 60) {
            return R.drawable.bg_ev_50;
        } else if (soc > 60 && soc <= 70) {
            return R.drawable.bg_ev_60;
        } else if (soc > 70 && soc <= 80) {
            return R.drawable.bg_ev_70;
        } else if (soc > 80 && soc <= 90) {
            return R.drawable.bg_ev_80;
        } else if (soc > 90 && soc < 100) {
            return R.drawable.bg_ev_90;
        } else if (soc == 100) {
            return R.drawable.bg_ev_100;
        }
        return R.drawable.bg_ev_100;
    }
}
