package com.rssreader.app.ui.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.rssreader.app.adapter.GridAdapter;
import com.rssreader.app.commons.AppConfig;
import com.rssreader.app.commons.AppContext;
import com.rssreader.app.entity.Section;
import com.rssreader.app.ui.R;
import com.rssreader.app.ui.activity.FavoriteItemListActivity;
import com.rssreader.app.ui.activity.FeedCategoryActivity;
import com.rssreader.app.ui.activity.ItemListActivity;
import com.rssreader.app.ui.activity.MainActivity;
import com.rssreader.app.ui.activity.SettingActivity;
import com.rssreader.app.ui.activity.SwitchBgActivity;
import com.rssreader.app.ui.activity.UserInfoActivity;
import com.rssreader.app.ui.base.BasePresenter;
import com.umeng.update.UmengUpdateAgent;

import java.io.File;

/**
 * Created by LuoChangAn on 16/4/26.
 */
public class MainPresenter extends BasePresenter<MainActivity> implements View.OnClickListener {
    private boolean isNight;//是否为夜间模式


    public MainPresenter(MainActivity target) {
        super(target);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        checkShortcutMsg();
        checkDeprecated();
        checkVersion();
    }

    //检查是否来自shortcut的动作
    private void checkShortcutMsg()
    {
        Intent intent = target.getIntent();
        if(intent != null)
        {
            String action = intent.getAction();
            if(action != null && action.equals(GridAdapter.ACTION_ENTER_BY_SHORTCUT))
            {
                Intent indirectIntent = new Intent();
                indirectIntent.putExtra("section_title", intent.getStringExtra("section_title"));
                indirectIntent.putExtra("url", intent.getStringExtra("url"));
                indirectIntent.setClass(target, ItemListActivity.class);
                target.startActivity(indirectIntent);
            }
        }
    }

    /**
     * @description 检查缓存文件是否过期
     */
    private void checkDeprecated()
    {
        String fileName = target.getFilesDir().getAbsolutePath() + File.separator
                + AppConfig.PREF_DEPRECATED;
        File file = new File(fileName);
        int day = (int) (System.currentTimeMillis() - file.lastModified())/(24*60*60*1000);
        Log.d(MainActivity.TAG, "day = " + day);
        if(day >= 7)
        {
            AppContext.clearCache(target);
            file.setLastModified(System.currentTimeMillis());
        }
    }


    //切换壁纸
    private void swithBg()
    {
        Intent intent = new Intent();
        intent.setClass(target, SwitchBgActivity.class);
        target.startActivity(intent);
    }

    //收藏列表
    private void openFavorite()
    {
        Intent intent = new Intent();
        intent.setClass(target, FavoriteItemListActivity.class);
        target.startActivity(intent);
    }

    //打开设置界面
    private void openSetting()
    {
        Intent intent = new Intent();
        intent.setClass(target, SettingActivity.class);
        target.startActivity(intent);
    }

    // 打开订阅中心
    private void openSubscribeCenter()
    {
        Intent intent = new Intent();
        intent.setClass(target, FeedCategoryActivity.class);
        target.startActivity(intent);
    }

    private void switchMode()
    {
        isNight = AppContext.getPrefrences(target).getBoolean("day_night_mode", false);
        SharedPreferences.Editor editor = AppContext.getPrefrences(target).edit();
        //切回日间模式
        if(isNight)
        {
            isNight = false;
            int resid = AppContext.getPrefrences(target).getInt("home_bg", R.drawable.home_bg_default);
            target.bgLayout.setBackgroundResource(resid);
            target.switchModeBtn.setImageResource(R.drawable.composer_sun);
            Toast.makeText(target, R.string.switch2Day, Toast.LENGTH_SHORT).show();
        }
        else
        {
            //切回夜间模式
            isNight = true;
            target.bgLayout.setBackgroundResource(R.drawable.home_bg_night);
            target.switchModeBtn.setImageResource(R.drawable.composer_moon);
            Toast.makeText(target, R.string.switch2Night, Toast.LENGTH_SHORT).show();
        }
        editor.putBoolean("day_night_mode", isNight);
        editor.commit();
    }

    //检测新版本
    public void checkVersion()
    {
        UmengUpdateAgent.setUpdateOnlyWifi(true);
        UmengUpdateAgent.update(target);
    }

    //登陆
    private void login()
    {
        UserInfoActivity.startActivity(target);
    }

    public void initReceive(Context context, Intent intent){
        String action = intent.getAction();
        if (action.equals(MainActivity.ACTION_ADD_SECTION))
        {
            // 最后一个adapter为空或已满，新生一个gridView
            GridAdapter lastGridAdapter = target.getLastGridAdapter();
            if (lastGridAdapter == null || lastGridAdapter.isFull())
            {
                target.addGridView();
            } else
            {
                // 最后一个gridAdapter添加section
                lastGridAdapter.addItem(target.sectionDAO.getLast());
            }
        } else if (action.equals(MainActivity.ACTION_DELETE_SECTION))
        {
            // 根据移除此section
            GridAdapter deCreaseAdapter = null;

            String url = intent.getStringExtra("url");
            for (int i = 0; i < target.gridAdapters.size(); i++)
            {
                deCreaseAdapter = target.gridAdapters.get(i);
                if (deCreaseAdapter.removeItem(url))
                {
                    break;
                }
            }
            GridAdapter lastAdapter = target.getLastGridAdapter();
            if (lastAdapter.isEmpty())
            {
                if (target.gridViews.size() <= 1)
                {
                    return;
                }
                target.removeLastGridView();
                return;
            }
            if (!lastAdapter.equals(deCreaseAdapter))
            {
                Section section = lastAdapter.getLastItem();
                deCreaseAdapter.addItem(section);
                lastAdapter.removeItem(section.getUrl());
            }
            if (lastAdapter.isEmpty())
            {
                if (target.gridViews.size() <= 1)
                {
                    return;
                }
                target.removeLastGridView();
                target.removeLastGridAdapter();
            }
        }else if(action.equals(SwitchBgActivity.SWITCH_HOME_BG))
        {
            int resid = intent.getIntExtra("home_bg_id", R.drawable.home_bg_default);
           target.bgLayout.setBackgroundResource(resid);
            SharedPreferences.Editor editor = AppContext.getPrefrences(target).edit();
            editor.putInt("home_bg_id", resid);
            editor.commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.composer_btn_user:
                login();
                break;
            case R.id.composer_btn_setting:
                openSetting();
                break;
            case R.id.composer_btn_favorite:
                openFavorite();
                break;
            case R.id.composer_btn_switch_bg:
                swithBg();
                break;
            case R.id.composer_btn_add:
                openSubscribeCenter();
                break;
            case R.id.composer_btn_moon:
                switchMode();
                break;
            case R.id.composer_show_hide_button:
                target.showHideButton();
                break;
            default:
                break;
        }
    }



}
