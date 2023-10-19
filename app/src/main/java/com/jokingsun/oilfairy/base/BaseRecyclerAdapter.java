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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.callback.iBaseRecyclerViewAdapter;
import com.jokingsun.oilfairy.common.constant.AppConstant;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cfd058
 */
public abstract class BaseRecyclerAdapter<B> extends RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder>
        implements iBaseRecyclerViewAdapter<B> {

    /**
     * 項目加載動畫
     */
    protected static final int SLIDE_IN_LEFT = android.R.anim.slide_in_left;
    protected static final int SLIDE_OUT_RIGHT = android.R.anim.slide_out_right;
    /**
     * TYPE ITEM 有數據資料 HEADER
     */
    private final int TYPE_VIEW_HEADER = 100;
    /**
     * TYPE ITEM 有數據資料 CONTENT
     */
    private final int TYPE_VIEW_CONTENT = 101;
    /**
     * TYPE ITEM 無數據資料 --> 加載資料
     */
    private final int TYPE_VIEW_PROGRESS = 102;
    private final int visibleThreshold = 10;
    protected ArrayList<B> dataList;
    boolean isLoadMoreLoading, haveNoMoreData, isLimitMode = false;
    private int lastVisibleItem, firstVisibleItem, totalItemCount, maxLimitCount;
    private LoadMoreModeListener loadMoreModeListener;

    public BaseRecyclerAdapter() {
        this.dataList = new ArrayList<>();
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

        switch (viewType) {
            //當啟用 Header 實作區塊，會自動判定第一個 layout 為 Header Layout
            case TYPE_VIEW_HEADER:
                resLayoutId = getLayoutIds()[AppConstant.TYPE_VIEW_HEADER];
                break;
            case TYPE_VIEW_CONTENT:
                resLayoutId = getLayoutIds()[AppConstant.TYPE_VIEW_CONTENT];
                break;
            case TYPE_VIEW_PROGRESS:
                resLayoutId = R.layout.layout_load_more_progress;
                break;
            default:
                break;
        }

        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), resLayoutId, parent, false);
        ViewHolder holder = new ViewHolder(binding.getRoot());
        holder.setBinding(binding);

        //setHolderListenerConfig(holder, holder.getBindingAdapterPosition());

        return holder;
    }

    protected void setHolderListenerConfig(ViewHolder viewHolder, int position) {
    }

    @Override
    public int getItemViewType(int position) {

        if (dataList.get(position) == null) {
            return TYPE_VIEW_PROGRESS;
        }

        //如果 getLayoutIds index 0 ，第一項不是 TYPE_NO_USE_HEADER ，表示啟動 Header - Content 模式
        if ((this.getLayoutIds()[0] != AppConstant.TYPE_NO_USE_HEADER) && position == 0) {
            return TYPE_VIEW_HEADER;
        }

        return TYPE_VIEW_CONTENT;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * CRUD Methods
     */
    @Override
    public void set(int position, B data) {
        this.dataList.set(position, data);
        notifyItemChanged(position);
    }

    @Override
    public void remove(int pos) {
        this.dataList.remove(pos);
        this.notifyItemRemoved(pos);
        this.notifyItemRangeChanged(0, dataList.size());
    }

    @Override
    public void setData(List<B> dataList) {
        if (dataList != null) {
            this.dataList.clear();
            this.dataList.addAll(dataList);
            this.notifyDataSetChanged();
        }
    }

    @Override
    public void add(int position, B data) {
        this.dataList.add(position, data);
        notifyItemInserted(position);
        this.notifyItemRangeChanged(0, dataList.size());
    }

    public void addDataMore(List<B> newDataList) {
        dataList.addAll(newDataList);
        notifyItemRangeInserted(dataList.size() - 1, newDataList.size());
    }

    public void addDataMoreInvert(List<B> newDataList) {
        dataList.addAll(0, newDataList);
        notifyItemRangeInserted(0, newDataList.size());
    }

    @Override
    public void clear() {
        this.dataList.clear();
        notifyDataSetChanged();
    }

    public void setDataByDiff(List<B> newDataList, DiffUtil.Callback callback) {
        //獲取 Diff Result
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);

        //按照 Diff Result 定義好的邏輯，更新數據
        dataList.clear();
        dataList.addAll(newDataList);

        //使用 DiffResult 分發給 adapter 熱更新
        diffResult.dispatchUpdatesTo(this);
    }

    public ArrayList<B> getDataList() {
        return dataList;
    }

    protected void setItemAnimation(Context context, View itemView, int resAnim) {
        Animation animation = AnimationUtils.loadAnimation(context, resAnim);
        itemView.setAnimation(animation);
    }

    /**
     * 註冊滾動底部加載
     */
    public void registerScrollListener(RecyclerView recyclerView, LoadMoreModeListener loadMoreModeListener) {
        this.loadMoreModeListener = loadMoreModeListener;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (linearLayoutManager != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

//                    Logger.i("!isLoadMoreLoading : " + !isLoadMoreLoading +
//                            "\ntotalItemCount : " + totalItemCount +
//                            "\nlastVisibleItem : " + lastVisibleItem +
//                            "\nvisibleThreshold : " + visibleThreshold
//                    );
                    //  item大於0，並且到最后一個item，並且還有更多數據
                    if (!isLoadMoreLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        isLoadMoreLoading = true;
                        runLoadMore();
                    }


                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoadMoreLoading && firstVisibleItem == 0) {
                        Logger.d("加載模式：已經接近底部準備加載");
                    }
                }
            });
        } else {
            Logger.e("LinearLayoutManager is Null，可能要在  RecyclerView setLayoutManager 之後 Adapter 才能 registerScrollListener");
        }

    }

    /**
     * 註冊滾動頂部加載
     */
    public void registerInvertScrollListener(RecyclerView recyclerView, LoadMoreModeListener loadMoreModeListener) {
        this.loadMoreModeListener = loadMoreModeListener;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (linearLayoutManager != null) {
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoadMoreLoading && (firstVisibleItem - 10) <= 0) {
                        isLoadMoreLoading = true;
                        runLoadMoreInvert();
                    }
                }

            }
        });
    }

    public boolean isHaveNoMoreData() {
        return haveNoMoreData;
    }

    /**
     * LoadMore Mode Setting And Register
     */
    public void setHaveNoMoreData(boolean haveNoMoreData) {
        this.haveNoMoreData = haveNoMoreData;
    }

    /**
     * 加載項目進程
     */
    public void runLoadMore() {
        try {
            if (dataList != null && isLoadMoreLoading) {
                Logger.d("準備加載! 當前前資料量為 :" + dataList.size());

                //限制最大加載筆數模式
                if (isLimitMode && this.getItemCount() >= maxLimitCount) {
                    haveNoMoreData = true;
                }

                if (!haveNoMoreData) {
                    // 新增 Loading Progress Layout
                    if (dataList.get(dataList.size() - 1) != null) {
                        this.add(dataList.size(), null);
                    }
                    loadMoreModeListener.loadMoreTask();
                } else {
                    loadMoreModeListener.failureTask();
                }

                Logger.d("加載後資料量 :" + dataList.size() + "   Have No More Data : " + haveNoMoreData);
            }

        } catch (Exception e) {
            Logger.d("底部加載項目異常" + e.getMessage());
        }
    }

    /**
     * 加載頂部項目進程
     */
    private void runLoadMoreInvert() {
        try {
            if (dataList != null && isLoadMoreLoading) {
                Logger.d("準備加載! 當前前資料量為 :" + dataList.size());

                //限制最大加載筆數模式
                if (isLimitMode && this.getItemCount() >= maxLimitCount) {
                    haveNoMoreData = true;
                }

                if (!haveNoMoreData) {
                    //this.add(0, null);
                    loadMoreModeListener.loadMoreTask();
                } else {
                    loadMoreModeListener.failureTask();
                }

                Logger.d("加載後資料量 :" + dataList.size());
            }

        } catch (Exception e) {
            Logger.d("頂部加載項目異常" + e.getMessage());
        }
    }

    /**
     * 是否限制加載數量
     */
    public void setLimitLoadMoreCount(int maxCount, boolean isLimitMode) {
        this.isLimitMode = isLimitMode;
        this.maxLimitCount = maxCount;
    }

    public void setLoadMoreLoading(boolean isLoadMoreLoading) {
        this.isLoadMoreLoading = isLoadMoreLoading;
    }

    public boolean isLoadMoreLoading() {
        return isLoadMoreLoading;
    }

    public void setLoadMoreModeListener(LoadMoreModeListener loadMoreModeListener) {
        this.loadMoreModeListener = loadMoreModeListener;
    }

    public interface LoadMoreModeListener {

        /**
         * 加載 Task
         */
        void loadMoreTask();

        /**
         * 不允許加載 Task
         */
        void failureTask();

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
