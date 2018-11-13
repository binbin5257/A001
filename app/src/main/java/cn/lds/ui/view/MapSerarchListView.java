package cn.lds.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import cn.lds.widget.Pullable;

/**
 * Created by sibinbin on 18-4-17.
 */

public class MapSerarchListView extends ListView implements Pullable {

    public MapSerarchListView(Context context) {
        super(context);
    }

    public MapSerarchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canPullDown() {

        if (getCount() == 0) {
            return false;
        } else if (getFirstVisiblePosition() == 0 && getChildAt(0)!=null &&getChildAt(0).getTop() >= 0) {
            return false;
        } else
            return false;
    }

    @Override
    public boolean canPullUp() {

        if (getCount() == 0) {
            return true;
        } else if (getLastVisiblePosition() == (getCount() - 1)) {
            if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
                    && getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
                return true;
        }
        return false;
    }
}
