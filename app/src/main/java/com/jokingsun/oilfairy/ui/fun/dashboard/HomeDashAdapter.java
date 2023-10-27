package com.jokingsun.oilfairy.ui.fun.dashboard;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseRecyclerAdapter;
import com.jokingsun.oilfairy.common.constant.AppConstant;
import com.jokingsun.oilfairy.common.constant.OilQualityEnum;
import com.jokingsun.oilfairy.data.remote.model.response.ResOilDetailInfo;
import com.jokingsun.oilfairy.databinding.LayoutLoadMoreProgressBinding;
import com.jokingsun.oilfairy.databinding.LayoutOilPriceDashboardBinding;

import java.util.ArrayList;

public class HomeDashAdapter extends BaseRecyclerAdapter<ResOilDetailInfo> {

    private final Context context;
    private float diffPrice = 0f;

    public HomeDashAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{AppConstant.TYPE_NO_USE_HEADER, R.layout.layout_oil_price_dashboard};
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (holder.getBinding() instanceof LayoutOilPriceDashboardBinding) {
            LayoutOilPriceDashboardBinding binding = (LayoutOilPriceDashboardBinding) holder.getBinding();

            ResOilDetailInfo detailInfo = dataList.get(position);

            //填充油品 Logo
            insertQualityLogo(detailInfo.getQuality(), binding.ivQualityLogo);

            //填充本週各廠牌價格
            insertDiffNowPrice(binding, detailInfo);

            //填充下週各廠牌價格
            insertDiffNextPrice(binding, detailInfo);
        }
    }

    public void insertQualityLogo(String quality, ImageView imageView) {
        if (TextUtils.equals(OilQualityEnum.QUALITY_98.getOilQualitySign(), quality)) {
            imageView.setImageResource(R.drawable.ic_98);
        }

        if (TextUtils.equals(OilQualityEnum.QUALITY_95.getOilQualitySign(), quality)) {
            imageView.setImageResource(R.drawable.ic_95);
        }

        if (TextUtils.equals(OilQualityEnum.QUALITY_92.getOilQualitySign(), quality)) {
            imageView.setImageResource(R.drawable.ic_92);
        }

        if (TextUtils.equals(OilQualityEnum.QUALITY_SUPER.getOilQualitySign(), quality)) {
            imageView.setImageResource(R.drawable.ic_super);
        }
    }


    private void insertDiffNowPrice(LayoutOilPriceDashboardBinding binding, ResOilDetailInfo detailInfo) {
        binding.tvCpcNowPrice.setText(detailInfo.getCpcNowPrice());
        binding.tvFpgNowPrice.setText(detailInfo.getFpgNowPrice());

        if (detailInfo.getCostcoNowPrice() == null || detailInfo.getCostcoNowPrice().isEmpty()) {
            detailInfo.setCostcoNowPrice("----");
        }

        binding.tvCostcoNowPrice.setText(detailInfo.getCostcoNowPrice());
    }

    private void insertDiffNextPrice(LayoutOilPriceDashboardBinding binding, ResOilDetailInfo detailInfo) {
        binding.tvCpcNextPrice.setText(detailInfo.getCpcNextPrice());
        binding.tvFpgNextPrice.setText(detailInfo.getFpgNextPrice());
        if (detailInfo.getCostcoNextPrice() == null || detailInfo.getCostcoNextPrice().isEmpty()) {
            detailInfo.setCostcoNextPrice("----");
        }

        binding.tvCostcoNextPrice.setText(detailInfo.getCostcoNextPrice());
    }

}
