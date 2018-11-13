package cn.lds.common.api;

/**
 * Created by leadingsoft on 2017/11/30.
 */

public class HttpApiKey {
    public static final String login = "common_login";
    public static final String logout = "common_logout";
    public static final String uploadfile = "uploadfile";
    /**
     * 意见反馈列表
     */
    public static final String feedbackList = "feedbackList";
    /**
     * 意见反馈提交
     */
    public static final String feedback = "feedback";
    /**
     * 系统配置项
     */
    public static final String systemConfig = "systemConfig";

    //App -> 远控服务API start－－－－－－－－－－－－－－－－－－－－－－

    /**
     * 空调加热
     */
    public static final String airConditionHeat = "airConditionHeat";
    /**
     * 空调制冷
     */
    public static final String airConditionRefrigerate = "airConditionRefrigerate";
    /**
     * 空调关闭
     */
    public static final String airConditionTurnOff = "airConditionTurnOff";
    /**
     * 获取车辆详情
     */
    public static final String conditionReport = "conditionReport";
    /**
     * 获取车辆详情经纬度
     */
    public static String conditionReportPosition = "conditionReportPosition";
    /**
     * 经销商列表
     */
    public static final String dealer = "dealer";
    /**
     * 车辆体检
     */
    public static final String faultLamp = "faultLamp";
    /**
     * 闪灯鸣笛
     */
    public static final String flashLightWhistle = "flashLightWhistle";
    /**
     * 车门落锁
     */
    public static final String lock = "lock";
    /**
     * 开车门
     */
    public static final String unlock = "unlock";
    /**
     * 行程轨迹
     */
    public static final String track = "track";
    /**
     * 获取远控指令结果
     */
    public static final String transactions = "transactions";
    /**
     * 行程列表
     */
    public static final String trip = "trip";
    /**
     * 车辆远控记录
     */
    public static final String tspLog = "tspLog";
    /**
     * 发动机启动
     */
    public static String startEngine = "startEngine";
    //App -> 远控服务API end－－－－－－－－－－－－－－－－－－－－－－
    // App -> POI服务API start－－－－－－－－－－－－－－－－－－－－－－

    /**
     * 获取收藏列表
     */
    public static final String getCollections = "getCollections";
    /**
     * 添加收藏
     */
    public static final String addCollections = "addCollections";
    /**
     * 取消收藏
     */
    public static final String deleteCollections = "deleteCollections";
    /**
     * 获取 家 或 公司 poi
     */
    public static final String getHomeAndCompany = "getHomeAndCompany";
    /**
     * poi下发
     */
    public static final String postPoi = "postPoi";


    //App -> POI服务API end－－－－－－－－－－－－－－－－－－－－－－
    //App -> 个人信息API  start－－－－－－－－－－－－－－－－－－－－－－

    /**
     * 个人信息详情
     */
    public static final String getPersonalInfo = "getPersonalInfo";
    /**
     * 个人信息修改
     */
    public static final String updatePersonalInfo = "updatePersonalInfo";
    /**
     * 修改登录密码
     */
    public static final String modifyPassword = "modifyPassword";
    /**
     * 修改登录密码
     */
    public static final String getToken = "getToken";
    /**
     * 更新PIN码
     */
    public static final String modifyPin = "modifyPin";
    /**
     * 个人车辆列表
     */
    public static final String getVehicles = "getVehicles";
    /**
     * 个人车辆更新
     */
    public static final String updateVehicle = "updateVehicle";
    /**
     * 获取验证码
     */
    public static final String getVerificationCode = "getVerificationCode";

    /**
     * 重置密码
     */
    public static final String resetPassword = "resetPassword";
    /**
     * 校验验证码
     */
    public static final String checkVerificationCode = "checkVerificationCode";
    /**
     * 校验登录面密码
     */
    public static final String checkPassword = "/m/personal/account/password";

    //App -> 个人信息API  end－－－－－－－－－－－－－－－－－－－－－－

    //App -> 消息api  start－－－－－－－－－－－－－－－－－－－－－－
    /**
     * 获取消息列表
     */
    public static final String getMessages = "getMessages";
    /**
     * 批量删除消息
     */
    public static final String deleMessages = "deleMessages";
    /**
     * 更新pin
     */
    public static final String updatePin = "updatePin";
    public static final String setHomeAndCompany = "setHomeAndCompany";
    public static final String cancelSetHomeAndCompy = "cancelSetHomeAndCompy";
    public static final String findCollectionByCollectionId = "findCollectionByCollectionId";
    public static final String modifyCollected = "modifyCollected";
    public static final String markMessage = "markMessage";
    public static final String cancelSubscribe = "cancelSubscribe";
    public static final String subscribe = "subscribe";

    /**
     * 服务页面活动
     */
    public static final String advertisement = "advertisement";

    /**
     * 获取未读消息数量
     */
    public static final String getMessagesCount = "getMessagesCount";
    /**
     * 删除行程历史
     */
    public static final String deleteTrip = "deleteTrip";


    //App -> 消息api  end－－－－－－－－－－－－－－－－－－－－－－
}
