package cn.lds.common.manager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.constants.Constants;
import cn.lds.common.data.CarControlModel;
import cn.lds.common.data.CollectionsModel;
import cn.lds.common.data.ConditionReportModel;
import cn.lds.common.data.postbody.PoiPostBean;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.widget.ToastUtil;

/**
 * Created by leadingsoft on 2017/11/30.
 * 车辆控制类
 */

public class CarControlManager {
    private static final String TAG = CarControlManager.class.getSimpleName();
    private String transactionId;
    private boolean stopLoop = true;
    private CollectionsModel.DataBean home, company;
    private ConditionReportModel.DataBean carDetail;//车辆基本信息
    private String apiNo;
    private int loopTimes = 0; //




    /**
     * 内部类实现单例模式
     * 延迟加载，减少内存开销
     *
     * @author leadingsoft
     */
    private static class CarControlHolder {
        private static CarControlManager instance = new CarControlManager();
    }

    public static CarControlManager getInstance() {
        try {
            if (!EventBus.getDefault().isRegistered(CarControlHolder.instance))
                EventBus.getDefault().register(CarControlHolder.instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CarControlHolder.instance;
    }


    /**
     * 车辆控制api
     *
     * @param apiNo
     *         API类型
     * @param pin
     *         pin码
     */
    public void requestControl(String apiNo, String pin) {
        String url;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pin", pin);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (HttpApiKey.airConditionHeat.equals(apiNo)) {
            url = ModuleUrls.airConditionHeat;
        } else if (HttpApiKey.airConditionRefrigerate.equals(apiNo)) {
            url = ModuleUrls.airConditionRefrigerate;
        } else if (HttpApiKey.airConditionTurnOff.equals(apiNo)) {
            url = ModuleUrls.airConditionTurnOff;
        } else if (HttpApiKey.flashLightWhistle.equals(apiNo)) {
            url = ModuleUrls.flashLightWhistle;
        } else if (HttpApiKey.lock.equals(apiNo)) {
            url = ModuleUrls.lock;
        } else if (HttpApiKey.unlock.equals(apiNo)) {
            url = ModuleUrls.unlock;
        } else if (HttpApiKey.startEngine.equals(apiNo)) {
            url = ModuleUrls.startEngine;
            try {
                jsonObject.put("startShutDownTime", Constants.SYS_CONFIG_ENGINE_WAIT);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
        url = url.replace("{vin}", CacheHelper.getVin());
        RequestManager.getInstance().post(url, apiNo, jsonObject.toString());
    }


    /**
     * 获取远控指令结果
     *
     * @transactionId 跟踪id
     */
    public void transactions(String transactionId) {
        String url = ModuleUrls.transactions.replace("{vin}", CacheHelper.getVin()).replace("{transactionId}", transactionId);
        Map<String, String> extras = new HashMap<>();
        extras.put("apiNo", apiNo);
        RequestManager.getInstance().post(url, HttpApiKey.transactions, "", extras);
    }

    /**
     * 车辆体检
     */
    public void faultLamp() {
        String url = ModuleUrls.faultLamp.replace("{vin}", CacheHelper.getVin());
        RequestManager.getInstance().get(url, HttpApiKey.faultLamp);
    }

    /**
     * 获取车辆详情
     */
    public void conditionReport() {
        String url = ModuleUrls.conditionReport.replace("{vin}", CacheHelper.getVin());
        RequestManager.getInstance().get(url, HttpApiKey.conditionReport);

    }

    /**
     * 获取车辆位置
     */
    public void conditionReportPosition() {
        String url = ModuleUrls.conditionReportPosition.replace("{vin}", CacheHelper.getVin());
        RequestManager.getInstance().get(url, HttpApiKey.conditionReportPosition);
    }

    /**
     * 下发poi到车机
     *
     * @param dataBean
     *         poi位置信息
     */
    public void postPoi(CollectionsModel.DataBean dataBean) {
        String url = ModuleUrls.postPoi.replace("{vin}", CacheHelper.getVin());
        PoiPostBean postPoi = new PoiPostBean();
        PoiPostBean.PoiNodeBean poiNodeBean = new PoiPostBean.PoiNodeBean();
        poiNodeBean.setDestinations(dataBean.getAddress());
        poiNodeBean.setLatitude(dataBean.getLatitude());
        poiNodeBean.setLongitude(dataBean.getLongitude());
        postPoi.setPoiNode(poiNodeBean);
        String json = GsonImplHelp.get().toJson(postPoi);
        RequestManager.getInstance().post(url, HttpApiKey.postPoi, json);
    }

    public void findCollectionByCollectionId(String conllectionId){
        String url = ModuleUrls.findCollectionByCollectionId.replace("{vin}", CacheHelper.getVin()).replace("{collectionId}", conllectionId);
        RequestManager.getInstance().get(url, HttpApiKey.findCollectionByCollectionId);
    }

    /**
     * 获取收藏列表
     */
    public void getCollections() {
        String url = ModuleUrls.getCollections.replace("{vin}", CacheHelper.getVin());
        RequestManager.getInstance().get(url, HttpApiKey.getCollections);
    }

    /**
     * 设置收藏为家或公司
     * @param collectId 收藏id
     * @param vin vin
     * @param typeCode 1,家;2,公司
     */
    public void setHomeAndCompany( String collectId, String vin, int typeCode ) {
        String url = ModuleUrls.setHomeAndCompany.replace("{vin}", vin).replace("{collectionId}", collectId).replace("{typeCode}", typeCode+"");
        RequestManager.getInstance().post(url, HttpApiKey.setHomeAndCompany);
    }

    /**
     * 获取家和公司地址
     */
    public void getHomeAndCompany() {
        String url = ModuleUrls.getHomeAndCompany.replace("{vin}", CacheHelper.getVin());
        RequestManager.getInstance().get(url, HttpApiKey.getHomeAndCompany);
    }
    /**
     * 取消收藏为公司或家
     *
     * @param collectionId
     *         收藏id
     */
    public void cancelSetHomeAndCompy(String collectionId){
        String url = ModuleUrls.cancelSetHomeAndCompy.replace("{vin}", CacheHelper.getVin()).replace("{collectionId}", collectionId);
        RequestManager.getInstance().post(url, HttpApiKey.cancelSetHomeAndCompy);

    }

    /**
     * 删除收藏
     *
     * @param collectionId
     *         收藏id
     */
    public void deleCollection(String collectionId) {
        String url = ModuleUrls.deleteCollections.replace("{vin}", CacheHelper.getVin()).replace("{collectionId}", collectionId);
        RequestManager.getInstance().delete(url, HttpApiKey.deleteCollections);
    }

    /**
     * 添加收藏
     */
    public void addCollection(CollectionsModel.DataBean dataBean, final Map<String, String> extras) {
        String url = ModuleUrls.addCollections.replace("{vin}", CacheHelper.getVin());
        String json = GsonImplHelp.get().toJson(dataBean);
        RequestManager.getInstance().post(url, HttpApiKey.addCollections, json, extras);
    }

    /**
     * 修改poi收藏名称
     * @param name
     */
    public void modifyCollected(String name,String collectionId){

        try {
            String url = ModuleUrls.modifyCollected.replace("{vin}", CacheHelper.getVin()).replace("{collectionId}", collectionId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            RequestManager.getInstance().put(url, HttpApiKey.modifyCollected, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * 车辆控制api请求成功
     *
     * @param event
     *         返回数据
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void requestControlSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.airConditionHeat.equals(apiNo) ||
                HttpApiKey.airConditionRefrigerate.equals(apiNo) ||
                HttpApiKey.airConditionTurnOff.equals(apiNo) ||
                HttpApiKey.flashLightWhistle.equals(apiNo) ||
                HttpApiKey.lock.equals(apiNo) ||
                HttpApiKey.unlock.equals(apiNo) ||
                HttpApiKey.startEngine.equals(apiNo)
        ))
            return;
        CarControlModel carControlModel = GsonImplHelp.get().toObject(httpResult.getResult(), CarControlModel.class);
        if (null != carControlModel) {
            transactionId = carControlModel.getData().getTransactionId();
            stopLoop = false;
            startLoop(0, apiNo);
        }


    }

    /**
     * 车辆控制api请求失败
     *
     * @param event
     *         返回数据
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void requestControlFailed(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.airConditionHeat.equals(apiNo) ||
                HttpApiKey.airConditionRefrigerate.equals(apiNo) ||
                HttpApiKey.airConditionTurnOff.equals(apiNo) ||
                HttpApiKey.flashLightWhistle.equals(apiNo) ||
                HttpApiKey.lock.equals(apiNo) ||
                HttpApiKey.unlock.equals(apiNo)
        ))
            return;
        transactionId = "";
        HttpResult httpResult1 = new HttpResult(HttpApiKey.transactions, httpResult.getUrl(), httpResult.getResult());
        HttpRequestErrorEvent httpRequestErrorEvent = new HttpRequestErrorEvent(httpResult1);
        EventBus.getDefault().post(httpRequestErrorEvent);
    }


    /**
     * 轮询接口
     */
    Runnable loopRunnable = new Runnable() {
        @Override
        public void run() {
            if (ToolsHelper.isNull(transactionId) || stopLoop) {
                HttpResult httpResult = new HttpResult(HttpApiKey.transactions, "", "", null);
                if(stopLoop = true){
                    httpResult.setResult("Control_car_Time_out");
                }
                HttpRequestErrorEvent event = new HttpRequestErrorEvent(httpResult);
                EventBus.getDefault().post(event);
                return;
            }
            transactions(transactionId);
        }
    };
    /**
     * 定时器
     */
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            stopLoop();
        }
    };

    /**
     * 开始轮询
     */
    public void startLoop(long delay, String apiNo) {
        BaseApplication.getInstance().runOnUiThreadDelay(loopRunnable, delay);
        this.apiNo = apiNo;
        if (0 == delay) {
            stopLoop = false;
            BaseApplication.getInstance().runOnUiThreadDelay(timerRunnable, Constants.SYS_CONFIG_LOOP_TIME);
        }
    }

    /**
     * 结束轮询
     */
    public void stopLoop() {
        stopLoop = true;
    }

    /**
     * 停止定时器
     */
    public void stopTimer(){
        if(timerRunnable != null){
            BaseApplication.getInstance().getHandler().removeCallbacks(timerRunnable);
        }
    }


    /**
     * 车辆基本信息
     */
    public ConditionReportModel.DataBean getCarDetail() {
        return carDetail;
    }

    public CollectionsModel.DataBean getCompany() {
        return company;
    }

    public CollectionsModel.DataBean getHome() {
        return home;
    }

    public void setCarDetail(ConditionReportModel.DataBean carDetail) {
        this.carDetail = carDetail;
    }

    public void setHome(CollectionsModel.DataBean home) {
        this.home = home;
    }

    public void setCompany(CollectionsModel.DataBean company) {
        this.company = company;
    }

    /**
     * manager回收
     */
    public void onDestory() {
        BaseApplication.getInstance().getHandler().removeCallbacks(timerRunnable);
        BaseApplication.getInstance().getHandler().removeCallbacks(loopRunnable);

        EventBus.getDefault().unregister(CarControlHolder.instance);
        CarControlHolder.instance = null;
    }

}
