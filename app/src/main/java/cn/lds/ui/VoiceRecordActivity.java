package cn.lds.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.file.FileUploadComplete;
import cn.lds.common.file.FileUploadFailed;
import cn.lds.common.manager.FilesManager;
import cn.lds.common.manager.VoiceManager;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.databinding.ActivityVoiceRecordBinding;

/**
 * 语音录制界面
 */
public class VoiceRecordActivity extends BaseActivity implements View.OnClickListener {


    ActivityVoiceRecordBinding mBinding;
    private int totalTime = 30;//30s
    private final int STARTRECORD = 999;
    private final int ENDRECORD = 998;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_voice_record);
        initView();
        initListener();
    }

    public void initView() {
        TextView topTitle = mBinding.getRoot().findViewById(R.id.top_title_tv);
        topTitle.setText("开机提示语");
    }

    /**
     * 注册事件监听
     */
    @Override
    public void initListener() {
        mBinding.getRoot().findViewById(R.id.top_back_iv).setOnClickListener(this);
        mBinding.cancel.setOnClickListener(this);
        mBinding.confirm.setOnClickListener(this);
        mBinding.start.setOnClickListener(this);
    }

    /**
     * @param view
     *         点击的view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.top_back_iv:
                finish();
                break;
            case R.id.confirm:
                uploadAudio();
                break;
            case R.id.start:
                VoiceManager.getInstance().startRec();
                handler.sendEmptyMessage(STARTRECORD);
                break;
            case R.id.cancel:
                finish();
                break;
        }

    }

    /**
     * 上传语音
     */
    private void uploadAudio() {
        String url = VoiceManager.getInstance().getAudioFilePath();
        FilesManager.getInstance().upload(url);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STARTRECORD:
                    mBinding.lessTime.setText("00:" + getMin());
                    if (totalTime <= 0) {
                        handler.sendEmptyMessage(ENDRECORD);
                    } else {
                        handler.sendEmptyMessageDelayed(STARTRECORD, 1000);
                        totalTime -= 1;
                    }
                    break;
                case ENDRECORD:
                    VoiceManager.getInstance().stopRec();
                    break;
            }
        }
    };

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

    /**
     * @param fileUploadComplete
     *         文件上传成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadSucces(FileUploadComplete fileUploadComplete) {
        ToolsHelper.showInfo(mContext, "提示音上传成功");
        finish();
    }

    /**
     * @param fileUploadFailed
     *         文件上传失败
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadFailed(FileUploadFailed fileUploadFailed) {
        ToolsHelper.showInfo(mContext, "提示音上传失败");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(STARTRECORD);
    }


    private String getMin() {
        int min = totalTime;
        if (min >= 10) {
            return min + "";
        } else {
            return "0" + min;
        }
    }

}
