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

import java.io.File;
import java.util.List;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.base.IPermission;
import cn.lds.common.base.UIInitListener;
import cn.lds.common.data.UserInfoModel;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.AccountManager;
import cn.lds.common.table.CarsTable;
import cn.lds.common.table.base.DBManager;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.FileHelper;
import cn.lds.common.utils.PictureHelper;
import cn.lds.databinding.ActivityProfileBinding;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.CameraOrAlbumBottomDialog;
import cn.lds.widget.dialog.LoadingDialogUtils;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;
import cn.lds.widget.listener.OnEnditorListener;
import io.realm.Realm;

/**
 * 个人中心
 * Created by sibinbin on 17-12-13.
 */

public class ProfileActivity extends BaseActivity implements UIInitListener, View.OnClickListener, OnDialogClickListener {

    /**
     * 页面绑定视图对象
     */
    private ActivityProfileBinding binding;
    /**
     * 返回上一个页面按钮
     */
    private ImageView backIv;

    private CameraOrAlbumBottomDialog cameraOrAlbumBottomDialog;

    protected static final int PHOTO_REQUEST_CAMERA = 1;// 拍照

    protected static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择

    protected static final int PHOTO_REQUEST_CUT = 384;// 结果

    private String avatarFilePath; //头像存储路径
    private File temps;
    private UserInfoModel.DataBean intentData;
    private int UPDATE_NICK_NAME =0x123;
    private int UPDATE_CONTACT_NAME =0x124;
    private int UPDATE_CONTACT_PHONE =0x125;


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

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            intentData = (UserInfoModel.DataBean) bundle.getSerializable("USERINFO");
            initUserInfo(intentData);
        }
    }


    /**
     * 初始化页面view
     */
    @Override
    public void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        binding.ivAvatar.setImageResource(R.drawable.bg_headavatar);
        backIv = findViewById(R.id.top_back_iv);
        TextView titleTv = findViewById(R.id.top_title_tv);
        titleTv.setText("个人信息");
        cameraOrAlbumBottomDialog = new CameraOrAlbumBottomDialog(this);

    }
    private void initUserInfo(UserInfoModel.DataBean bean) {
        if(!TextUtils.isEmpty(bean.getAvatarFileRecordNo())){
            //根据id加载图片
            binding.ivAvatar.setImageURI(ModuleUrls.displayFile + bean.getAvatarFileRecordNo());
        }
        if(!TextUtils.isEmpty(bean.getName())){
            binding.userName.setContent(bean.getName());
        }
        if(TextUtils.isEmpty(bean.getNickname())){
            binding.nickName.setContent(bean.getName());
        }else{
            binding.nickName.setContent(bean.getNickname());
        }

        if("0".equals(bean.getGender())){
            binding.sex.setContent("男");
        }else if("1".equals(bean.getGender())){
            binding.sex.setContent("女");
        }

        binding.mobile.setContent(bean.getMobile());
        binding.address.setContent(bean.getAddress());
        binding.contacts1.setContent(bean.getFirstEmLinkerName());
        binding.contacts1Phone.setContent(bean.getFirstEmLinkerMobile());
        binding.contacts2.setContent(bean.getSecEmLinkerName());
        binding.contacts2Phone.setContent(bean.getSecEmLinkerMobile());
    }

    /**
     * 绑定view监听事件
     */
    @Override
    public void initListener() {
        binding.rlAvatar.setOnClickListener(this);
        backIv.setOnClickListener(this);
        cameraOrAlbumBottomDialog.setOnDialogClickListener(this);
        binding.nickName.setOnClickListener(this);
        binding.contacts2.setOnClickListener(this);
        binding.contacts2Phone.setOnClickListener(this);

    }

    /**
     * 个人信息修改成功
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatePersonSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.updatePersonalInfo.equals(apiNo)))
            return;
        switch (AccountManager.updatePersonFlag){
            case AccountManager.updateNickName:
                ToastUtil.showToast(this,"昵称修改成功");
                break;
            case AccountManager.updateContacts:
                ToastUtil.showToast(this,"第二联系人姓名修改成功");
                break;
            case AccountManager.updateAvatar:
                ToastUtil.showToast(this,"头像修改成功");
                break;
        }
        LoadingDialogUtils.dissmiss();

    }

    /**
     * 页面点击事件
     * @param v 点击的view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.rl_avatar:
                selectPic();
                break;
            case R.id.contacts_2:
                updatePersonInfo("修改紧急联系人姓名",binding.contacts2.getContent(),"UPDATE_CONTACT_NAME",UPDATE_CONTACT_NAME);
                break;
            case R.id.contacts_2_phone:
                updatePersonInfo("修改紧急联系人手机号",binding.contacts2Phone.getContent(),"UPDATE_CONTACT_PHONE",UPDATE_CONTACT_PHONE);
                break;
            case R.id.nick_name:
                //修改昵称
                updatePersonInfo("修改昵称",binding.nickName.getContent(),"UPDATE_NICK_NAME",UPDATE_NICK_NAME);
                break;

        }
    }

    /**
     * 修改昵称
     */
    private void updatePersonInfo(String title,String content,String flag,int requestCode) {
        Intent intent = new Intent(this,UpdateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("TITLE",title);
        bundle.putString("CONTENT",content);
        bundle.putString("FLAG",flag);
        intent.putExtras(bundle);
        startActivityForResult(intent,requestCode);
    }

    /**
     * 选择头像
     */
    private void selectPic() {
        cameraOrAlbumBottomDialog.show();
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
                requestWriteStoragePermission();
                break;
            case ClickPosition.CANCEL:
                break;
        }
    }

    /**
     * 请求读写内存卡权限
     */
    private void requestWriteStoragePermission() {
        requestRunTimePermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new IPermission() {
            @Override
            public void onGranted() {
                PictureHelper.enterAlbum(ProfileActivity.this,PHOTO_REQUEST_GALLERY);
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {
//                for (String deniedPermission : deniedPermissions) {
//                    ToastUtil.showToast(ProfileActivity.this, "被拒绝的权限是"+deniedPermission);
//                }
            }
        });
    }

    /**
     * 请求照相权限
     */
    private void requesTakePhotoPermission() {
        BaseActivity.requestRunTimePermission(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, new IPermission() {
            @Override
            public void onGranted() {
                //启动相机
                avatarFilePath = FileHelper.getTakeAvatarPath();
                PictureHelper.takePhoto(ProfileActivity.this,PHOTO_REQUEST_CAMERA,avatarFilePath);
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {
//                for (String deniedPermission : deniedPermissions) {
//                    ToastUtil.showToast(ProfileActivity.this, "被拒绝的权限是"+deniedPermission);
//                }
            }
        });
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
                    File tempFile = new File(avatarFilePath);
                    crop(Uri.fromFile(tempFile));
                }

            } else if (requestCode == PHOTO_REQUEST_CUT) {
                if (null == temps || TextUtils.isEmpty(temps.getPath())) {
                    return;
                }
                binding.ivAvatar.setImageURI(Uri.fromFile(temps));
                //上传头像
                AccountManager.getInstance().uploadAvatar(temps.getPath(), CacheHelper.getAccount()+"avatar");
            } else if(requestCode == UPDATE_NICK_NAME){
                binding.nickName.setContent(data.getStringExtra("nickName"));

            } else if(requestCode == UPDATE_CONTACT_NAME){
                binding.contacts2.setContent(data.getStringExtra("contactName"));

            }
            else if(requestCode == UPDATE_CONTACT_PHONE){
                binding.contacts2Phone.setContent(data.getStringExtra("contactMobile"));

            }
        }
    }

    /**
     * 剪切图片
     *
     * @param uri
     */
    protected void crop(Uri uri) {
        temps = FileHelper.getTemps();
        PictureHelper.crop(this,PHOTO_REQUEST_CUT,uri,temps);
    }
}
