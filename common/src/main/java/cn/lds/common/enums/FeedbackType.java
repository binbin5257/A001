package cn.lds.common.enums;

/**
 * 意见反馈，类型
 * Created by leadingsoft on 18/1/19.
 */

public enum FeedbackType {
    ACTIVATION_ACCOUNT("激活账户"),
    REPAIR_MAINTAIN("维修保养"),
    USING_APP("app使用"),
    OTHER("其他");

    private final String value;

    private FeedbackType(String value) {
        this.value = value;
    }

    public static FeedbackType getType(String value) {
        FeedbackType[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            FeedbackType mtype = var1[var3];
            if (mtype.getValue().equals(value)) {
                return mtype;
            }
        }

        return OTHER;
    }

    public String getValue() {
        return this.value;
    }
}
