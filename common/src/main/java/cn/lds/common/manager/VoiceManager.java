package cn.lds.common.manager;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;

import cn.lds.common.constants.Constants;
import cn.lds.common.utils.LogHelper;

/**
 * 录音管理中心
 *
 * @author leadingsoft
 */
public class VoiceManager {
    private static VoiceManager mInstance;//单利引用
    /* 文件存储路径 */
    private final String mSavePath = Constants.SYS_CONFIG_FILE_PATH + "welcome.amr";

    public static VoiceManager getInstance() {
        VoiceManager inst = mInstance;
        if (inst == null) {
            synchronized (VoiceManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new VoiceManager();
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    public VoiceManager() {
    }


    private MediaRecorder mMediaRecorder;
    public boolean isRecording = false;

    private RecordAmplitude recordAmplitude;


    /**
     * 开始录音
     */
    public String startRec() {
        try {
            if (isRecording) {
                stopRec();
            }

            if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                File myRecAudioFile = new File(mSavePath);
                if (!myRecAudioFile.getParentFile().exists()) {
                    myRecAudioFile.getParentFile().mkdirs();
                }

                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                // 设置录制的音频通道数。
                mMediaRecorder.setAudioChannels(1);
                // 设置录制的音频编码比特率
                mMediaRecorder.setAudioEncodingBitRate(16);
                // //设置录制的音频采样率。
//                mMediaRecorder.setAudioSamplingRate(16000);
                mMediaRecorder.setOutputFile(myRecAudioFile.getAbsolutePath());

                mMediaRecorder.prepare();
                mMediaRecorder.start();

                isRecording = true;

                recordAmplitude = new RecordAmplitude();
                recordAmplitude.execute();
                return mSavePath;

            } else {
                return null;
            }
        } catch (Exception e) {
            LogHelper.wtf(e);
        }

        return null;
    }

    /**
     * 结束录音
     */
    public void stopRec() {
        if (!isRecording) {
            return;
        }
        try {
            isRecording = false;

            if (recordAmplitude != null)
                recordAmplitude.cancel(true);

            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getAudioFilePath() {
        return mSavePath;
    }

    /**
     * 音量监视器
     */
    private class RecordAmplitude extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (isRecording) {
                try {
                    publishProgress(mMediaRecorder.getMaxAmplitude());
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    protected void onDestroy() {
        if (null != mInstance) {
            mInstance = null;
        }
    }

}
