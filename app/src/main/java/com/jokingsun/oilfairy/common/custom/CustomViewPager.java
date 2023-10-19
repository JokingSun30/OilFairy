package com.jokingsun.oilfairy.common.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * @author cfd058
 */
public class CustomViewPager extends ViewPager {

    private boolean enabled = true;

    public CustomViewPager(@NonNull Context context) {
        super(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 用來解決 viewpager 與 fragment 佈局滑動衝突
     */
    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v != this) {
            if (v instanceof SwipeItemLayout) {
                if (dx < 0) {
                    return true;
                } else if (((SwipeItemLayout) v).isOpen()) {
                    return true;
                }
            }
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

    /**
     * IllegalArgumentException（pointerIndex超出範圍），同時使用許多手指放大和縮小 (ImageZoomView)
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            if (this.enabled) {
                return super.onTouchEvent(ev);
            }

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            if (this.enabled) {
                return super.onInterceptTouchEvent(ev);
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 是否開啟 viewpager 滾動
     */
    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
