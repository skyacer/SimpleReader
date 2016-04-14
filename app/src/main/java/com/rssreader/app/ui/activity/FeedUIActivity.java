package com.rssreader.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.rssreader.app.adapter.CategoryDetailAdapter;
import com.rssreader.app.dao.FeedDao;
import com.rssreader.app.entity.Feed;
import com.rssreader.app.ui.R;
import com.rssreader.app.ui.base.BaseActionBarActivity;
import com.rssreader.app.ui.presenter.FeedUIPresenter;

import java.util.ArrayList;

/**
 * @author LuoChangAn
 */
public class FeedUIActivity extends BaseActionBarActivity<FeedUIPresenter>
{
	public static final String tag = "CategoryDetail";
	
	private ListView detailList;
	private ArrayList<Feed> feeds = new ArrayList<Feed>();
	private CategoryDetailAdapter mAdapter;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	@Override
	protected void initPresenter() {
        presenter = new FeedUIPresenter(this);
	}

	private void initData()
	{
		FeedDao mDao = new FeedDao(this);
		Intent intent = getIntent();
		int cid = intent.getIntExtra("category", 1);
		Log.i(tag, "category.id = " + cid);
		String id = String.valueOf(cid);
		feeds = mDao.getListByCategoryId(id);
		//设置适配器
		mAdapter = new CategoryDetailAdapter(this, feeds, "feed");
		detailList.setAdapter(mAdapter);
	}

	private void initView()
	{
		setRealContentView(R.layout.category_detail);
		setTitle(R.string.subscribe_rss_center);
		setRightText(R.string.complete);

		detailList = (ListView) findViewById(R.id.catagory_detail_lv_feed);
		detailList.setOnItemClickListener(presenter);

        (findViewById(R.id.nav_right_container)).setOnClickListener(presenter);

	}
}

