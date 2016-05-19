package com.rssreader.app.module.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by LuoChangAn on 16/4/4.
 */
public abstract class BaseFragmentPresenter<T extends BaseFragment>
        extends BasePresenter<T>{

    public BaseFragmentPresenter(T target) {
        super(target);
    }

    public Context getContext(){
        return  target.getActivity();
    }

    public void onAttach() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    public void onCreateView() {
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    public void onDestroyView() {
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public void onDetach() {
    }
}
