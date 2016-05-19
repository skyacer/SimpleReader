package com.rssreader.app.module.base;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.rssreader.app.module.common.OnSlidingFinishListener;
import com.rssreader.app.module.common.SlidingFinishLayout;

/**
 * Created by LuoChangAn on 16/4/1.
 */
public abstract class BaseActivity<T extends BasePresenter> extends Activity
        implements OnSlidingFinishListener {
    protected T presenter;
    /**
     * 用于监听滑动的view
     */
    /**
     * 用于监听滑动的view
     */
    protected SlidingFinishLayout contentView;
    protected View touchView;
    protected ViewGroup rootView;

    /**
     * 用于监听inflater view
     */
    protected LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInflater = LayoutInflater.from(this);

        initPresenter();
        if (presenter != null) {
            presenter.onCreate();
        }
    }


    /**
     * Inflate a content view for the activity.
     *
     * @param resId ID for an XML layout resource as the content view
     */
    public void setRealContentView(@LayoutRes int resId) {
        getLayoutInflater().inflate(resId, contentView);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    protected void disableScreenCapture() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (presenter != null) {
            presenter.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (presenter != null) {
            presenter.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (presenter != null) {
            presenter.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    abstract protected void initPresenter();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {//MENU键
            //监控/拦截菜单键
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onSlidingFinish() {
        finish();
    }

    public void setSlidingFinishEnabled(boolean isSlidingEnabled) {
        if (contentView != null) {
            contentView.setIsSlidingEnabled(isSlidingEnabled);
        }
    }

    public boolean isSlidingFinishEnabled() {
        return contentView != null && contentView.isSlidingEnabled();
    }



    protected <T extends View> T findView(@IdRes int id) {
        return (T) findViewById(id);
    }

}