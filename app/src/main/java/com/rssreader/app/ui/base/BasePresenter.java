package com.rssreader.app.ui.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LuoChangAn on 16/4/1.
 */
public abstract class BasePresenter<T>  {
    protected T target;

    public BasePresenter(T target) {
        this.target = target;
    }

    public void onCreate() {
    }

    public void onStart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onDestroy() {
    }
}
