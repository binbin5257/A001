package cn.lds.common.enums;

/**
 * 统计事件id
 * Created by leadingsoft on 17/12/11.
 */

public enum UMengEventType {
    OPEN("open"),
    CLOSE("close"),
    UNKNOWN("未知");

    private final String value;

    private UMengEventType(String value) {
        this.value = value;
    }

    public static UMengEventType getDeviceType(int value) {
        UMengEventType[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            UMengEventType mtype = var1[var3];
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
