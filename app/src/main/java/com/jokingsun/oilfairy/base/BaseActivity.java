package com.jokingsun.oilfairy.base;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.callback.iFragmentTransactionCallback;
import com.jokingsun.oilfairy.base.callback.iGeneralSettingCallback;
import com.jokingsun.oilfairy.ui.controller.ViewModelProviderFactory;
import com.jokingsun.oilfairy.utils.GeneralUtil;
import com.jokingsun.oilfairy.utils.MultiClickUtil;
import com.jokingsun.oilfairy.widget.helper.NetWorkStatusHelper;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.zyq.easypermission.EasyPermissionHelper;

/**
 * @author cfd058
 */
public abstract class BaseActivity<T extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity
        implements iGeneralSettingCallback, iFragmentTransactionCallback {

    public static final String FADE = "FADE";
    public static final String SLIDE = "SLIDE";
    public static final String SLIDE_UP = "SLIDE_UP";

    protected Toast toast;
    protected FragmentManager fragmentManager;
    protected VM viewModel;
    protected T binding;
    private KProgressHUD loading;
    private int containerId = 0;
    private NetWorkStatusHelper netWorkStatusHelper;
    private ViewModelProviderFactory factory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.factory = ViewModelProviderFactory.getInstance(this.getApplicationContext());
        initLoadingView();
        this.fragmentManager = this.getSupportFragmentManager();
        this.performDataBinding();
        this.initNetWorkManager();
        this.initView();
        this.initial();
    }

    protected abstract void initView();

    protected abstract void initial();

    private void initNetWorkManager() {
        if (netWorkStatusHelper == null) {
            netWorkStatusHelper = new NetWorkStatusHelper(this, () -> {
                //網路重新恢復工作
                viewModel.setWhenNetWorkRework();
            });
        }

        //將觀察者(MyLocationListener) 與 被觀察者 (Activity) 綁定
        getLifecycle().addObserver(netWorkStatusHelper);

    }

    /**
     * ----------------------------- DataBinding and ViewModel Attach-----------------------------
     * <p>
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * get layout id
     *
     * @return layout resource id
     */
    @LayoutRes
    public abstract int getLayoutId();

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract VM getViewModel();

    public ViewModelProviderFactory getFactory() {
        return this.factory;
    }


    public T getViewDataBinding() {
        return binding;
    }

    private void performDataBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        this.viewModel = viewModel == null ? getViewModel() : viewModel;

        //弱引用 Fragment 的 fragment 切頁方法
        this.viewModel.setNavigator(this);

        //取得布局中的 VM ，並賦值
        binding.setVariable(getBindingVariable(), viewModel);
        binding.executePendingBindings();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissionHelper.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults,
                this);
    }

    /**
     * ----------------------- Loading , Toast , Keyboard , Status Bar Setting -----------------
     */
    private void initLoadingView() {
        loading = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0);
    }

    @Override
    public void showLoading() {
        if (this.loading == null) {
            initLoadingView();
        }
        this.loading.show();
    }

    @Override
    public void cancelLoading() {
        if (this.loading == null) {
            initLoadingView();
        }

        this.loading.dismiss();
    }

    @Override
    public void showToast(CharSequence content) {
        this.showToast(content, true);
    }

    @Override
    public void showToast(CharSequence content, boolean isLong) {
        if (this.toast != null) {
            this.toast.cancel();
        }
        this.toast = Toast.makeText(this, null, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        this.toast.setText(content);
        this.toast.show();
    }

    @Override
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isActive()) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isActive()) {
                imm.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 設定 Activity 對應的頂部狀態列的顏色及 text color
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void updateStatusBarColor(Activity activity, int colorResId, boolean useDarkText) {
        GeneralUtil.updateStatusBarColor(activity, colorResId, useDarkText);
    }

    /**
     * 打印 Log 訊息
     */
    protected void printLog(String message) {
        GeneralUtil.printLog(this.getClass().getSimpleName(), message);
    }

    /**
     * --------------------------------- About Fragment Logic Setting ----------------------------
     */
    protected void setContainer(int containerId) {
        this.containerId = containerId;
    }

    /**
     * 加入新的 fragment 並移除目前的 fragment ，且將此次交易放入 stack
     */
    @Override
    public void AddFragment(Fragment fragment) {
        this.AddFragment(fragment, this.containerId, "SLIDE");
    }

    @Override
    public void AddFragment(Fragment fragment, int container) {
        this.AddFragment(fragment, container, "SLIDE");
    }

    @Override
    public void AddFragment(Fragment fragment, String anim) {
        this.AddFragment(fragment, this.containerId, anim);
    }

    @Override
    public void AddFragment(Fragment fragment, int container, String anim) {
        MultiClickUtil.clickButton(() -> {
            if (container == 0) {
                showToast("Please Set ContainerId ID");

            } else {
                FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
                switch (anim) {
                    case SLIDE:
                        fragmentTransaction.setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit, R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit);
                        break;
                    case SLIDE_UP:
                        fragmentTransaction.setCustomAnimations(R.animator.slide_fragment_in, R.animator.slide_fragment_out);
                        break;
                    case FADE:
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        break;
                    case "":
                    default:
                        break;
                }
                fragmentTransaction.replace(container, fragment, "main").
                        addToBackStack("main_interface").commitAllowingStateLoss();
                this.OnAddFragment();
            }
        });
    }

    @Override
    public void AddFragmentZoom(Fragment fragment, int container) {
        this.fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(container, fragment, "main").addToBackStack("main_interface").commitAllowingStateLoss();
        this.OnAddFragment();
    }

    @Override
    public void AddFragmentUp(Fragment fragment) {
        this.AddFragmentUp(fragment, this.containerId);
    }

    @Override
    public void AddFragmentUp(Fragment fragment, int container) {
        if (container == 0) {
            showToast("Please Set ContainerId ID");
        } else {
            Fragment originalFragment = this.fragmentManager.findFragmentById(container);
            if (!fragment.getClass().equals(originalFragment.getClass())) {
                this.fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_fragment_in,
                        R.animator.slide_fragment_out, R.animator.slide_fragment_in, R.animator.slide_fragment_out)
                        .replace(container, fragment, "main").addToBackStack("main_interface")
                        .commitAllowingStateLoss();
            }
            this.OnAddFragment();
        }
    }

    /**
     * 加入新的 fragment 並移除目前的 fragment ，但不將此次交易放入 stack ，一次性交易
     */
    @Override
    public void ReplaceFragment(Fragment fragment) {
        this.ReplaceFragment(fragment, this.containerId, "SLIDE");
    }

    @Override
    public void ReplaceFragment(Fragment fragment, String anim) {
        this.ReplaceFragment(fragment, this.containerId, anim);
    }

    @Override
    public void ReplaceFragment(Fragment fragment, int container) {
        this.ReplaceFragment(fragment, container, "SLIDE");
    }

    @Override
    public void ReplaceFragment(Fragment fragment, int container, String anim) {
        if (container == 0) {
            showToast("Please Set ContainerId ID");
        } else {
            this.PopAllBackStack();
            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
            switch (anim) {
                case SLIDE:
                    fragmentTransaction.setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit, R.animator.fragment_slide_right_exit, R.animator.fragment_slide_right_enter);
                    break;
                case SLIDE_UP:
                    fragmentTransaction.setCustomAnimations(R.animator.slide_fragment_in, R.animator.slide_fragment_out, R.animator.slide_fragment_in, R.animator.slide_fragment_out);
                    break;
                case FADE:
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    break;
                case "":
                default:
                    break;
            }
            fragmentTransaction.replace(container, fragment, "main").disallowAddToBackStack().commitAllowingStateLoss();
            this.OnReplaceFragment();
        }
    }


    /**
     * 一次性取出 stack 頂層最近一筆交易
     */
    @Override
    public void PopBackStack() {
        this.fragmentManager.popBackStack();
    }


    /**
     * 清除在定 ID 以上所有在 stack 的交易
     */
    @Override
    public void PopBackStack(int targetId) {
        int count = getFragmentManager().getBackStackEntryCount();
        //回到 targetId 指定的 fragment
        for (int j = (count - 1); j >= targetId; --j) {
            int backStackId = getFragmentManager().getBackStackEntryAt(j).getId();
            getFragmentManager().popBackStack(backStackId, 0);
        }
    }


    /**
     * 清除所有在 stack 的交易
     */
    @Override
    public void PopAllBackStack() {
        int backStackCount = this.fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; ++i) {
            int backStackId = this.fragmentManager.getBackStackEntryAt(i).getId();
            this.fragmentManager.popBackStack(backStackId, 1);
        }
    }

    @Override
    public void OnAddFragment() {
    }

    @Override
    public void OnReplaceFragment() {
    }

    /**
     * ------------------------------- About OnTouchEvent ----------------------------------------
     * <p>
     * 觸發有註冊 MyTouchListener 的 View (EX: 碰觸事件由當前有註冊的  Fragment 優先觸發)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        viewModel.handleOnTouchEvent(ev);

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (viewModel.isShouldHideKeyboard(v, ev)) {
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 防止系统字體大小影響 APP 中的字體
     */
    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return resources;
    }

}
