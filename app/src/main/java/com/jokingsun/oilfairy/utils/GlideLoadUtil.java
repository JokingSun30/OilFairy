package com.jokingsun.oilfairy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.jokingsun.oilfairy.common.custom.BlurTransformation;

import java.io.File;

/**
 * @author cfd058
 */
public class GlideLoadUtil {

    public static RequestOptions GLIDE_REQUEST_OPTIONS = new RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565);

    public static RequestOptions GLIDE_REQUEST_OPTIONS_FOR_CIRCLE_ICON = new RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565)
            .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new CircleCrop()));

    public static RequestOptions GLIDE_REQUEST_OPTIONS_FOR_CENTER_CROP = new RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565).transform(new CenterCrop());

    public static RequestOptions GLIDE_REQUEST_OPTIONS_FOR_CIRCLE_CROP = new RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565).transform(new CircleCrop());

    public static RequestOptions GLIDE_REQUEST_OPTIONS_FOR_FIT_CENTER = new RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565).transform(new FitCenter());

    /**
     * 載入 Gif Drawable
     */
    public static void gifLoadByGlide(Context context, int resGif, ImageView imageView) {
        Glide.with(context)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .load(resGif)
                .into(imageView);
    }

    /**
     * 載入 Gif Url Drawable
     */
    public static void gifUrlLoadByGlide(Context context, String urlGif, ImageView imageView) {
        Glide.with(context)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .load(urlGif)
                .apply(GLIDE_REQUEST_OPTIONS_FOR_CENTER_CROP)
                .into(imageView);
    }


    /**
     * 載入 Gif Drawable
     */
    public static void gifLoopOnTimeLoadByGlide(Context context, int resGif, ImageView imageView) {
        Glide.with(context)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .load(resGif)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        resource.setLoopCount(1);
                        resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                super.onAnimationEnd(drawable);
                                imageView.setVisibility(View.GONE);
                            }
                        });
                        return false;
                    }
                })
                .into(imageView);
    }

    public static void backgroundImageLoadByGlide(Context context, int resId, View view, RequestOptions options) {
        Glide.with(context)
                .load(resId)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(options)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        view.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public static void resLoadByGlideCircleCrop(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(ContextCompat.getDrawable(context, resId))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(new RequestOptions().transform(new MultiTransformation<Bitmap>(new CenterCrop(), new CircleCrop())))
                .into(imageView);
    }

    /**
     * 導入 Glide lib 進行圖片壓縮 (centerCrop 是利用圖片圖填充ImageView設定的大小)
     */
    public static void imageLoadByGlideCenterCrop(Context context, String imagePath, ImageView imageView, int radius) {
        Glide.with(context)
                .load(new File(imagePath))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(radius))))
                .into(imageView);
    }

    public static void imageLoadByGlideCenterCropCache(Context context, String imagePath, ImageView imageView, int radius) {
        Glide.with(context)
                .load(new File(imagePath))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(radius))))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    /**
     * 導入 Glide lib 進行圖片壓縮 (fitCenter 即縮放影象讓影象都測量出來等於或小於 ImageView)
     */
    public static void imageLoadByGlideFitCenter(Context context, String imagePath, ImageView imageView) {
        Glide.with(context)
                .load(new File(imagePath))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fitCenter()
                .into(imageView);

    }

    /**
     * 導入 Glide lib 進行圖片壓縮 (CenterCrop  CircleCrop)
     */
    public static void imageLoadByGlideCircleCrop(Context context, String imagePath, ImageView imageView) {
        Glide.with(context)
                .load(new File(imagePath))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(new RequestOptions().transform(new MultiTransformation<Bitmap>(new CenterCrop(), new CircleCrop())))
                .into(imageView);
    }

    public static void imageResourceLoadByGlide(Context context, int resId, ImageView imageView, RequestOptions options) {
        Glide.with(context)
                .load(resId)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .apply(options)
                .into(imageView);
    }

    public static void imageLoadByGlide(Context context, int resId, ImageView imageView, RequestOptions options) {
        Glide.with(context)
                .load(resId)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(options)
                .into(imageView);
    }

    public static void imageLoadByGlideAndRadius(Context context, int resId, ImageView imageView, int radius) {
        Glide.with(context)
                .load(resId)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(radius))))
                .into(imageView);
    }

    public static void urlLoadByGlideCenterCrop(Context context, String urlString, ImageView imageView, int radius
            , int resLoadingHolder) {
        Glide.with(context)
                .load(urlString)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(resLoadingHolder)
                .apply(new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(radius))))
                .into(imageView);
    }

    public static void urlLoadByGlideCenterCrop(Context context, String urlString, ImageView imageView, int radius) {
        Glide.with(context)
                .load(urlString)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(radius))))
                .into(imageView);
    }

    public static void urlLoadByGlideFitCenter(Context context, String urlString, ImageView imageView, int radius
            , int resLoadingHolder) {
        Glide.with(context)
                .load(urlString)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(resLoadingHolder)
                .apply(new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .transform(new MultiTransformation<Bitmap>(new FitCenter(), new RoundedCorners(radius))))
                .into(imageView);
    }

    public static void urlLoadByGlideFitCenter(Context context, String urlString, ImageView imageView, int radius) {
        Glide.with(context)
                .load(urlString)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .transform(new MultiTransformation<Bitmap>(new FitCenter(), new RoundedCorners(radius))))
                .into(imageView);
    }

    public static void urlLoadByGlideCircleCrop(Context context, String urlString, ImageView imageView) {
        Glide.with(context)
                .load(urlString)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(new RequestOptions().transform(new MultiTransformation<Bitmap>(new CenterCrop(), new CircleCrop())))
                .into(imageView);
    }

    public static void urlLoadByGlideCenterCropDefault(Context context, String urlString, ImageView imageView) {
        Glide.with(context)
                .load(urlString)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop())))
                .into(imageView);
    }

    /**
     * 設定高斯模糊,模糊程度(最大25)  及 sampling 縮放比例
     */
    public static void urlLoadByGlideBlur(Context context, String urlString, ImageView imageView, int radius, int sampling) {
        Glide.with(context)
                .load(urlString)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(RequestOptions.bitmapTransform(
                        new MultiTransformation<Bitmap>(
                                new CenterCrop(),
                                new BlurTransformation(context, radius, sampling))))
                .into(imageView);
    }

    /**
     * 設定高斯模糊,模糊程度(最大25)  及 sampling 縮放比例
     */
    public static void urlLoadFitCenterBlurApply(Context context, String urlString, ImageView imageView, int radius, int sampling) {
        Glide.with(context)
                .load(urlString)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(RequestOptions.bitmapTransform(
                        new MultiTransformation<Bitmap>(
                                new FitCenter(),
                                new BlurTransformation(context, radius, sampling))))
                .into(imageView);
    }

//    private static ShimmerDrawable createShimmerDrawable(Context context) {
//        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
//                .setBaseColor(context.getColor(R.color.color_moon_grey))
//                .setHighlightColor(context.getColor(R.color.color_night_black_B3))
//                .setHighlightAlpha(0.6f)
//                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
//                .setDropoff(50)
//                .setAutoStart(true)
//                .build();
//
//        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
//        shimmerDrawable.setShimmer(shimmer);
//
//        return shimmerDrawable;
//    }

}
