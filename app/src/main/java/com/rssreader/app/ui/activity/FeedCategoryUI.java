package com.rssreader.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.rssreader.app.adapter.FeedCategoryAdapter;
import com.rssreader.app.commons.AppContext;
import com.rssreader.app.commons.util.ToastUtil;
import com.rssreader.app.dao.FeedCategoryDao;
import com.rssreader.app.entity.FeedCategory;
import com.rssreader.app.ui.R;

import java.util.ArrayList;

public class FeedCategoryUI extends FragmentActivity
{
	public static final String tag = "FeedCategoryUI";
	private ListView categoryLv;
	private TextView addBtnTv;
	private ArrayList<FeedCategory> fcList = new ArrayList<FeedCategory>();
	private FeedCategoryAdapter mAdapter;
	private FeedCategoryDao fcDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView()
	{
		setContentView(R.layout.feed_category);
		categoryLv = (ListView) findViewById(R.id.feed_category_lsit);
		addBtnTv = (TextView) findViewById(R.id.feed_category_add_btn);
		addBtnTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!AppContext.isNetworkAvailable(FeedCategoryUI.this)) {
					ToastUtil.makeShortToast(R.string.please_check_network);
					return;
				}
				AddFeedActivity.start(FeedCategoryUI.this);
//				new AddDialog().show(getSupportFragmentManager(), "添加Feed");
//				Toast.makeText(FeedCategoryUI.this, "开发中功能", Toast.LENGTH_SHORT).show();

			}
		});
		findViewById(R.id.feed_category_btn_back).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		fcDao = new FeedCategoryDao(this);
		fcList = fcDao.getList();
		mAdapter = new FeedCategoryAdapter(this, fcList);
		categoryLv.setAdapter(mAdapter);
		categoryLv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Intent intent = new Intent();
				intent.putExtra("category", fcList.get(position).getId());
				intent.setClass(FeedCategoryUI.this, FeedUIActivity.class);
				FeedCategoryUI.this.startActivity(intent);
			}
		});
	}
}
