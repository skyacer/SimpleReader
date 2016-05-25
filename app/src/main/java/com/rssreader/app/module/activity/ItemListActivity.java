package com.rssreader.app.module.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.rssreader.app.module.adapter.ItemListAdapter;
import com.rssreader.app.commons.DatabaseHelper;
import com.rssreader.app.commons.HtmlFilter;
import com.rssreader.app.commons.SeriaHelper;
import com.rssreader.app.commons.UIHelper;
import com.rssreader.app.entity.FeedItem;
import com.rssreader.app.entity.ItemListEntity;
import com.rssreader.app.module.R;
import com.rssreader.app.module.base.BaseActionBarActivity;
import com.rssreader.app.module.presenter.ItemListPresenter;

import java.io.File;
import java.util.ArrayList;

/**
 * @author LuoChangAn
 */
public class ItemListActivity extends BaseActionBarActivity<ItemListPresenter>
{

	public static final String tag = "ItemListActivity";
	public PullToRefreshListView itemLv;
	public ItemListAdapter mAdapter;
	public SeriaHelper seriaHelper;
	public ArrayList<FeedItem> mItems = new ArrayList<FeedItem>();
	public ArrayList<String> speechTextList = new ArrayList<String>();
	public String sectionTitle;
	public String sectionUrl;
	private BroadcastReceiver mReceiver;

	public static final String ACTION_UPDATE_ITEM_LIST = "com.rssreader.action.update_item_list";
	private boolean isNight;// 是否夜间

    @Override
    protected void initPresenter() {
        presenter = new ItemListPresenter(this);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        initView();
        initData();
        initBroadeCast();
	}

	private void initBroadeCast()
	{
		mReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				String link = intent.getStringExtra("link");
				boolean isFavorite = intent.getBooleanExtra("is_favorite",
						false);
				for (FeedItem i : mItems)
				{
					if (i.getLink().equals(link))
					{
						i.setFavorite(isFavorite);
						break;
					}
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_UPDATE_ITEM_LIST);
		registerReceiver(mReceiver, filter);
	}



	private void initView()
	{
		UIHelper.initTheme(this);
		setRealContentView(R.layout.feed_item_list);

        Intent intent = getIntent();
        sectionUrl = intent.getStringExtra("url");
        sectionTitle = intent.getStringExtra("section_title");

        setTitle(sectionTitle);


		itemLv = (PullToRefreshListView) findViewById(R.id.fil_lv_feed_item);

        itemLv.setOnRefreshListener(presenter);

		itemLv.setOnItemClickListener(presenter);
	}

	private void initData() {
		File file = DatabaseHelper.getSdCache(sectionUrl);
		if (file.exists())
		{
			seriaHelper = SeriaHelper.newInstance();
			ItemListEntity itemListEntity = (ItemListEntity) seriaHelper
					.readObject(file);
			mItems = itemListEntity.getItemList();
			if (mItems != null)
			{
				mAdapter = new ItemListAdapter(this, mItems, isNight);
				itemLv.setAdapter(mAdapter);
				for (int i = 0, n = mItems.size(); i < n; i++)
				{
					FeedItem item = mItems.get(i);
					String input = item.getTitle() + item.getContent();
					speechTextList.add(HtmlFilter.filterHtml(input));
				}
			}
		}
	}




	@Override
    public void onDestroy()
	{

		unregisterReceiver(mReceiver);
		super.onDestroy();
	}


}
