package cn.lds.model;

import android.view.View;

/**
 * Created by sibinbin on 18-2-9.
 */

public class FixedViewInfo {
    /** The view to add to the list */
    public View view;
    /** The data backing the view. This is returned from {RecyclerView.Adapter#getItemViewType(int)}. */
    public int viewType;
}
