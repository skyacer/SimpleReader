package com.rssreader.app.ui.presenter;

import android.view.View;
import android.widget.AdapterView;

import com.rssreader.app.ui.activity.AddFeedActivity;
import com.rssreader.app.ui.base.BasePresenter;
import com.rssreader.app.utils.CategoryNameExchange;

/**
 * Created by LuoChangAn on 16/4/6.
 */
public class AddFeedPresenter extends BasePresenter<AddFeedActivity> implements AdapterView.OnItemSelectedListener {
    private String tableName_en;


    public AddFeedPresenter(AddFeedActivity target) {
        super(target);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String tableName = parent.getItemAtPosition(position).toString();
        tableName_en = new CategoryNameExchange(target).zh2en(tableName);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
