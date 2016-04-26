package com.rssreader.app.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rssreader.app.adapter.GridAdapter;
import com.rssreader.app.adapter.MPagerAdapter;
import com.rssreader.app.commons.AppContext;
import com.rssreader.app.commons.DatabaseHelper;
import com.rssreader.app.commons.ItemListEntityParser;
import com.rssreader.app.commons.SeriaHelper;
import com.rssreader.app.commons.UIHelper;
import com.rssreader.app.dao.SectionDao;
import com.rssreader.app.entity.ItemListEntity;
import com.rssreader.app.entity.Section;
import com.rssreader.app.ui.R;
import com.rssreader.app.ui.base.BaseActivity;
import com.rssreader.app.ui.presenter.MainPresenter;
import com.rssreader.app.utils.ImageUtils;
import com.rssreader.custom.ui.PathAnimations;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends BaseActivity<MainPresenter>
{
	public static final String TAG = "MainActivity";
	private ViewPager mPager;
	private MPagerAdapter mPagerAdapter;
	private RelativeLayout composerWrapper;
	private RelativeLayout composerShowHideBtn;
	public RelativeLayout bgLayout;
	private ImageView composerShowHideIconIv;
	private TextView pageTv;
	public ImageButton switchModeBtn;
	private RelativeLayout homeLoadingLayout;
	public ArrayList<GridView> gridViews = new ArrayList<GridView>();
	public ArrayList<GridAdapter> gridAdapters = new ArrayList<GridAdapter>();
	private BroadcastReceiver mReceiver;
	private boolean arePathMenuShowing;
	public static final int PAGE_SECTION_SIZE = 8;// 一页8个section
	public static final String ACTION_ADD_SECTION = "com.rssreader.app.action.add_section";
	public static final String ACTION_DELETE_SECTION = "com.rssreader.app.action.delete_section";
	public static final int PAGE_SIZE_INCREASE = 1;
	public static final int PAGE_SIZE_NOT_CHANGE = 0;
	public static final int PAGE_SIZE_DECREASE = -1;
	private Intent mIntent;
	private boolean exit = false;//双击退出
	private boolean isEdting = false;//是否编辑section中
	public SectionDao sectionDAO;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initView();
		initPathMenu();
		initPager();
		initBroadcast();
	}

    @Override
    protected void initPresenter() {
        presenter = new MainPresenter(this);
    }
	
	private void initBroadcast()
	{
		mReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
                presenter.initReceive(context,intent);
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_ADD_SECTION);
		filter.addAction(ACTION_DELETE_SECTION);
		filter.addAction(SwitchBgActivity.SWITCH_HOME_BG);
		registerReceiver(mReceiver, filter);
	}

	private void initPathMenu()
	{
		PathAnimations.initOffset(this);
		composerWrapper = (RelativeLayout) findViewById(R.id.composer_wrapper);

		composerShowHideIconIv = (ImageView) findViewById(R.id.composer_show_hide_button_icon);

		composerShowHideBtn = (RelativeLayout) findViewById(R.id.composer_show_hide_button);
		composerShowHideBtn.setOnClickListener(presenter);

		// Buttons事件处理
		for (int i = 0; i < composerWrapper.getChildCount(); i++)
		{
			composerWrapper.getChildAt(i).setOnClickListener(presenter);
		}
		composerShowHideBtn.startAnimation(PathAnimations.getRotateAnimation(0,
                360, 200));
	}

	/**
	 * @description 初始化pagerView,DAO
	 */
	private void initPager()
	{
		sectionDAO = new SectionDao(this);
		int pageSize = getPageSize();
		for (int i = 0; i < pageSize; i++)
		{
			gridViews.add(newGridView(i));
			mPagerAdapter.notifyDataSetChanged();
		}
	}

	private void initView()
	{
		UIHelper.initTheme(this);
		setContentView(R.layout.main);
		switchModeBtn = (ImageButton) findViewById(R.id.composer_btn_moon);
		pageTv = (TextView) findViewById(R.id.home_page_tv);
		homeLoadingLayout = (RelativeLayout) findViewById(R.id.home_loading_layout);
		
		bgLayout = (RelativeLayout) findViewById(R.id.home_bg_layout);
		int resid = AppContext.getPrefrences(this).getInt("home_bg", R.drawable.home_bg_default); 
		bgLayout.setBackgroundResource(resid);
		
		mPager = (ViewPager) findViewById(R.id.home_pager);
		mPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels)
			{
				pageTv.setText(position + 1 + "");
			}

			@Override
			public void onPageScrollStateChanged(int state)
			{
			}
		});
		mPagerAdapter = new MPagerAdapter(gridViews);
		mPager.setAdapter(mPagerAdapter);
	}

	private GridView newGridView(int currentPage)
	{
		GridView grid = new GridView(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		grid.setLayoutParams(params);
		int right = ImageUtils.dip2px(this, 50);
		int left = ImageUtils.dip2px(this, 20);
		int top = ImageUtils.dip2px(this, 20);
		int bottom = ImageUtils.dip2px(this, 20);
		grid.setPadding(left, top, right, bottom);
		grid.setNumColumns(2);
		grid.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				GridAdapter adapter = gridAdapters.get(
							mPager.getCurrentItem());
				Section section = (Section) adapter.getItem(position);
				String title = section.getTitle();
				String url = section.getUrl();
				Log.d(TAG, url);
				//初始intent
				mIntent = new Intent();
				mIntent.putExtra("section_title", title);
				mIntent.putExtra("url", url);
				mIntent.setClass(MainActivity.this, ItemListActivity.class);
				
				//读取缓存
				File cache = DatabaseHelper.getSdCache(url);
				if(cache.exists())
				{
					MainActivity.this.startActivity(mIntent);
				}
				else
				{
					if(!AppContext.isNetworkAvailable(MainActivity.this))
					{
						Toast.makeText(MainActivity.this, R.string.no_network, Toast.LENGTH_SHORT).show();
						return;
					}
					//异步加载数据
					Log.d(TAG, "" + url);
					new LoadDataTask().execute(url);
				}
			}
		});
		//长按进入删除section状态
		grid.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
										   int position, long id) {
				inSectionEdit();
				return false;
			}
		});
		
		ArrayList<Section> sections = null;
		try
		{
			sections = sectionDAO.getList(currentPage);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		GridAdapter gridAdapter = new GridAdapter(this, sections);
		gridAdapters.add(gridAdapter);
		grid.setAdapter(gridAdapter);
		return grid;
	}

	private void inSectionEdit()
	{
		isEdting = true;
		int isVisble = 1;
		//section不可再点击
		for(int i = 0; i < gridViews.size(); i++)
		{
			gridViews.get(i).setEnabled(false);
		}
		for(int i = 0; i < gridAdapters.size(); i++)
		{
			gridAdapters.get(i).changeDelBtnState(isVisble);
		}
		Toast.makeText(this, R.string.exitEdit, Toast.LENGTH_SHORT).show();
	}
	
	//退出编辑模式
	private void outSectionEdit()
	{
		isEdting = false;
		int isVisble = 0;
		
		for(int i = 0; i < gridViews.size(); i++)
		{
			gridViews.get(i).setEnabled(true);
		}
		for(int i = 0; i < gridAdapters.size(); i++)
		{
			gridAdapters.get(i).changeDelBtnState(isVisble);
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		// 销毁广播接收器
		unregisterReceiver(mReceiver);
	}



    public void addGridView()
	{
		int lastPage = getPageSize() - 1;
		GridView grid = newGridView(lastPage);
		gridViews.add(grid);
		mPagerAdapter.notifyDataSetChanged();
	}

	public void removeLastGridView()
	{
		if (gridViews.isEmpty())
			return;
		gridViews.remove(gridViews.size() - 1);
		mPagerAdapter.notifyDataSetChanged();
	}

	public GridAdapter getLastGridAdapter()
	{
		if (gridAdapters.isEmpty())
			return null;
		return gridAdapters.get(gridAdapters.size() - 1);
	}

	public void removeLastGridAdapter()
	{
		if (gridAdapters.isEmpty())
			return;
		gridAdapters.remove(gridAdapters.size() - 1);
	}

	// 从1记
	private int getPageSize()
	{
		// pager分页
		int pageSize = 0;
		int sectionCount = sectionDAO.getCount();
		
		if (sectionCount % PAGE_SECTION_SIZE == 0)
			pageSize = sectionCount / PAGE_SECTION_SIZE;
		else
			pageSize = sectionCount / PAGE_SECTION_SIZE + 1;
		return pageSize;
	}

	public void showHideButton() {
		if (!arePathMenuShowing) {
			PathAnimations.startAnimationsIn(composerWrapper, 300);
			composerShowHideIconIv.startAnimation(PathAnimations
					.getRotateAnimation(0, -270, 300));
		} else {
			PathAnimations.startAnimationsOut(composerWrapper, 300);
			composerShowHideIconIv.startAnimation(PathAnimations
					.getRotateAnimation(-270, 0, 300));
		}
		arePathMenuShowing = !arePathMenuShowing;
	}

	private class LoadDataTask extends AsyncTask<String, Integer, ItemListEntity>
	{

		@Override
		protected void onPreExecute()
		{
			homeLoadingLayout.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(ItemListEntity result)
		{
			homeLoadingLayout.setVisibility(View.GONE);
			//跳转界面
			if(result != null && mIntent != null && !result.getItemList().isEmpty())
			{
				MainActivity.this.startActivity(mIntent);
			}
			else
			{
				Toast.makeText(MainActivity.this, R.string.networkexception, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected ItemListEntity doInBackground(String... params)
		{
			ItemListEntityParser parser = new ItemListEntityParser();
			ItemListEntity entity = parser.parse(params[0]);
			if(entity != null)
			{
				SeriaHelper helper = SeriaHelper.newInstance();
				File cache = DatabaseHelper.newSdCache(params[0]);
				helper.saveObject(entity, cache);
			}
			return entity;
		}
	}

	//返回true时，表示已经完整地处理了这个事件，并不希望其他的回调方法再次进行处理，
	//而当返回false时，表示并没有完全处理完该事件，更希望其他回调方法继续对其进行处理，
	//例如Activity中的回调方法
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(isEdting)
			{
				//在编辑，取消编辑
				outSectionEdit();
			}
			else
			{
				if(exit)
				{
					Log.d(TAG, "exit");
					finish();
					return true;
				}
				Toast.makeText(this, R.string.twice2Exit, Toast.LENGTH_SHORT).show();
				exit = true;
				Log.d(TAG, "after toast");
			}
		}
		return false;
	}

 }