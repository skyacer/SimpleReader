package com.rssreader.app.ui.presenter;

import android.view.View;
import android.widget.AdapterView;

import com.rssreader.app.commons.AppContext;
import com.rssreader.app.commons.util.ToastUtil;
import com.rssreader.app.ui.R;
import com.rssreader.app.ui.activity.FeedUIActivity;
import com.rssreader.app.ui.base.BasePresenter;

/**
 * Created by LuoChangAn on 16/4/14.
 */
public class FeedUIPresenter extends BasePresenter<FeedUIActivity> implements AdapterView.OnItemClickListener {
    public FeedUIPresenter(FeedUIActivity target) {
        super(target);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!AppContext.isNetworkAvailable(target))
        {
            ToastUtil.makeShortToast(R.string.no_network);
        }
    }
}
