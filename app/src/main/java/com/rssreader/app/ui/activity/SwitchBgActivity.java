package com.rssreader.app.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.rssreader.app.ui.R;
import com.rssreader.app.ui.base.BaseActionBarActivity;
import com.rssreader.app.ui.presenter.SwitchBgPresenter;

public class SwitchBgActivity extends BaseActionBarActivity<SwitchBgPresenter> implements
	ViewSwitcher.ViewFactory {


	public ImageSwitcher mSwitcher;

	public static final String SWITCH_HOME_BG = "com.rssreader.action.swtich_home_bg";

	@Override
	protected void initPresenter() {
		presenter = new SwitchBgPresenter(this);
	}

    
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRealContentView(R.layout.switch_bg);
		setTitle(R.string.set_homepage_background);
		setRightView(R.drawable.btn_ok);


		mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
		mSwitcher.setFactory(this);
		mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));

		Gallery g = (Gallery) findViewById(R.id.gallery);
		g.setAdapter(new ImageAdapter(this));
		g.setOnItemSelectedListener(presenter);
		
		findViewById(R.id.nav_right_img).setOnClickListener(presenter);

	}
	public class ImageAdapter extends BaseAdapter
	{
		public ImageAdapter(Context c)
		{
			mContext = c;
		}

		public int getCount()
		{
			return presenter.mImageIds.length;
		}

		public Object getItem(int position)
		{
			return position;
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			ImageView i = new ImageView(mContext);

			i.setImageResource(presenter.mImageIds[position]);
			i.setAdjustViewBounds(true);
			i.setLayoutParams(new Gallery.LayoutParams(
					Gallery.LayoutParams.WRAP_CONTENT, Gallery.LayoutParams.WRAP_CONTENT));
			i.setBackgroundResource(R.drawable.picture_frame);
			return i;
		}

		private Context mContext;

	}



	public View makeView()
	{
		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return i;
	}



}
