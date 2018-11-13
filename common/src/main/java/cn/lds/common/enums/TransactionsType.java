package cn.lds.common.enums;

/**
 * 控车远程指令结果
 * Created by leadingsoft on 17/12/11.
 */

public enum TransactionsType {
    /**
     * 未知
     */
    UNKNOW("未知"),
    /**
     * 成功
     */
    SUCCESS("操作成功"),
    /**
     * 等待下发
     */
    WAITING_SEND("车辆不在线"),
    /**
     * 已下发
     */
    SENT("已下发"),
    /**
     * T-BOX不在线
     */
    NOT_ONLINE("T-BOX不在线"),
    /**
     * 升级中
     */
    UPGRADING("升级中"),
    /**
     * 失败
     */
    FAIL("失败"),
    /**
     * 请求TSP失败
     */
    REQUEST_TSP_FAIL("请求TSP失败"),
    /**
     * PING码错误
     */
    PIN_ERROR("PIN码错误"),
    /**
     * 控车超时
     */
    TIME_OUT("控车超时");

    private final String value;

    private TransactionsType(String value) {
        this.value = value;
    }

    public static TransactionsType getType(String value) {
        TransactionsType[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            TransactionsType mtype = var1[var3];
            if (mtype.getValue().equals(value)) {
                return mtype;
            }
        }

        return UNKNOW;
    }

    public String getValue() {
        return this.value;
    }
}
