package cn.lds.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.base.IPermission;
import cn.lds.common.base.UIInitListener;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.ConfigManager;
import cn.lds.common.manager.ImageManager;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.table.CarsTable;
import cn.lds.common.table.base.DBManager;
import cn.lds.common.utils.FileHelper;
import cn.lds.common.utils.PictureHelper;
import cn.lds.databinding.ActivityCarDetailBinding;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.CameraOrAlbumBottomDialog;
import cn.lds.widget.dialog.LoadingDialogUtils;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;
import cn.lds.widget.listener.OnEnditorListener;
import io.realm.Realm;

/**
 * 车辆详情页面
 * Created by sibinbin on 17-12-18.
 */

public class CarDetailActivity extends BaseActivity implements UIInitListener, View.OnClickListener, OnDialogClickListener {

    private static final int UPDATE_CAR_NO = 1024;
    private ImageView backIv;
    private ActivityCarDetailBinding mBinding;
    private CameraOrAlbumBottomDialog cameraOrAlbumBottomDialog;

    protected final int PHOTO_REQUEST_CAMERA = 1;// 拍照

    protected final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择

    protected final int PHOTO_REQUEST_CUT = 384;// 结果

    private String carFilePath; //车辆图片存储路径
    private File temps;
    private String carVin;
    private InputMethodManager imm;
    private String carLicensePlate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initListener();
        getData();
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

    @Override
    public void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_car_detail);
        TextView titile = mBinding.getRoot().findViewById(R.id.top_title_tv);
        backIv = findViewById(R.id.top_back_iv);
        cameraOrAlbumBottomDialog = new CameraOrAlbumBottomDialog(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        titile.setText("车辆信息");
    }

    @Override
    public void initListener() {
        backIv.setOnClickListener(this);
        cameraOrAlbumBottomDialog.setOnDialogClickListener(this);
        mBinding.rlCarIcon.setOnClickListener(this);
        mBinding.carLisenceNo.setOnClickListener(this);
    }

    /**
     * 修改车牌号码
     * @param content
     */
    private void updateCarLisenceNo(String content) {
        LoadingDialogUtils.showVertical(mContext, "请稍候");
        JSONObject json = new JSONObject();
        try {
            json.put("licensePlate",content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ModuleUrls.updateVehicle.
                replace("{vin}", carVin);
        RequestManager.getInstance().put(url, HttpApiKey.updateVehicle,json.toString());
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.rl_car_icon:
//                cameraOrAlbumBottomDialog.show();
                break;
            case R.id.car_lisence_no:
                enterUpdateCarLisenceNo();
                break;
        }
    }

    /**
     * 进入修改车牌号页面
     */
    private void enterUpdateCarLisenceNo() {
        Intent intent = new Intent(this,UpdateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("TITLE","修改车牌号");
        bundle.putString("CONTENT",carLicensePlate);
        bundle.putString("FLAG","UPDATE_LICENSE_PLATE");
        bundle.putString("CAR_VIN",carVin);
        intent.putExtras(bundle);
        startActivityForResult(intent,UPDATE_CAR_NO);

    }

    /**
     * 对话框点击事件
     * @param dialog
     * @param clickPosition
     */
    @Override
    public void onDialogClick(Dialog dialog, String clickPosition) {
        dialog.dismiss();
        switch (clickPosition) {
            case ClickPosition.TAKE_PHOTO:  //照相
                requesTakePhotoPermission();
                break;
            case ClickPosition.TAKE_ALBUM://从相册中选择
                PictureHelper.enterAlbum(this,PHOTO_REQUEST_GALLERY);
                break;
            case ClickPosition.CANCEL:
                break;
        }
    }

    /**
     * 请求照相权限
     */
    private void requesTakePhotoPermission() {
        BaseActivity.requestRunTimePermission(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, new IPermission() {



            @Override
            public void onGranted() {
                //启动相机
                carFilePath = FileHelper.getCarIconPath();
                PictureHelper.takePhoto(CarDetailActivity.this,PHOTO_REQUEST_CAMERA,carFilePath);
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {
                for (String deniedPermission : deniedPermissions) {
//                    ToastUtil.showToast(CarDetailActivity.this, "被拒绝的权限是"+deniedPermission);
                }
            }
        });
    }
    private void getData() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String carMode = bundle.getString("CAR_MODE");
            carLicensePlate = bundle.getString("CAR_LICENSEPLATE");
            carVin = bundle.getString("CAR_VIN");
            if(!TextUtils.isEmpty(carMode)){
                mBinding.carMode.setContent(carMode);
            }else{
                mBinding.carMode.setContent("未知车型");
            }
            mBinding.carNo.setContent(carVin);
            mBinding.carLisenceNo.setContent(carLicensePlate);
            //TODO 车辆图片id
            String imageId = bundle.getString("CAR_IMAGE_ID");
            String requestUrl = String.format("%s/%s", ConfigManager.getInstance().getBaseUrl(), ModuleUrls.downloadFile.replace("{no}",imageId));
            mBinding.ivCar.setImageURI(requestUrl);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == PHOTO_REQUEST_GALLERY ) {
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    crop(uri);
                }
            } else if (requestCode == PHOTO_REQUEST_CAMERA) {
                if (FileHelper.existSDCard()) {
                    File tempFile = new File(carFilePath);
                    crop(Uri.fromFile(tempFile));
                }

            } else if (requestCode == PHOTO_REQUEST_CUT) {
                if (null == temps || TextUtils.isEmpty(temps.getPath())) {
                    return;
                }
                mBinding.ivCar.setImageURI(Uri.fromFile(temps));
//                LoadingDialog.showDialog(context, getString(R.string.profileactivity_uploading));
                //上传图片
//                FileManager.getInstance().uploadChatImage(temps.getPath(), mAccountsTable.getAccount());

            } else if(requestCode == UPDATE_CAR_NO){
                Bundle bundle = data.getExtras();
                if(bundle != null){
                    final String carNo = bundle.getString("car_no");
                    carLicensePlate = carNo;
                    mBinding.carLisenceNo.setContent(carNo);
                    //更新本地数据库车辆信息
                    updateCarNoByVinDb(carNo);
                }
            }
        }
    }

    private void updateCarNoByVinDb(final String carNo ) {
        Realm realm = DBManager.getInstance().getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                CarsTable table =  realm.where(CarsTable.class).equalTo("vin", carVin).findFirst();
                table.setLicensePlate(carNo);
                realm.copyToRealmOrUpdate(table);
            }
        });
        realm.close();
    }

    /**
     * 剪切图片
     *
     * @param uri
     */
    protected void crop(Uri uri) {
        temps = FileHelper.getCarTemps();
        PictureHelper.cropCar(this,PHOTO_REQUEST_CUT,uri, temps);
    }



}
