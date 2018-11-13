package cn.lds.ui.view.banner;


/**
 * Created by leadingsoft on 17/5/26.
 */

public interface MZHolderCreator<VH extends MZViewHolder> {
    /**
     * 创建ViewHolder
     *
     * @return
     */
    public VH createViewHolder();
}