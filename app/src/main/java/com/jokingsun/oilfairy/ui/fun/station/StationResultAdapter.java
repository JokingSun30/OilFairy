package com.jokingsun.oilfairy.ui.fun.station;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseRecyclerAdapter;
import com.jokingsun.oilfairy.common.constant.AppConstant;
import com.jokingsun.oilfairy.databinding.ItemStationCardBinding;
import com.jokingsun.oilfairy.utils.GlideLoadUtil;

import java.util.ArrayList;

/**
 * 加油站搜尋結果 Adapter
 */
public class StationResultAdapter extends BaseRecyclerAdapter<String> {

    private final Context context;

    public StationResultAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{AppConstant.TYPE_NO_USE_HEADER, R.layout.item_station_card};
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if (holder.getBinding() instanceof ItemStationCardBinding) {
            ItemStationCardBinding binding = (ItemStationCardBinding) holder.getBinding();

            String gameInfo = dataList.get(position);

        }

    }
}
