package com.rssreader.app.module.presenter;

import android.view.View;
import android.widget.AdapterView;

import com.rssreader.app.application.AppContext;
import com.rssreader.app.commons.util.ToastUtil;
import com.rssreader.app.module.R;
import com.rssreader.app.module.activity.FeedUIActivity;
import com.rssreader.app.module.base.BasePresenter;

/**
 * Created by LuoChangAn on 16/4/14.
 */
public class FeedUIPresenter extends BasePresenter<FeedUIActivity> implements AdapterView.OnItemClickListener, View.OnClickListener {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nav_right_container:
                target.setResult(target.RESULT_OK);
                target.finish();
                break;
            default:
                break;
        }
    }
}
