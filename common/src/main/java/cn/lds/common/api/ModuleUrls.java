package cn.lds.common.api;

/**
 * Created by leadingsoft on 2017/11/30.
 */

import cn.lds.common.manager.ConfigManager;
import cn.lds.common.utils.CacheHelper;

/**
 * api接口汇总
 */
public class ModuleUrls {
    //common start－－－－－－－－－－－－－－－－－－－－－－

    /**
     * 我的订单
     */
    public static String maintanceUrl = ConfigManager.getInstance().getBaseUrl()+"#/maintance?city={city}&adCode={adCode}&vin={vin}&longitude={longitude}&latitude={latitude}&token={token}";
    /**
     * 经销商
     */
    public static String dealerUrl =  ConfigManager.getInstance().getBaseUrl()+"#/dealer?vin={vin}&city={city}&adCode={adCode}&longitude={longitude}&latitude={latitude}&tag=dealer";
    /**
     * 我的订单
     */
    public static String myOrderUrl =  ConfigManager.getInstance().getBaseUrl()+"#/myOrder?vin={vin}&adCode={adCode}&longitude={longitude}&latitude={latitude}&token={token}";
    /**
     * 救援抢险
     */
    public static String rescue =  ConfigManager.getInstance().getBaseUrl()+"#/rescue";

    /**
     * 登录
     */
    public static String login = "m/login";
    /**
     * 注销
     */
    public static String logout = "logout";
    /**
     * 文件上传
     */
    public static String uploadFile = "files";
    /**
     * 文件下载
     */
    public static String downloadFile = "files/{no}";
    /**
     * 系统配置项
     */
    public static String systemConfig = "m/system/config?ver={ver}";

    /**
     * 意见反馈列表
     */
    public static String feedbackList = "m/feedback?page={page}";
    /**
     * 意见反馈提交
     */
    public static String feedback = "m/feedback";

    //common end－－－－－－－－－－－－－－－－－－－－－－

    //App -> 远控服务API start－－－－－－－－－－－－－－－－－－－－－－

    /**
     * 空调加热
     */
    public static String airConditionHeat = "m/vehicles/{vin}/airConditionHeat";
    /**
     * 空调制冷
     */
    public static String airConditionRefrigerate = "m/vehicles/{vin}/airConditionRefrigerate";
    /**
     * 空调关闭
     */
    public static String airConditionTurnOff = "m/vehicles/{vin}/airConditionTurnOff";
    /**
     * 获取车辆详情
     */
    public static String conditionReport = "m/vehicles/{vin}/conditionReport";
    /**
     * 获取车辆详情经纬度
     */
    public static String conditionReportPosition = "m/vehicles/{vin}/conditionReport/position";
    /**
     * 经销商列表
     */
    public static String dealer = "m/vehicles/{vin}/nearDealer?s_provinceCode={provinceCode}&s_cityCode={cityCode}&s_longitude={longitude}&s_latitude={latitude}";
    /**
     * 车辆体检
     */
    public static String faultLamp = "m/vehicles/{vin}/faultLamp";
    /**
     * 闪灯鸣笛
     */
    public static String flashLightWhistle = "m/vehicles/{vin}/flashLightWhistle";
    /**
     * 车门落锁
     */
    public static String lock = "m/vehicles/{vin}/lock";
    /**
     * 开车门
     */
    public static String unlock = "m/vehicles/{vin}/unlock";
    /**
     * 行程轨迹
     */
    public static String track = "m/vehicles/{vin}/track/{uuid}";
    /**
     * 获取远控指令结果
     */
    public static String transactions = "m/vehicles/{vin}/transactions/{transactionId}";
    /**
     * 行程列表
     */
    public static String trip = "m/vehicles/{vin}/trip?s_startTime={s_startTime}&s_endTime={s_endTime}";
    /**
     * 车辆远控记录
     */
    public static String tspLog = "m/vehicles/{vin}/tspLog";
    /**
     * 发动机启动
     */
    public static String startEngine = "m/vehicles/{vin}/engine";

    //App -> 远控服务API end－－－－－－－－－－－－－－－－－－－－－－
    //App -> POI服务API start－－－－－－－－－－－－－－－－－－－－－－

    /**
     * 获取收藏列表
     */
    public static String getCollections = "m/poi/vehicles/{vin}/collections";
    /**
     * 添加收藏
     */
    public static String addCollections = "m/poi/vehicles/{vin}/collections";
    /**
     * 取消收藏
     */
    public static String deleteCollections = "m/poi/vehicles/{vin}/collections/{collectionId}";
    /**
     * 取消设置为家或公司
     */
    public static String cancelSetHomeAndCompy = "m/poi/vehicles/{vin}/collections/cancel/{collectionId}";
    /**
     * 获取 家 或 公司 poi
     */
    public static String getHomeAndCompany = "m/poi/vehicles/{vin}/collection/homeAndCompany";
    /**
     * poi下发
     */
    public static String postPoi = "m/vehicles/{vin}/poi";

    /**
     * 设置收藏为家或公司
     */
    public static String setHomeAndCompany = "m/poi/vehicles/{vin}/collections/{collectionId}/{typeCode}";


    //App -> POI服务API end－－－－－－－－－－－－－－－－－－－－－－
    //App -> 个人信息API  start－－－－－－－－－－－－－－－－－－－－－－

    /**
     * 个人信息详情
     */
    public static String getPersonalInfo = "m/personal/info";
    /**
     * 个人信息修改
     */
    public static String updatePersonalInfo = "m/personal/info";
    /**
     * 修改登录密码
     */
    public static String modifyPassword = "m/personal/modify/password";
    /**
     * 更新PIN码
     */
    public static String modifyPin = "m/personal/pin";
    /**
     * 个人车辆列表
     */
    public static String getVehicles = "m/personal/vehicles";
    /**
     * 个人车辆更新
     */
    public static String updateVehicle = "m/personal/vehicles/{vin}";

    /**
     * 文件上传地址
     */
    public static String fileUpload = "files";

    /**
     * 文件加载地址
     */
    public static String displayFile = ConfigManager.getInstance().getBaseUrl() + "/files/";
    /**
     * 获取验证码地址
     */
    public static String getVerificationCode = "m/personal/captcha/{mobile}";
    /**
     * 重置密码地址
     */
    public static String resetPassword = "m/personal/password";
    public static String getToken = "m/personal/uniform/auth/token";

    /**
     * 校验验证码
     */
    public static String checkVerificationCode = "m/personal/captcha";
    /**
     * 校验登录面密码
     */
    public static String checkPassword = "m/personal/account/password";
    //App -> 个人信息API  end－－－－－－－－－－－－－－－－－－－－－－
    //App -> 消息api  start－－－－－－－－－－－－－－－－－－－－－－
    /**
     * 获取消息列表
     */
    public static String getMessages = "m/message?page={page}";
    /**
     * 获取未读消息数量
     */
    public static String getMessagesCount = "m/message/count";
    /**
     * 批量删除消息
     */
    public static String deleMessages = "m/message";
    public static String updatePin = "m/personal/pin";
    public static String findCollectionByCollectionId = "m/poi/vehicles/{vin}/collection/{collectionId}";
    public static String modifyCollected = "m/poi/vehicles/{vin}/collections/{collectionId}";
    public static String markMessage = "m/message/{id}";
    public static String cancelSubscribe = "m/vehicles/{vin}/dealer/cancel/subscribe";
    public static String subscribe = "m/vehicles/{vin}/dealer/subscribe";

    /**
     * 服务页面banner图
     */
    public static String advertisement = "m/advertisement";
    /**
     * 删除行程历史
     */
    public static String deleteTrip = "m/vehicles/{vin}/trip/{uuid}";

    //App -> 消息api  end－－－－－－－－－－－－－－－－－－－－－－


}
