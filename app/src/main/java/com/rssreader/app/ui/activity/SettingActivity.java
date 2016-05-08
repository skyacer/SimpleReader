package com.rssreader.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.rssreader.app.commons.AppConfig;
import com.rssreader.app.commons.AppContext;
import com.rssreader.app.service.PushService;
import com.rssreader.app.ui.R;
import com.rssreader.app.utils.FileUtils;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.io.File;

public class SettingActivity extends PreferenceActivity
{
	private SharedPreferences mPreferences;
	private CheckBoxPreference imageLoadCb;
    private CheckBoxPreference mPushSwitch;
	private Preference clearCachePref;
	private Preference feedbackPref;
	
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initView();
		initPreference();
	}

	private void initView()
	{
		addPreferencesFromResource(R.xml.preference);
		ListView mLv = getListView();
		mLv.setBackgroundColor(0);
		mLv.setCacheColorHint(0);
		((ViewGroup) mLv.getParent()).removeView(mLv);
		ViewGroup localViewGroup = (ViewGroup) getLayoutInflater().inflate(
				R.layout.setting, null);
		((ViewGroup) localViewGroup.findViewById(R.id.setting_content))
				.addView(mLv, -1, -1);
		setContentView(localViewGroup);
	
		//return btn
		localViewGroup.findViewById(R.id.setting_return_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});;
		
	}
	
	private void initPreference()
	{
		mPreferences = getPreferences(Context.MODE_PRIVATE);
		feedbackPref = findPreference("pref_feedback");
		feedbackPref.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, FeedbackUIActivity.class);
				SettingActivity.this.startActivity(intent);
				return false;
			}
		});
		imageLoadCb = (CheckBoxPreference) findPreference("pref_imageLoad");
		imageLoadCb.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				if(mPreferences.getBoolean("pref_imageLoad", true))
				{
					//显示图片
					imageLoadCb.setSummary("加载图片");
				}
				else
				{
					imageLoadCb.setSummary("不加载图片");
				}
				return false;
			}
		});
		//缓存
		// 计算缓存大小
		long fileSize = 0;
		String cacheSize = "0KB";
		File cacheDir = getCacheDir();
		File imageCacheDir = new File(AppConfig.APP_IMAGE_CACHE_DIR);
		File sectionCacheDir = new File(AppConfig.APP_SECTION_DIR);
		fileSize += FileUtils.getDirSize(cacheDir);
		fileSize += FileUtils.getDirSize(imageCacheDir);
		fileSize += FileUtils.getDirSize(sectionCacheDir);
		if(fileSize > 0)
			cacheSize = FileUtils.formatFileSize(fileSize);
		
		clearCachePref = findPreference("pref_clearCache");
		clearCachePref.setSummary(cacheSize);
		clearCachePref.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				new AsyncTask<Integer, Integer, Integer>()
				{
					@Override
					protected void onPostExecute(Integer result)
					{
						Toast.makeText(SettingActivity.this, "清理完毕！", Toast.LENGTH_SHORT).show();
						clearCachePref.setSummary("0KB");
					}

					@Override
					protected Integer doInBackground(Integer... params)
					{
						AppContext.clearCache(SettingActivity.this);
						return 0;
					}
				}.execute(0);
				return false;
			}
		});
		//push
        mPushSwitch = (CheckBoxPreference) findPreference("pref_push");
        mPushSwitch.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(mPreferences.getBoolean("pref_push", true))
                {
                    startService(new Intent(SettingActivity.this, PushService.class));

                }
                else
                {
                    stopService(new Intent(SettingActivity.this, PushService.class));
                }
                return false;
            }
        });

		//about
		findPreference("pref_about").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, AboutActivity.class);
				SettingActivity.this.startActivity(intent);
				return true;
			}
		});
		
//		//alarm tts
//		findPreference("pref_alarm").setOnPreferenceClickListener(new OnPreferenceClickListener() {
//			@Override
//			public boolean onPreferenceClick(Preference preference) {
//				Intent intent = new Intent();
//				intent.setClass(SettingActivity.this, AlarmTts.class);
//				SettingActivity.this.startActivity(intent);
//				return true;
//			}
//		});
		
		//update
		findPreference("pref_update").setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				final Context mContext = SettingActivity.this;
				
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			    @Override
			    public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
			        switch (updateStatus) {
			        case UpdateStatus.Yes: // has update
			            UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
			            break;
			        case UpdateStatus.No: // has no update
			            Toast.makeText(mContext, "已是最新版本", Toast.LENGTH_SHORT).show();
			            break;
			        case UpdateStatus.NoneWifi: // none wifi
			            Toast.makeText(mContext, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
			            break;
			        case UpdateStatus.Timeout: // time out
			            Toast.makeText(mContext, "超时", Toast.LENGTH_SHORT).show();
			            break;
			        	}
			    	}
				});
				UmengUpdateAgent.forceUpdate(mContext);
				return true;
			}
		});
	}
}
