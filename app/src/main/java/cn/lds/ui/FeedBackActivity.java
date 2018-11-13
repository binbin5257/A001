package cn.lds.ui;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.base.IPermission;
import cn.lds.common.data.FilesModel;
import cn.lds.common.enums.FeedbackType;
import cn.lds.common.file.FilesBean;
import cn.lds.common.file.ProgressFileUploadListener;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.CarControlManager;
import cn.lds.common.manager.FilesManager;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.photoselector.PickerActivity;
import cn.lds.common.photoselector.PickerConfig;
import cn.lds.common.photoselector.adapter.SpacingDecoration;
import cn.lds.common.photoselector.entity.Media;
import cn.lds.common.table.CarsTable;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.LogHelper;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityFeedBackBinding;
import cn.lds.ui.adapter.FeedBackGridAdapter;
import cn.lds.ui.adapter.PictureGridAdapter;
import cn.lds.ui.select_image.MainConstant;
import cn.lds.ui.select_image.PictureSelectorConfig;
import cn.lds.ui.select_image.PlusImageActivity;
import cn.lds.widget.dialog.LoadingDialogUtils;
import okhttp3.Call;
import okhttp3.Response;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.tools.PictureFileUtils;


/**
 * 意见反馈－－提交
 */
public class FeedBackActivity extends BaseActivity implements View.OnClickListener {
    ActivityFeedBackBinding mBinding;
//    ArrayList<Media> select;
//    PictureGridAdapter gridAdapter;
    ArrayList<String> picsNo = new ArrayList<>();
    FeedbackType feedbackType;
    private FeedBackGridAdapter adapter;
    List<LocalMedia> localMediaList = new ArrayList<>();
    ArrayList<String> picShowPath = new ArrayList<>();

    Handler handler = new Handler(){
        @Override
        public void handleMessage( Message msg ) {
            refrushAdapter();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_feed_back);
        initView();
        initListener();
        refrushAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
        PictureFileUtils.deleteCacheDirFile(this);
    }

    @Override
    public void initView() {
        TextView topTitle = mBinding.getRoot().findViewById(R.id.top_title_tv);
        topTitle.setText(getString(R.string.feedback_title));
        List<String> data_list = new ArrayList<>();
        data_list.add(FeedbackType.ACTIVATION_ACCOUNT.getValue());
        data_list.add(FeedbackType.REPAIR_MAINTAIN.getValue());
        data_list.add(FeedbackType.USING_APP.getValue());
        data_list.add(FeedbackType.OTHER.getValue());
        createAdapter();
    }

    private void createAdapter() {


        mBinding.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( final AdapterView<?> parent, View view, final int position, long id ) {
                requestRunTimePermission(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, new IPermission() {
                    @Override
                    public void onGranted() {
                        gridItemClick(parent, position);
                    }

                    @Override
                    public void onDenied( List<String> deniedPermissions ) {

                    }
                });

            }
        });

    }

    /**
     * gridView 条目点击事件
     * @param parent
     * @param position
     */
    private void gridItemClick( AdapterView<?> parent, int position ) {
        if (position == parent.getChildCount() - 1) {
            //如果“增加按钮形状的”图片的位置是最后一张，且添加了的图片的数量不超过5张，才能点击
            if (picShowPath.size() == 9) {
                viewPluImg(position);
            } else {
                //添加凭证图片
                selectPic(MainConstant.MAX_SELECT_PIC_NUM - picShowPath.size());
            }
        } else {
            viewPluImg(position);
        }
    }

    /**
     * 打开相册或者照相机选择凭证图片，最多5张
     *
     * @param maxTotal 最多选择的图片的数量
     */
    private void selectPic(int maxTotal) {
        PictureSelectorConfig.initMultiConfig(this, maxTotal,localMediaList);
    }
    //查看大图
    private void viewPluImg(int position) {
        Intent intent = new Intent(mContext, PlusImageActivity.class);
        intent.putStringArrayListExtra(MainConstant.IMG_LIST, picShowPath);
        intent.putExtra(MainConstant.POSITION, position);
        startActivityForResult(intent, MainConstant.REQUEST_CODE_MAIN);
    }

    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        mBinding.inputEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mBinding.textSize.setText(String.format("%d/140", charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mBinding.uploadFeedback.setOnClickListener(this);
        mBinding.addPic.setOnClickListener(this);


        mBinding.feedbackType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = ((TextView) view).getText().toString();
                feedbackType = FeedbackType.getType(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.upload_feedback:
                submitFeedBack();
                break;
        }
    }

    /**
     * 提交意见反馈
     *
     */
    private void submitFeedBack() {
        if (0 >= mBinding.inputEdittext.getText().length()) {
            ToolsHelper.showInfo(mContext, "请输入意见");
            return;
        }
//        if (null == feedbackType) {
//            ToolsHelper.showInfo(mContext, "请选择问题类型");
//            return;
//        }
        LoadingDialogUtils.showVertical(mContext, getString(R.string.loading_waitting));
        if (null == picShowPath || picShowPath.isEmpty()) {
            uploadFeedback(null);
        } else {
            uploadPic(0);//上传第一张图片
        }
    }

    private void uploadPic(final int i) {
        FilesManager.getInstance().uploadFile(subShowPath(i), new ProgressFileUploadListener() {
            @Override
            public void onLoading(long totalSize, long currSize) {

            }

            @Override
            public void loadSuccess(Response response) throws IOException {
                String s = response.body().string();
                LogHelper.d(s);
                FilesModel model = GsonImplHelp.get().toObject(s, FilesModel.class);
                List<FilesBean> filesBeanList = model.getData();
                for (int i = 0; i < filesBeanList.size(); i++) {
                    picsNo.add(filesBeanList.get(i).getNo());
                }
                if (picsNo.size() == picShowPath.size()) {
                    uploadFeedback(picsNo);
                } else {
                    int j = i + 1;
                    uploadPic(j);//上传下一张图片
                }
            }

            @Override
            public void loadFaile(Call call, IOException e) {

            }
        });
    }

    public String subShowPath(int index){
        String path = picShowPath.get(index);
        return path.substring(7,path.length());

    }

    private void uploadFeedback(List<String> picsNo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", mBinding.inputEdittext.getText().toString());
//            jsonObject.put("opinionType", feedbackType.name());
            JSONArray pics = new JSONArray();
            if (null != picsNo) {
                for (int i = 0; i < picsNo.size(); i++) {
                    pics.put(picsNo.get(i));
                }
                jsonObject.put("pictures", pics);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestManager.getInstance().post(ModuleUrls.feedback, HttpApiKey.feedback, jsonObject.toString());
    }

    /**
     * 提交反馈意见api请求成功
     *
     * @param event
     *         成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadFeedbackSuccess(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.feedback.equals(apiNo)))
            return;
        LoadingDialogUtils.dissmiss();
        ToolsHelper.showInfo(mContext, getString(R.string.upload_feedbac_success));
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 提交反馈意见api请求失败
     *
     * @param event
     *         失败返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadFeedbackFailed(HttpRequestErrorEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!(HttpApiKey.feedback.equals(apiNo)))
            return;
        LoadingDialogUtils.dissmiss();
        finish();
        ToolsHelper.showHttpRequestErrorMsg(mContext, httpResult);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> result = (List)data.getSerializableExtra("extra_result_media");
                    localMediaList.clear();
                    localMediaList.addAll(result);
                    processSelectedPicture(PictureSelector.obtainMultipleResult(data));
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    break;
            }
        }
        if (requestCode == MainConstant.REQUEST_CODE_MAIN && resultCode == MainConstant.RESULT_CODE_VIEW_IMG) {
            //查看大图页面删除了图片
            ArrayList<String> toDeletePicList = data.getStringArrayListExtra(MainConstant.IMG_LIST); //要删除的图片的集合
            picShowPath.clear();
            picShowPath.addAll(toDeletePicList);
            refrushAdapter();
        }
    }
    // 处理选择的照片的地址
    private void processSelectedPicture( final List<LocalMedia> picList) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (LocalMedia localMedia : picList) {
                    //被压缩后的图片路径
                    if (localMedia.isCompressed()) {
                        String compressPath = localMedia.getCompressPath(); //压缩后的图片路径
                        picShowPath.add("file://" + compressPath); //把图片添加到将要上传的图片数组中
//                        picShowPath.add(compressPath); //把图片添加到将要上传的图片数组中

                    }
                }
                handler.sendEmptyMessage(0);

            }
        }).start();


    }
    private void refrushAdapter(){
        if(adapter == null){
            adapter = new FeedBackGridAdapter(this,picShowPath);
            mBinding.gridView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }

    }

}
