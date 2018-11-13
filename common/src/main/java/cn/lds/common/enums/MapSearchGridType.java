package cn.lds.common.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 远程控制枚举
 * Created by leadingsoft on 17/12/18.
 */

public enum MapSearchGridType {
    /**
     * 充电桩
     */
    CHONGDIANZHUANG("充电桩"),
    /**
     * 停车场
     */
    TINGCHECHANG("停车场"),
    /**
     * 商场
     */
    SHANGCHANG("商场"),
    /**
     * 酒店
     */
    JIUDIAN("酒店"),
    /**
     * 收藏夹
     */
    SHOUCANGJIA("收藏夹"),
    /**
     * 加油站
     */
    JIAYOUZHAN("加油站"),
    /**
     * 经销商
     */
    JINGXIAOSHANG("经销商"),
    /**
     * 更多
     */
    GENGDUO("更多");
    private final String value;

    private MapSearchGridType(String value) {
        this.value = value;
    }

    public static MapSearchGridType getType(String value) {
        MapSearchGridType[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            MapSearchGridType mtype = var1[var3];
            if (mtype.getValue().equals(value)) {
                return mtype;
            }
        }

        return GENGDUO;
    }

    public String getValue() {
        return this.value;
    }

    public static List<MapSearchGridType> getList() {
        List<MapSearchGridType> list = new ArrayList<>();
        list.add(CHONGDIANZHUANG);
        list.add(TINGCHECHANG);
        list.add(SHANGCHANG);
        list.add(JIUDIAN);
        list.add(SHOUCANGJIA);
        list.add(JIAYOUZHAN);
        list.add(JINGXIAOSHANG);
        list.add(GENGDUO);
        return list;
    }
}
