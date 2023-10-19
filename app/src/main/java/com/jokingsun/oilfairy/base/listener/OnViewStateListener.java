package com.jokingsun.oilfairy.base.listener;

/**
 * @author Joshua
 * View 依據 View Model Response 的 成功 與 失敗做出回應。
 */
public interface OnViewStateListener {

    void onSuccess();

    void onFail();
}
