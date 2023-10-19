package com.jokingsun.oilfairy.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleObserver;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jokingsun.oilfairy.common.constant.AppConstant;

/**
 * @author cfd058
 */
public abstract class BaseRecyclerPageListAdapter<B> extends PagedListAdapter<B, BaseRecyclerPageListAdapter.ViewHolder>
        implements LifecycleObserver {

    public BaseRecyclerPageListAdapter(@NonNull DiffUtil.ItemCallback<B> diffCallback) {
        super(diffCallback);
    }

    /**
     * 取的所需的 item layout
     *
     * @return layout
     */
    protected abstract int[] getLayoutIds();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //預設
        int resLayoutId = getLayoutIds()[AppConstant.TYPE_VIEW_CONTENT];

        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), resLayoutId, parent, false);
        ViewHolder holder = new ViewHolder(binding.getRoot());
        holder.setBinding(binding);

        return holder;
    }

    protected void setItemAnimation(Context context, View itemView, int resAnim) {
        Animation animation = AnimationUtils.loadAnimation(context, resAnim);
        itemView.setAnimation(animation);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }
    }

}
