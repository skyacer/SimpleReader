package com.rssreader.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.rssreader.app.ui.R;
import com.rssreader.app.ui.base.BaseActionBarActivity;
import com.rssreader.app.ui.presenter.AddFeedPresenter;

/**
 * Created by LuoChangAn on 16/4/6.
 */
public class AddFeedActivity extends BaseActionBarActivity<AddFeedPresenter> {
    private Spinner mSpinner;


    @Override
    protected void initPresenter() {
        presenter = new AddFeedPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRealContentView(R.layout.activity_add_feed);
        initView();
    }

    private void initView() {
        setTitle(R.string.add_feed_title);
        setRightText(R.string.submit);

        mSpinner = (Spinner) findViewById(R.id.spinner_rss_category_right);
        ArrayAdapter<CharSequence> cateAdapter = ArrayAdapter.createFromResource(AddFeedActivity.this
                , R.array.feed_category, android.R.layout.simple_spinner_item);
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(cateAdapter);
        mSpinner.setOnItemSelectedListener(presenter);
    }

    public static void start(Activity activity){
        Intent intent = new Intent(activity,AddFeedActivity.class);
        activity.startActivity(intent);
    }

}
