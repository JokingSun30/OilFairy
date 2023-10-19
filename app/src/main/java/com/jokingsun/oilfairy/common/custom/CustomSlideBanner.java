package com.jokingsun.oilfairy.common.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.databinding.LayoutCustomSlideBannerBinding;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderPager;

/**
 * 滑動/輪播 Banner
 *
 * @author cfd058
 */
public class CustomSlideBanner extends LinearLayout implements SliderPager.OnPageChangeListener
        , View.OnTouchListener {

    private LayoutCustomSlideBannerBinding binding;
    private ImageView[] dots;
    private boolean autoCycle, indicatorEnabled;
    private int cycleSecond = 0;
    private int indicatorSelectColor, indicatorUnSelectColor;
    private SlideBannerListener slideBannerListener;
    private boolean userCustomIndicator = false;
    private final Handler mHandler = new Handler();
    //private ArrayList<ResAnnouncementInfo.DataBean> bannerListBeans;

    private float downX, downY;

    public CustomSlideBanner(Context context) {
        super(context);
    }

    public CustomSlideBanner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        binding = DataBindingUtil.inflate(inflater, R.layout.layout_custom_slide_banner, this, false);

        this.addView(binding.getRoot(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        initAttrs(context, attrs);
    }

    @SuppressLint({"Recycle", "CustomViewStyleable"})
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideBanner, 0, 0);

        autoCycle = typedArray.getBoolean(R.styleable.SlideBanner_AutoCycle, true);
        indicatorEnabled = typedArray.getBoolean(R.styleable.SlideBanner_IndicatorEnabled, true);
        cycleSecond = typedArray.getInt(R.styleable.SlideBanner_CycleDuration, 0);
        indicatorSelectColor = typedArray.getColor(R.styleable.SlideBanner_IndicatorSelectColor,
                context.getColor(R.color.color_moon_grey));
        indicatorUnSelectColor = typedArray.getColor(R.styleable.SlideBanner_IndicatorUnSelectColor,
                context.getColor(R.color.color_origin_white));

        initSlideView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initSlideView() {
        binding.sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        binding.sliderView.setIndicatorEnabled(indicatorEnabled);
        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.sliderView.setAutoCycle(autoCycle);
        binding.sliderView.setScrollTimeInSec(cycleSecond);
        binding.sliderView.setIndicatorSelectedColor(indicatorSelectColor);
        binding.sliderView.setIndicatorUnselectedColor(indicatorUnSelectColor);
        binding.sliderView.setIndicatorRadius(3);
        binding.sliderView.getSliderPager().addOnPageChangeListener(this);
        binding.sliderView.getSliderPager().setOnTouchListener(this);
    }

//    public void loadData(ArrayList<ResAnnouncementInfo.DataBean> bannerBeans) {
//        this.bannerListBeans = bannerBeans;
//
//        if (bannerListBeans == null || bannerListBeans.size() == 0) {
//            return;
//        }
//
//        try {
//            ArrayList<String> photos = new ArrayList<>();
//
//            for (ResAnnouncementInfo.DataBean bannerBean : bannerListBeans) {
//                photos.add(bannerBean.getBanner_pic());
//            }
//
//            customIndicator(photos.size());
//
//            PhotoSlideAdapter slideAdapter = new PhotoSlideAdapter(photos, getContext());
//            slideAdapter.setRadius(1);
//
//            slideAdapter.setOnPhotoClickListener((photoPath, position) -> {
//                distinguishClickAction(position);
//            });
//
//            binding.sliderView.setSliderAdapter(slideAdapter);
//            binding.sliderView.startAutoCycle();
//
//        } catch (Exception e) {
//            Logger.d(e.getMessage());
//        }
//    }

    /**
     * 根據圖片集和來源，新增對應數量的 dot (僅有一張圖片，則不建構)
     *
     * @param photoCounts 圖片數量
     */
    private void customIndicator(int photoCounts) {
        if (indicatorEnabled || !userCustomIndicator) {
            return;
        }

        binding.sliderDots.setVisibility(VISIBLE);
        binding.sliderDots.removeAllViews();

        dots = new ImageView[photoCounts];

        for (int i = 0; i < photoCounts; i++) {
            dots[i] = new ImageView(getContext());
            dots[i].setImageResource(R.drawable.shape_non_active_dot);

            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);

            binding.sliderDots.addView(dots[i], params);
        }

        dots[0].setImageResource(R.drawable.shape_active_dot);

        binding.sliderView.setCurrentPageListener(position -> {
            for (ImageView dot : dots) {
                dot.setImageResource(R.drawable.shape_non_active_dot);
            }
            dots[position].setImageResource(R.drawable.shape_active_dot);
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean isAutoCycle = binding.sliderView.isAutoCycle();

        if (slideBannerListener != null) {
            slideBannerListener.bannerFocusState(true);
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (isAutoCycle) {
                binding.sliderView.stopAutoCycle();
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            if (isAutoCycle) {

                // resume after ~2 seconds debounce.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.sliderView.startAutoCycle();
                    }
                }, 1500);
            }

            if (slideBannerListener != null) {
                slideBannerListener.bannerFocusState(false);
            }

        }
        return false;

    }

//    /**
//     * 根據不同類型布告對應不同操作
//     */
//    private void distinguishClickAction(int position) {
//
//        if (slideBannerListener == null || bannerListBeans == null || bannerListBeans.size() < 0) {
//            return;
//        }
//
//        ResAnnouncementInfo.DataBean bannerDetail = bannerListBeans.get(position);
//        String linkType = bannerDetail.getLink_type();
//
//        try {
//            switch (linkType) {
//                case "2":
//                    if (bannerDetail.getExternal_url() != null && !bannerDetail.getExternal_url().isEmpty()) {
//                        slideBannerListener.linkOuterWebsite(Uri.parse(bannerDetail.getExternal_url()));
//                    }
//                    break;
//
//                case "1":
//                    ResAnnouncementInfo.DataBean promotionData = new ResAnnouncementInfo.DataBean();
//                    promotionData.setId(bannerDetail.getId());
//                    promotionData.setRoute_path(bannerDetail.getRoute_path());
//                    promotionData.setType(bannerDetail.getLink_type());
//
//                    slideBannerListener.linkInnerPromotion(new Gson().toJson(promotionData));
//                    break;
//
//                case "0":
//                default:
//                    break;
//            }
//
//        } catch (Exception e) {
//            Logger.d("活動異常！");
//        }
//    }

    public interface SlideBannerListener {

        void guideRouteSign(String routeSign);

        void linkOuterWebsite(Uri uri);

        void linkInnerPromotion(String jsonPromotionData);

        /**
         * 觀察目前 Banner 的焦點狀態
         */
        void bannerFocusState(boolean isFocus);
    }

    public void setSlideBannerListener(SlideBannerListener slideBannerListener) {
        this.slideBannerListener = slideBannerListener;
    }

    public void setUserCustomIndicator(boolean userCustomIndicator) {
        this.userCustomIndicator = userCustomIndicator;
    }
}
