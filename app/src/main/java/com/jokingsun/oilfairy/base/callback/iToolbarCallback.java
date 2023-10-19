package com.jokingsun.oilfairy.base.callback;

import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;

public interface iToolbarCallback {

    Toolbar getToolbar();

    void setTitleImage(int resId);

    void setTitleByResource(int titleId, int textColor);

    void setTitle(CharSequence title, int textColor);

    void setTitleAndImage(int resId, int titleId, int textColor);

    void setToolBarBackGroundColor(int backBackGroundColor);

    void setLeftBackButton(int resId, View.OnClickListener onClickListener);

    void setRightBackButton(int resId, View.OnClickListener onClickListener);

    void clearLeftContent();

    void clearRightContent();

    void clearLeftAndRightContent();

    ViewGroup getLeftInnerContent();

    ViewGroup getRightInnerContent();

    void showToolbar();

    void hideToolbar();
}
