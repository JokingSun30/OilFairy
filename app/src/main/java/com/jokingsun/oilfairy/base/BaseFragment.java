package com.jokingsun.oilfairy.base;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.callback.iFragmentTransactionCallback;
import com.jokingsun.oilfairy.base.callback.iGeneralSettingCallback;
import com.jokingsun.oilfairy.base.callback.iToolbarCallback;
import com.jokingsun.oilfairy.common.constant.AppConstant;
import com.jokingsun.oilfairy.databinding.FragmentBaseArchitectureBinding;
import com.jokingsun.oilfairy.ui.controller.ViewModelProviderFactory;
import com.jokingsun.oilfairy.utils.GeneralUtil;
import com.jokingsun.oilfairy.widget.helper.NetWorkStatusHelper;
import com.jokingsun.oilfairy.widget.manager.logevent.LogEventManager;
import com.orhanobut.logger.Logger;
import com.zyq.easypermission.EasyPermissionHelper;

/**
 * @author cfd058
 */
public abstract class BaseFragment<T extends ViewDataBinding, VM extends BaseViewModel>
        extends Fragment implements iToolbarCallback {

    protected T binding;
    protected VM viewModel;

    private iGeneralSettingCallback generalSetting;
    private iFragmentTransactionCallback fragmentTransaction;

    private BaseActivity mActivity;
    private ViewModelProviderFactory factory;
    private View view;
    private FragmentBaseArchitectureBinding architectureBinding;

    private NetWorkStatusHelper netWorkStatusHelper;
    protected BroadcastReceiver isNetWorkAliveBroadCast;
    protected Gson gson;
    protected LogEventManager logEventManager;

    protected MutableLiveData<Boolean> isMenuVisible = new MutableLiveData<>();

    private boolean isFirstLoad = true;
    private Handler lazyLoadHandler;
    private Runnable lazyLoadTask;
    private int lazyLoadTime = 300;
    private boolean lockSystemBack = false;
    protected boolean everThroughPause = false;
    private SystemBackListener systemBackListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {

            if (context instanceof BaseActivity) {
                mActivity = (BaseActivity) context;
            }

            if (context instanceof iGeneralSettingCallback) {
                this.generalSetting = (iGeneralSettingCallback) context;
            }

            if (context instanceof iFragmentTransactionCallback) {
                this.fragmentTransaction = (iFragmentTransactionCallback) context;
            }

            this.factory = ViewModelProviderFactory.getInstance(context.getApplicationContext());

        } catch (ClassCastException e) {
            printLog(context.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.lazyLoadHandler = new Handler();
        this.logEventManager = LogEventManager.getInstance(this.getContext());
    }

    /**
     * 因 Fragment 在 ViewPager 有不同的 lifeCycle ， 當在 viewpager 底下的 Fragment 壓入 Stack ，再次 Pop 出來後，不會經過 onCreate ，
     * 會直接進入 OnCreateView --> onDestroyView ，因此為確保繼承本 BaseFragment 的子 Fragment --> Pop 出來後仍有原初始設定，故在第一次及重新加載畫面時，
     * 緩存原 view 設定，待二次加載後回傳緩存的 view，已確保初始設置定無誤，建議後續繼承的子 Fragment 不要 Override OnCreateView ，
     * 可將方法寫入 Base 提供的  initView 、 initial，另每次進入 onCreateView 都重新向 factory 獲取 ViewModel ，以確保 ViewModel is alive
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            viewModel = getViewModel();
            this.gson = new Gson();

            architectureBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_architecture, container, false);

            binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
            analysisVariable(binding);
            binding.setLifecycleOwner(this);
            binding.executePendingBindings();

            if (architectureBinding != null) {
                architectureBinding.llContent.addView(binding.getRoot());
            }

            //弱引用 Fragment 的 fragment 切頁方法
            if (viewModel != null) {
                viewModel.setNavigator(this);
            }

            this.initNetWorkManager();
            this.initView();
            this.initial();
            this.initToolBar();

            this.view = architectureBinding.getRoot();

        } else {
            ViewParent vp = view.getParent();
            if (vp instanceof ViewGroup) {
                ((ViewGroup) vp).removeView(view);
            }
        }

        return this.view;
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        this.initNetWorkManager();
//        this.initView();
//        this.initial();
//    }

    /**
     * 透過 binding 初始 view 狀態
     */
    protected abstract void initView();

    /**
     * 初始設置自定義變數
     */
    protected abstract void initial();

    /**
     * 確保每個 fragment 進入後，都必須重製 toolbar
     */
    protected abstract void initToolBar();

    /**
     * 初始化網路狀態管理者
     */
    private void initNetWorkManager() {

        if (netWorkStatusHelper == null) {
            netWorkStatusHelper = new NetWorkStatusHelper(mActivity, () -> {
                //網路重新恢復工作
                viewModel.setWhenNetWorkRework();
            });
        }

        //將觀察者(MyLocationListener) 與 被觀察者 (Fragment) 綁定
        getLifecycle().addObserver(netWorkStatusHelper);

    }

    /**
     * 當 fragment 確定已被加入 activity 且以獲取當前焦點時，設定所有操作，建議打 api 獲取資料位置
     */
    protected abstract void initSettingHaveVisible();

    /**
     * 攔截系統返回鍵 --> 執行動作區間
     */
    protected abstract void onBackPressed();

    /**
     * 註冊相關廣播事件，需以成對的 LifeCycle 做 Register 與 unRegister ， 如 : onCreate --> onDestroy  onCreateView --> onDestroyView
     * onResume --> onPause ， 然為求上述 ViewPager 在 Fragment 的 lifeCycle 不同，故建議若要註冊廣播事件，統一在 onResume 和 onPause 操作
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();
        catchSystemBackEvent();

        //確定 Fragment 已加入 Activity 且已經獲取焦點(User 已確認看到顯示畫面) 但是若在 viewpager 會默認已顯示
        if (this.isVisible()) {
            initSettingHaveVisible();
        }
        this.lazyLoadPageData();
    }

    @Override
    public void onPause() {
        super.onPause();
        everThroughPause = true;
    }

    /**
     * 頁面延遲加載數據
     */
    private void lazyLoadPageData() {
        if (this.isVisible() && isFirstLoad) {

            //isMenuVisible == null 表示 Fragment 並非在 ViewPager 中 ; 反之則在 Viewpager 中
            // 若在ViewPager中，且又為 false 表示尚未顯示在 ViewPager

            if (isMenuVisible.getValue() == null || isMenuVisible.getValue()) {

                lazyLoadTask = this::loadPageData;
                lazyLoadHandler.postDelayed(lazyLoadTask, lazyLoadTime);
                isFirstLoad = false;
            }
        }
    }

    protected void loadPageData() {
        Logger.d("優化中：" + "執行延遲加載");
    }

    /**
     * 客製延遲加載時間
     *
     * @param lazyLoadTime 延遲時間
     */
    protected void setLazyLoadTime(int lazyLoadTime) {
        this.lazyLoadTime = lazyLoadTime;
    }

    @Override
    public void onDestroyView() {
        onBackPressed();
        hideKeyboard();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lazyLoadHandler != null && lazyLoadTask != null) {
            lazyLoadHandler.removeCallbacks(lazyLoadTask);
        }
    }

    /**
     * 使用情境通常在 ViewPager 中的 Fragment ，用於判斷 Fragment 的顯示與隱藏
     */
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        isMenuVisible.setValue(menuVisible);
        lazyLoadPageData();
    }

    public MutableLiveData<Boolean> getIsMenuVisible() {
        return isMenuVisible;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissionHelper.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults,
                getBaseActivity());
    }

    /**----------------------------- DataBinding and ViewModel Attach------------------------------*/

    /**
     * Override for set binding variable
     * <p>
     * int [0] : BR.ViewModel
     * int [1] : BR.TargetFragment
     *
     * @return variable id
     */
    public abstract int[] getBindingVariable();

    /**
     * @return layout resource id
     */
    @LayoutRes
    public abstract int getLayoutId();

    public T getViewDataBinding() {
        return binding;
    }

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract VM getViewModel();

    public ViewModelProviderFactory getFactory() {
        return factory;
    }

    /**
     * 解析開發者需求綁定參數 [0]:ViewModel / [1]:View
     */
    private void analysisVariable(T binding) {
        int[] variable = getBindingVariable();

        if (variable.length > 0) {
            binding.setVariable(getBindingVariable()[0], viewModel);
        }

        if (variable.length > 1) {
            binding.setVariable(getBindingVariable()[1], this);
        }

    }

    /**
     * --------------------- Loading , Toast , Keyboard , Status Bar Setting ----------------------
     */
    public void showLoading() {
        if (generalSetting != null) {
            generalSetting.showLoading();
        }
    }

    public void cancelLoading() {
        if (generalSetting != null) {
            generalSetting.cancelLoading();
        }
    }

    public void scheduleLoading() {
        showLoading();
        new Handler().postDelayed(this::cancelLoading, 800);
    }

    public void showToast(CharSequence content) {
        this.showToast(content, true);
    }

    public void showToast(CharSequence content, boolean isLong) {
        generalSetting.showToast(content, isLong);
    }

    public void hideKeyboard() {
        generalSetting.hideKeyboard();
    }

    public void hideKeyboard(IBinder token) {
        generalSetting.hideKeyboard(token);
    }

    public void updateStatusBarColor(int colorResId, boolean useDarkText) {
        generalSetting.updateStatusBarColor(mActivity, colorResId, useDarkText);
    }

    /**
     * 打印 Log 訊息
     */
    protected void printLog(String message) {
        GeneralUtil.printLog(this.getClass().getSimpleName(), message);
    }

    /**
     * --------------------------------- About Fragment Logic Setting -----------------------------
     */

    public void AddFragment(Fragment fragment) {
        fragmentTransaction.AddFragment(fragment);
    }

    public void AddFragment(Fragment fragment, String anim) {
        fragmentTransaction.AddFragment(fragment, anim);
    }

    public void AddFragment(Fragment fragment, int container) {
        fragmentTransaction.AddFragment(fragment, container);
    }

    public void AddFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        fragmentTransaction.AddFragment(fragment);
    }

    public void AddFragmentFade(Fragment fragment, int container) {
        fragmentTransaction.AddFragmentZoom(fragment, container);
    }

    public void AddFragmentUp(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        this.AddFragmentUp(fragment);
    }

    public void AddFragmentUp(Fragment fragment) {
        fragmentTransaction.AddFragmentUp(fragment);
    }

    public void AddFragmentUp(Fragment fragment, int container) {
        fragmentTransaction.AddFragmentUp(fragment, container);
    }

    public void ReplaceFragment(Fragment fragment) {
        fragmentTransaction.ReplaceFragment(fragment);
    }

    public void ReplaceFragment(Fragment fragment, int container) {
        fragmentTransaction.ReplaceFragment(fragment, container);
    }

    public void ReplaceFragment(Fragment fragment, String anim) {
        fragmentTransaction.ReplaceFragment(fragment, anim);
    }

    public void ReplaceFragment(Fragment fragment, int container, String anim) {
        fragmentTransaction.ReplaceFragment(fragment, container, anim);
    }

    public void PopBackStack() {
        fragmentTransaction.PopBackStack();
    }

    public void PopBackStack(int targetId) {
        fragmentTransaction.PopBackStack(targetId);
    }

    public void PopAllBackStack() {
        fragmentTransaction.PopAllBackStack();
    }

    public FragmentManager getParentFrag(Fragment frag) {
        Fragment f = frag.getParentFragment();
        return f != null ? this.getParentFrag(f) : frag.getChildFragmentManager();
    }

    /**
     * ---------------------------------- About Toolbar Setting --------------------------------
     **/
    @Override
    public Toolbar getToolbar() {
        return architectureBinding.ilToolbar.toolbar;
    }

    /**
     * 標題顯示(Image)
     */
    @Override
    public void setTitleImage(int resId) {
        architectureBinding.ilToolbar.tvTitle.setVisibility(View.GONE);
        architectureBinding.ilToolbar.ivTitle.setVisibility(View.VISIBLE);
        architectureBinding.ilToolbar.ivTitle.setImageResource(resId);
    }

    /**
     * 標題顯示(Text)
     */
    @Override
    public void setTitleByResource(int titleId, int textColor) {
        architectureBinding.ilToolbar.tvTitle.setVisibility(View.VISIBLE);
        architectureBinding.ilToolbar.ivTitle.setVisibility(View.GONE);
        architectureBinding.ilToolbar.tvTitle.setText(titleId);
        architectureBinding.ilToolbar.tvTitle.setTextColor(textColor);
    }

    /**
     * 標題顯示(Text)
     */
    @Override
    public void setTitle(CharSequence title, int textColor) {
        architectureBinding.ilToolbar.tvTitle.setVisibility(View.VISIBLE);
        architectureBinding.ilToolbar.ivTitle.setVisibility(View.GONE);
        architectureBinding.ilToolbar.tvTitle.setText(title);
        architectureBinding.ilToolbar.tvTitle.setTextColor(textColor);
    }

    /**
     * 標題顯示(Image , Text)
     */
    @Override
    public void setTitleAndImage(int resId, int titleId, int textColor) {
        architectureBinding.ilToolbar.tvTitle.setVisibility(View.VISIBLE);
        architectureBinding.ilToolbar.tvTitle.setText(titleId);
        architectureBinding.ilToolbar.tvTitle.setTextColor(textColor);
        architectureBinding.ilToolbar.ivTitle.setVisibility(View.VISIBLE);
        architectureBinding.ilToolbar.ivTitle.setImageResource(resId);
    }

    /**
     * 設置背景色樣
     */
    @Override
    public void setToolBarBackGroundColor(int backBackGroundColor) {
        architectureBinding.ilToolbar.toolbar.setBackgroundColor(backBackGroundColor);
    }

    /**
     * 設置左邊按鈕(預設系統返回)
     */
    @Override
    public void setLeftBackButton(int resId, View.OnClickListener onClickListener) {
        architectureBinding.ilToolbar.leftIcon.setVisibility(View.VISIBLE);
        architectureBinding.ilToolbar.leftIcon.setImageResource(resId);
        architectureBinding.ilToolbar.leftIcon.setOnClickListener(v -> {
            onClickListener.onClick(architectureBinding.ilToolbar.leftIcon);
        });
    }

    /**
     * 設置右邊按鈕(預設系統返回)
     */
    @Override
    public void setRightBackButton(int resId, View.OnClickListener onClickListener) {
        architectureBinding.ilToolbar.rightIcon.setVisibility(View.VISIBLE);
        architectureBinding.ilToolbar.rightIcon.setImageResource(resId);
        architectureBinding.ilToolbar.rightIcon.setOnClickListener(v -> {
            onClickListener.onClick(architectureBinding.ilToolbar.rightIcon);
        });
    }

    /**
     * 清除左側設定(保留預設 icon)
     */
    @Override
    public void clearLeftContent() {
        architectureBinding.ilToolbar.leftIcon.setVisibility(View.INVISIBLE);
        architectureBinding.ilToolbar.leftIcon.setOnClickListener(null);
        architectureBinding.ilToolbar.leftInnerContent.removeAllViews();
    }

    /**
     * 清除右側設定(保留預設 icon)
     */
    @Override
    public void clearRightContent() {
        architectureBinding.ilToolbar.rightIcon.setVisibility(View.INVISIBLE);
        architectureBinding.ilToolbar.rightIcon.setOnClickListener(null);
        architectureBinding.ilToolbar.rightInnerContent.removeAllViews();
    }

    /**
     * 清除左右側設定(保留預設 icon)
     */
    @Override
    public void clearLeftAndRightContent() {
        architectureBinding.ilToolbar.leftIcon.setVisibility(View.INVISIBLE);
        architectureBinding.ilToolbar.rightIcon.setVisibility(View.INVISIBLE);
        architectureBinding.ilToolbar.rightInnerContent.removeAllViews();
        architectureBinding.ilToolbar.leftInnerContent.removeAllViews();
    }

    @Override
    public ViewGroup getLeftInnerContent() {
        return architectureBinding.ilToolbar.leftInnerContent;
    }

    @Override
    public ViewGroup getRightInnerContent() {
        return architectureBinding.ilToolbar.rightInnerContent;
    }

    @Override
    public void showToolbar() {
        architectureBinding.appBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideToolbar() {
        architectureBinding.appBar.setVisibility(View.GONE);
    }

    /**
     * setBackGroundColor(int color) 中的需求簽章是 int resource ，然而 Color.RED is actual color value , not resource
     * 因此獲取顏色不可以用 setBackgroundColor(ContextCompat.getColor(context, Color.RED));
     * 應改用 setBackgroundColor(ContextCompat.getColor(context, R.color.RED)); 或  setBackgroundColor(Color.parseColor("#ff0000"));
     * 抑或是 setBackgroundColor(Color.RED)
     */
    public void showGeneralToolbar(int backGroundColor, int titleTextColor, int leftNavIcon,
                                   int rightNavIcon, String title,
                                   View.OnClickListener leftClickListener, View.OnClickListener rightClickListener) {
        setTitle(title, titleTextColor);
        setToolBarBackGroundColor(backGroundColor);

        if (leftNavIcon != AppConstant.NO_USE_TOOLBAR_LEFT_NAVIGATION) {
            setLeftBackButton(leftNavIcon, leftClickListener);
        } else {
            clearLeftContent();
        }
        if (rightNavIcon != AppConstant.NO_USE_TOOLBAR_RIGHT_NAVIGATION) {
            setRightBackButton(rightNavIcon, rightClickListener);
        } else {
            clearRightContent();
        }
        showToolbar();
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    public boolean isFirstLoad() {
        return isFirstLoad;
    }

    /**
     * 該 fragment 攔截系統返回鍵，並提供回調處理介面
     * 如果僅需要鎖定系統返回，則設定 lockSystemBack = true;
     * 若需要另外處理攔截後的客製邏輯，請實作 SystemBackListener 並設置
     */
    protected void catchSystemBackEvent() {
        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            //攔截到的系統返回事件
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (lockSystemBack) {
                    Logger.d("攔截到系統返回鍵" + "需求鎖定");
                    return true;
                }

                if (systemBackListener != null) {
                    Logger.d("攔截到系統返回鍵" + "需求鎖定並處理");
                    systemBackListener.handleBack();
                    return true;
                }
            }
            return false;
        });
    }

    public void setSystemBackListener(SystemBackListener systemBackListener) {
        this.systemBackListener = systemBackListener;
    }

    public interface SystemBackListener {
        /**
         * 處理系統返回事件
         */
        void handleBack();
    }

    public void setLockSystemBack(boolean lockSystemBack) {
        this.lockSystemBack = lockSystemBack;
    }
}
