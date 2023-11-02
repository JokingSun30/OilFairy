package com.jokingsun.oilfairy.common.custom;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.common.constant.OilQualityEnum;
import com.jokingsun.oilfairy.data.remote.model.response.ResOilDetailInfo;
import com.jokingsun.oilfairy.databinding.LayoutOilCardViewBinding;

public class OilCardInfoView extends LinearLayout {

    private final LayoutOilCardViewBinding binding;

    public OilCardInfoView(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        binding = DataBindingUtil.inflate(inflater, R.layout.layout_oil_card_view, this, false);

        this.addView(binding.getRoot(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }


    public void insertOilInfo(ResOilDetailInfo oilDetailInfo){

        //填充油品 Logo
        insertQualityLogo(oilDetailInfo.getQuality(), binding.ivQualityLogo);

        //填充本週各廠牌價格
        insertDiffNowPrice(binding, oilDetailInfo);

        //填充下週各廠牌價格
        insertDiffNextPrice(binding, oilDetailInfo);
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


    private void insertDiffNowPrice(LayoutOilCardViewBinding binding, ResOilDetailInfo detailInfo) {
        binding.tvCpcNowPrice.setText(detailInfo.getCpcNowPrice());
        binding.tvFpgNowPrice.setText(detailInfo.getFpgNowPrice());

        if (detailInfo.getCostcoNowPrice() == null || detailInfo.getCostcoNowPrice().isEmpty()) {
            detailInfo.setCostcoNowPrice("----");
        }

        binding.tvCostcoNowPrice.setText(detailInfo.getCostcoNowPrice());
    }

    private void insertDiffNextPrice(LayoutOilCardViewBinding binding, ResOilDetailInfo detailInfo) {
        binding.tvCpcNextPrice.setText(detailInfo.getCpcNextPrice());
        binding.tvFpgNextPrice.setText(detailInfo.getFpgNextPrice());
        if (detailInfo.getCostcoNextPrice() == null || detailInfo.getCostcoNextPrice().isEmpty()) {
            detailInfo.setCostcoNextPrice("----");
        }

        binding.tvCostcoNextPrice.setText(detailInfo.getCostcoNextPrice());
    }

}