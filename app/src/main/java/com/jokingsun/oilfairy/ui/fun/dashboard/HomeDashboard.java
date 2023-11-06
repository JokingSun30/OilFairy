package com.jokingsun.oilfairy.ui.fun.dashboard;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jokingsun.oilfairy.BR;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseFragment;
import com.jokingsun.oilfairy.common.custom.OilCardInfoView;
import com.jokingsun.oilfairy.common.dialog.LoadingDialog;
import com.jokingsun.oilfairy.data.remote.model.response.ResOilDetailInfo;
import com.jokingsun.oilfairy.databinding.FragmentHomeDashboardBinding;
import com.jokingsun.oilfairy.ui.fun.console.ConsoleCenter;
import com.jokingsun.oilfairy.utils.MathUtil;
import com.jokingsun.oilfairy.widget.receiver.AppReceiver;

import java.util.ArrayList;
import java.util.Objects;

public class HomeDashboard extends BaseFragment<FragmentHomeDashboardBinding, HomeDashboardViewModel> {

    private boolean isFirstLoad = true;

    public static HomeDashboard getInstance() {
        return new HomeDashboard();
    }

    @Override
    protected void initView() {
        observeTopPriceDelta();
        observeOilDashboardData();
    }

    @Override
    protected void initial() {
        loadDashboardData();
    }

    @Override
    protected void initToolBar() {
        hideToolbar();
    }

    @Override
    protected void initSettingHaveVisible() {
    }

    @Override
    protected void onBackPressed() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstLoad) {
            loadDashboardData();
        }

        isFirstLoad = false;
    }

    @Override
    public int[] getBindingVariable() {
        return new int[]{BR.homeDashboardViewModel, BR.homeDashboard};
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home_dashboard;
    }

    @Override
    public HomeDashboardViewModel getViewModel() {
        if (viewModel == null) {
            viewModel = new ViewModelProvider(this, getFactory()).get(HomeDashboardViewModel.class);
        }
        return viewModel;
    }

    private void loadDashboardData() {
        getViewModel().getNextWeekPredict();
    }

    private void observeOilDashboardData() {
        getViewModel().getOilDashboardData().observe(this, resOilDetails -> {
            if (resOilDetails != null && resOilDetails.size() > 0) {
                binding.llOilCardContainer.removeAllViews();

                for (ResOilDetailInfo detailInfo : resOilDetails) {
                    OilCardInfoView cardInfoView = new OilCardInfoView(getContext());
                    cardInfoView.insertOilInfo(detailInfo);
                    binding.llOilCardContainer.addView(cardInfoView);
                }

                sendFirstLoadProgressStop();
                binding.llOilCardContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    private void observeTopPriceDelta() {
        getViewModel().getGasPriceDelta().observe(this, delta ->
                adjustTopDashboardUi(delta, binding.tvGasPriceDelta, binding.ivGasPriceDelta));
        getViewModel().getDieselPriceDelta().observe(this, delta ->
                adjustTopDashboardUi(delta, binding.tvDieselPriceDelta, binding.ivDieselPriceDelta));
    }

    private void adjustTopDashboardUi(float delta, AppCompatTextView texView, ImageView imageView) {
        boolean isUp = delta > 0;
        boolean isNoChange = delta == 0;

        texView.setText(isNoChange ? "0.0" : String.valueOf(
                MathUtil.roundToDecimalPlaces(Math.abs(delta), 1)));
        imageView.setVisibility(View.VISIBLE);

        if (isNoChange) {
            texView.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_ocean_blue));
            imageView.setImageResource(R.drawable.ic_no_change);

        } else {
            imageView.setImageResource(isUp ? R.drawable.ic_price_up : R.drawable.ic_price_down);
            texView.setTextColor(ContextCompat.getColor(requireContext(),
                    isUp ? R.color.color_price_up : R.color.color_price_down));
        }
    }

    public void jumpToConsoleCenter() {
        AddFragment(new ConsoleCenter());
    }

    private void sendFirstLoadProgressStop(){
        Intent intent = new Intent(AppReceiver.ACTION_TYPE_STOP_LOADING);
        requireActivity().sendBroadcast(intent);
    }
}
