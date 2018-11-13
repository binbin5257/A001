package cn.lds.common.file;

import android.os.AsyncTask;

import java.net.URL;

/**
 * AsyncTask
 */
public class DownloadFilesTask extends AsyncTask<URL,Integer,Long> {

    /**
     * 在主线程中执行，在异步任务之前，执行方法会被调用，一般用于一些准备工作
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * 此方法用于执行一些异步任务
     * 方法中可以通过publishProgress方法来更新任务进度，publishProgress方法会调用onProgressUpdate方法。
     * @param urls 异步任务输入的参数
     * @return 此方法返回的计算结果要给onPostExcute方法
     */
    @Override
    protected Long doInBackground(URL... urls) {
        return null;
    }

    /**
     * 在主线程中执行，当后台任务执行进度执行发生改变时会被调用
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    /**
     * 在主线程中执行，在异步任务执行之后，此方法会被调用
     * @param aLong
     */
    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
    }
}
