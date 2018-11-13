package cn.lds.common.file;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by sibinbin on 17-12-19.
 */

public class ProgressFileRequestBody extends RequestBody {

    public static final int UPDATE = 0x01;
    private RequestBody requestBody;
    private ProgressListener mListener;
    private BufferedSink bufferedSink;
    private MyHandler myHandler;
    public ProgressFileRequestBody(RequestBody body, ProgressListener listener) {
        requestBody = body;
        mListener = listener;
        if (myHandler==null){
            myHandler = new MyHandler();
        }
    }

    class MyHandler extends Handler {
        //放在主线程中显示
        public MyHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE:
                    ProgressModel progressModel = (ProgressModel) msg.obj;
                    if (mListener!=null)
                        mListener.onProgress(progressModel.getTotalSize(),progressModel.getCurrentSize());
                    break;

            }
        }


    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {

        if (bufferedSink==null){
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        requestBody.writeTo(bufferedSink);
        //刷新
        bufferedSink.flush();
    }

    private Sink sink(BufferedSink sink) {

        return new ForwardingSink(sink) {
            long bytesWritten = 0L;
            long contentLength = 0L;
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength==0){
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                //回调
                Message msg = Message.obtain();
                msg.what = UPDATE;
                msg.obj =  new ProgressModel(contentLength,bytesWritten,bytesWritten==contentLength);
                myHandler.sendMessage(msg);
            }
        };
    }


    public class ProgressModel{
        public long totalSize;
        public long currentSize;
        public boolean done;

        public ProgressModel(long totalSize, long currentSize, boolean done) {
            this.totalSize = totalSize;
            this.currentSize = currentSize;
            this.done = done;
        }

        public long getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(long totalSize) {
            this.totalSize = totalSize;
        }

        public long getCurrentSize() {
            return currentSize;
        }

        public void setCurrentSize(long currentSize) {
            this.currentSize = currentSize;
        }

        public boolean isDone() {
            return done;
        }

        public void setDone(boolean done) {
            this.done = done;
        }
    }

    public interface ProgressListener{
        void onProgress(long totalSize, long currSize);
    }

}
