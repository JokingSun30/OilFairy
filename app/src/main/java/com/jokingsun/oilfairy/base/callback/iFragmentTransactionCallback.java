package com.jokingsun.oilfairy.base.callback;

import androidx.fragment.app.Fragment;

public interface iFragmentTransactionCallback {

    void AddFragment(Fragment fragment);

    void AddFragment(Fragment fragment, int container);

    void AddFragment(Fragment fragment, String anim);

    void AddFragment(Fragment fragment, int container, String anim);

    void AddFragmentZoom(Fragment fragment, int container);

    void AddFragmentUp(Fragment fragment);

    void AddFragmentUp(Fragment fragment, int container);

    /**
     * 加入新的 fragment 並移除目前的 fragment ，但不將此次交易放入 stack ，一次性交易
     */
    void ReplaceFragment(Fragment fragment);

    void ReplaceFragment(Fragment fragment, String anim);

    void ReplaceFragment(Fragment fragment, int container);

    void ReplaceFragment(Fragment fragment, int container, String anim);

    /**
     * 一次性取出 stack 頂層最近一筆交易
     */
    void PopBackStack();

    /**
     * 清除在定 ID 以上所有在 stack 的交易
     */
    void PopBackStack(int targetId);

    /**
     * 清除所有在 stack 的交易
     */
    void PopAllBackStack();

    void OnAddFragment();

    void OnReplaceFragment();
}
