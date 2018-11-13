package cn.lds.common.enums;

/**
 * 系统类型
 * Created by leadingsoft on 17/12/7.
 */

public enum OsType {
    IOS(1),
    ANDROID(2),
    WEB(3),
    WINDOWS(4),
    UNKNOWN(-1);

    private final int value;

    private OsType(int value) {
        this.value = value;
    }

    public static OsType getOsType(int value) {
        OsType[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            OsType mtype = var1[var3];
            if (mtype.getValue() == value) {
                return mtype;
            }
        }

        return UNKNOWN;
    }

    public int getValue() {
        return this.value;
    }
}
