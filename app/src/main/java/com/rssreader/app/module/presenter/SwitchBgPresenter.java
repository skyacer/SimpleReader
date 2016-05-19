package com.rssreader.app.module.presenter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.rssreader.app.application.AppContext;
import com.rssreader.app.module.R;
import com.rssreader.app.module.activity.SwitchBgActivity;
import com.rssreader.app.module.base.BasePresenter;

/**
 * Created by LuoChangAn on 16/4/6.
 */
public class SwitchBgPresenter extends BasePresenter<SwitchBgActivity>
        implements View.OnClickListener,ViewSwitcher.ViewFactory, AdapterView.OnItemSelectedListener {
    public Integer[] mImageIds = {
            R.drawable.home_bg_night,
            R.drawable.home_bg_default,
            R.drawable.home_bg_0
    };

    private int selectedPosition;

    public static final String SWITCH_HOME_BG = "com.rssreader.action.swtich_home_bg";


    public SwitchBgPresenter(SwitchBgActivity target) {
        super(target);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nav_right_img:
                SharedPreferences prefs = AppContext.getPrefrences(target);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("home_bg", mImageIds[selectedPosition]);
                editor.commit();
                Intent intent = new Intent();
                intent.setAction(SWITCH_HOME_BG);
                intent.putExtra("home_bg_id", mImageIds[selectedPosition]);
                target.sendBroadcast(intent);
                Toast.makeText(target, R.string.bg_switch_success, Toast.LENGTH_SHORT).show();
                target.finish();
                break;
        }
    }

    @Override
    public View makeView() {
        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedPosition = position;
        target.mSwitcher.setImageResource(mImageIds[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
