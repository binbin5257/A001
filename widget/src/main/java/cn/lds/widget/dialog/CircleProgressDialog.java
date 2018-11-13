package cn.lds.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.lds.widget.ColorfulRingProgressView;
import cn.lds.widget.R;
import cn.lds.widget.dialog.base.CenterNormalDialog;

/**
 * 版本更新对话框
 * Created by leading123 on 17-12-8.
 */

public class CircleProgressDialog extends CenterNormalDialog<CircleProgressDialog> {

    private ColorfulRingProgressView progressBar;
    private TextView percentTv;

    public CircleProgressDialog(Context context) {
        super(context);
        init();
    }


    @Override
    public int getLayoutRes() {
        return R.layout.layout_circle_progress;
    }

    @Override
    public void onCreateData() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);

    }

    @Override
    public void onClick(View v, int id) {
    }

    @Override
    public void show() {
        super.show();
    }

    /**
     * 初始化
     */
    private void init() {
        progressBar = findViewById(R.id.progressBar1);
        percentTv = findViewById(R.id.tvPercent);
    }

    public void setProgress(int progress) {
        progressBar.setPercent(progress);
        percentTv.setText(""+progress);


    }
}
