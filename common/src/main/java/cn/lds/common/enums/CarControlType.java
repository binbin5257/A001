package cn.lds.common.enums;

/**
 * 远程控制枚举
 * Created by leadingsoft on 17/12/18.
 */

public enum CarControlType {
    /**
     * 未知
     */
    UNKNOWN("未知"),
    /**
     * 开门
     */
    UNLOCK("开车锁"),
    /**
     * 关门
     */
    LOCK("关车锁"),
    /**
     * 闪灯鸣笛
     */
    FLASHLIGHTWHISTLE("闪灯鸣笛"),
    /**
     * 空调加热
     */
    AIRCONDITIONHEAT("空调加热"),
    /**
     * 空调制冷
     */
    AIRCONDITIONREFRIGERATE("空调制冷"),
    /**
     * 空调关闭
     */
    AIRCONDITIONTURNOFF("关闭空调"),
    /**
     * 发动机启动
     */
    ENGINESTART("开启引擎");
    private final String value;

    private CarControlType(String value) {
        this.value = value;
    }

    public static CarControlType getType(String value) {
        CarControlType[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            CarControlType mtype = var1[var3];
            if (mtype.getValue().equals(value)) {
                return mtype;
            }
        }

        return UNKNOWN;
    }

    public String getValue() {
        return this.value;
    }
}
