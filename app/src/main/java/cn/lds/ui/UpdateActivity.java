package cn.lds.ui;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.AccountManager;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.databinding.ActivityUpdateBinding;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.LoadingDialogUtils;

/**
 * 修改页面
 * Created by sibinbin on 18-3-9.
 */

public class UpdateActivity extends BaseActivity implements View.OnClickListener {

    private String title;
    private String flag;
    private ActivityUpdateBinding updateBinding;
    private TextView topTitle;
    private String content;
    private String carVin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString("TITLE");
            content = bundle.getString("CONTENT");
            flag = bundle.getString("FLAG");
            topTitle.setText(title);


            if (flag.equals("UPDATE_NICK_NAME")) {
                updateBinding.etInput.setHint("请输入昵称");
                updateBinding.etInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                updateBinding.etInput.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                if(content.length() > 10){
                    content =  content.substring( 0,10);
                }
            } else if (flag.equals("UPDATE_LICENSE_PLATE")) {
                carVin = bundle.getString("CAR_VIN");
                updateBinding.etInput.setHint("请输入车牌号");
                updateBinding.etInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                updateBinding.etInput.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                if(content.length() > 8){
                    content =  content.substring( 0,8);
                }

            } else if (flag.equals("UPDATE_CONTACT_NAME")) {
                updateBinding.etInput.setHint("请输入联系人姓名");
                updateBinding.etInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                updateBinding.etInput.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                if(content.length() > 10){
                    content =  content.substring( 0,10);
                }
            } else if (flag.equals("UPDATE_CONTACT_PHONE")) {
                updateBinding.etInput.setHint("请输入手机号");
                updateBinding.etInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                updateBinding.etInput.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                if(content.length() > 11){
                    content =  content.substring( 0,11);
                }
            }
            updateBinding.etInput.setText(content);
            updateBinding.etInput.setSelection(content.length());


        }
    }

    @Override
    public void initView() {
        updateBinding = DataBindingUtil.setContentView(this, R.layout.activity_update);
        topTitle = updateBinding.getRoot().findViewById(R.id.top_title_tv);
        TextView saveTv = findViewById(R.id.top_menu_extend_iv);
        saveTv.setText("保存");

    }

    @Override
    public void initListener() {
        updateBinding.getRoot().findViewById(R.id.top_menu_lyt).setOnClickListener(this);
        updateBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        showSoftInputFromWindow(this, updateBinding.etInput);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.top_menu_lyt:
                save();
                break;
        }
    }

    /**
     * 保存
     */
    private void save() {
        if (updateBinding.etInput.getText().toString().equals(content)) {
            finish();
            return;
        }
        if (updateBinding.etInput.getText().toString().isEmpty() && flag.equals("UPDATE_LICENSE_PLATE")) {
            ToastUtil.showToast(this, "车牌号不能为空");

            return;
        }
//        if(flag.equals("UPDATE_CONTACT_PHONE") && TextUtils.isEmpty(updateBinding.etInput.getText().toString())){
//            ToastUtil.showToast(this,"手机号不能为空");
//            return;
//        }
        if (flag.equals("UPDATE_CONTACT_PHONE") && !TextUtils.isEmpty(updateBinding.etInput.getText().toString()) && updateBinding.etInput.getText().toString().length() != 11) {
            ToastUtil.showToast(this, "手机号格式不正确");
            return;
        }else if(flag.equals("UPDATE_LICENSE_PLATE") && !TextUtils.isEmpty(updateBinding.etInput.getText().toString()) && !ToolsHelper.isCarNumber(updateBinding.etInput.getText().toString())){
            ToastUtil.showToast(this, "车牌号格式不正确");
            return;
        }

        LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
        if (flag.equals("UPDATE_NICK_NAME")) {
            AccountManager.getInstance().putPersonInfo(updateBinding.etInput.getText().toString(), AccountManager.updateNickName);
        } else if (flag.equals("UPDATE_LICENSE_PLATE")) {
            updateCarLisenceNo(updateBinding.etInput.getText().toString());
        } else if (flag.equals("UPDATE_CONTACT_NAME")) {
            AccountManager.getInstance().putPersonInfo(updateBinding.etInput.getText().toString(), AccountManager.updateContacts);
        } else if (flag.equals("UPDATE_CONTACT_PHONE")) {
            //请求服务器修改昵称
            AccountManager.getInstance().putPersonInfo(updateBinding.etInput.getText().toString(), AccountManager.updateContactPhone);
        }
    }

    /**
     * 修改车牌号码
     *
     * @param content
     */
    private void updateCarLisenceNo(String content) {
        LoadingDialogUtils.showVertical(mContext, "请稍候");
        JSONObject json = new JSONObject();
        try {
            json.put("licensePlate", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ModuleUrls.updateVehicle.
                replace("{vin}", carVin);
        RequestManager.getInstance().put(url, HttpApiKey.updateVehicle, json.toString());
    }


    /**
     * 成功
     *
     * @param event 成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.updatePersonalInfo.equals(apiNo)
                || HttpApiKey.updateVehicle.equals(apiNo))) {
            return;
        }
        if (HttpApiKey.updateVehicle.equals(apiNo)) {
            //更新本地数据
            updateSuccess("car_no", "车牌号修改成功");
        } else {
            switch (AccountManager.updatePersonFlag) {
                case AccountManager.updateNickName:
                    updateSuccess("nickName", "昵称修改成功");
                    break;
                case AccountManager.updateContacts:
                    updateSuccess("contactName", "联系人姓名修改成功");
                    break;
                case AccountManager.updateContactPhone:
                    updateSuccess("contactMobile", "联系人手机号修改成功");
                    break;

            }
        }

        LoadingDialogUtils.dissmiss();

    }

    private void updateSuccess(String flag, String tostStr) {
        ToastUtil.showToast(this, tostStr);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(flag, updateBinding.etInput.getText().toString().trim());
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * api请求失败
     *
     * @param event 失败返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestFailed(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.tspLog.equals(apiNo)))
            return;
        LoadingDialogUtils.dissmiss();
        ToolsHelper.showHttpRequestErrorMsg(mContext, httpResult);

    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

}
