package cn.lds.common.enums;

/**
 * 设备类别
 * Created by leadingsoft on 17/12/7.
 */

public enum DeviceType {
    PHONE(1),
    PAD(2),
    PC(3),
    UNKNOWN(-1);

    private final int value;

    private DeviceType(int value) {
        this.value = value;
    }

    public static DeviceType getDeviceType(int value) {
        DeviceType[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            DeviceType mtype = var1[var3];
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
