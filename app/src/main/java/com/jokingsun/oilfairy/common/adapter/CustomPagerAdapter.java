package com.jokingsun.oilfairy.common.adapter;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cfd058
 */
public class CustomPagerAdapter extends FragmentPagerAdapter {
    private final ArrayList<Fragment> fragments;
    private final FragmentManager fm;
    /**
     * 保存每個 Fragment 的 Tag，刷新頁面的依据
     */
    private final SparseArray<String> tags = new SparseArray<>();
    private ArrayList<String> titles;
    private ArrayList<Integer> integers;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
        titles = new ArrayList<>();
        fragments = new ArrayList<>();
        this.fm = fm;
    }

    public void initCustomTab(ViewPager viewPager, TabLayout tabLayout) {
        this.tabLayout = tabLayout;
        this.viewPager = viewPager;

        viewPager.setAdapter(this);
        tabLayout.setupWithViewPager(viewPager);

        if (tabLayout.getTabCount() > 0) {
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    tab.setCustomView(getTabView(i));
                }
            }
        }
    }

    /**
     * custom tabLayout.tab
     *
     * @param position tab index
     * @return
     */
    public View getTabView(int position) {
        return null;
    }

    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
        titles.add("");
    }

    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        titles.add(title);
    }

    public void addFragment(Fragment fragment, int resourceId) {
        fragments.add(fragment);
        integers.add(resourceId);
    }

    public void addFragment(Fragment fragment, String title, int resourceId) {
        fragments.add(fragment);
        titles.add(title);
        integers.add(resourceId);
    }

    public ArrayList<String> getTitles() {
        return titles;
    }

    public void setTitles(ArrayList<String> titles) {
        this.titles = titles;
    }

    public ArrayList<Integer> getIntegers() {
        return integers;
    }

    public void setIntegers(ArrayList<Integer> integers) {
        this.integers = integers;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    /**
     * 拿到指定位置的 Fragment
     */
    public Fragment getFragmentByPosition(int position) {
        return fm.findFragmentByTag(tags.get(position));
    }

    public List<Fragment> getFragments() {
        return fm.getFragments();
    }

    /**
     * 刷新指定位置的 Fragment
     */
    public void notifyFragmentByPosition(int position) {
        tags.removeAt(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        Fragment fragment = (Fragment) object;
        //如果 Item 對應的 Tag 存在，则不進行刷新
        if (tags.indexOfValue(fragment.getTag()) > -1) {
            return super.getItemPosition(object);
        }
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //得到缓存的 Fragment
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        String tag = fragment.getTag();
        //保存每個 Fragment 的 Tag
        tags.put(position, tag);
        return fragment;
    }
}
