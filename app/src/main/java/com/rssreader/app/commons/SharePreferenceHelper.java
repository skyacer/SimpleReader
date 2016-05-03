package com.rssreader.app.commons;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.rssreader.app.application.AppProfile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LuoChangAn on 16/5/3.
 */
public class SharePreferenceHelper {
    private static final String APP_NAME = "YiYue";
    private static Map<String, SharedPreferences> sAccount2SP = new HashMap<String, SharedPreferences>();
    //表示是否需要缓存sp引用
    private static boolean sNeedCache = true;

    public static SharedPreferences getSharePreference(String accountName) {
        String spName = getSharePreferenceName(accountName);
        return getSharePreferencesWithSPName(spName, true);
    }

    public static void deleteSharePreference(String accountName){
        SharedPreferences sp = getSharePreference(accountName);
        sp.edit().clear().apply();
    }

    public static void putString(String accountName, String key, String value) {
        String spName = getSharePreferenceName(accountName);
        putStringWithSPName(spName, key, value);
    } public static String getString(String accountName, String key, String defValue) {
        String spName = getSharePreferenceName(accountName);
        return getStringWithSPName(spName, key, defValue);
    }

    public static String getString(Context context, String accountName, String key, String defValue) {
        String spName = getSharePreferenceName(accountName);
        return getStringWithSPName(context, spName, key, defValue);
    }

    public static void putLong(String accountName, String key, Long value) {
        String spName = getSharePreferenceName(accountName);
        putLongWithSPName(spName, key, value);
    }

    public static long getLong(String accountName, String key, long defValue) {
        String spName = getSharePreferenceName(accountName);
        return getLongWithSPName(spName, key, defValue);
    }

    public static void putInt(String accountName, String key, int value) {
        String spName = getSharePreferenceName(accountName);
        putIntWithSPName(spName, key, value);
    }

    public static int getInt(String accountName, String key, int defValue) {
        String spName = getSharePreferenceName(accountName);
        return getIntWithSPName(spName, key, defValue);
    }

    public static void putBoolean(String accountName, String key, boolean value) {
        String spName = getSharePreferenceName(accountName);
        putBooleanWithSPName(spName, key, value);
    }

    public static boolean getBoolean(String accountName, String key, boolean defValue) {
        String spName = getSharePreferenceName(accountName);
        return getBooleanWithSPName(spName, key, defValue);
    }

    public static void putFloat(String accountName, String key, float value) {
        String spName = getSharePreferenceName(accountName);
        putFloatWithSPName(spName, key, value);
    }

    public static float getFloat(String accountName, String key, float defValue) {
        String spName = getSharePreferenceName(accountName);
        return getFloatWithSPName(spName, key, defValue);
    }

    public static void remove(String accountName, String key) {
        String spName = getSharePreferenceName(accountName);
        removeWithSPName(spName, key);
    }
    private static void removeWithSPName(String spName, String key) {
        SharedPreferences sp = getSharePreferencesWithSPName(spName, true);
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(key);
            editor.commit();
        }
    }

    private static boolean getBooleanWithSPName(String spName, String key, boolean defValue) {
        SharedPreferences sp = getSharePreferencesWithSPName(spName, true);
        return sp != null ? sp.getBoolean(key, defValue) : defValue;
    }

    private static void putBooleanWithSPName(String spName, String key, boolean value) {
        SharedPreferences sp = getSharePreferencesWithSPName(spName, true);
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    private static float getFloatWithSPName(String spName, String key, float defValue) {
        SharedPreferences sp = getSharePreferencesWithSPName(spName, true);
        return sp != null ? sp.getFloat(key, defValue) : defValue;
    }

    private static void putFloatWithSPName(String spName, String key, float value) {
        SharedPreferences sp = getSharePreferencesWithSPName(spName, true);
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat(key, value);
            editor.commit();
        }
    }

    private static int getIntWithSPName(String spName, String key, int defValue) {
        SharedPreferences sp = getSharePreferencesWithSPName(spName, true);
        return sp != null ? sp.getInt(key, defValue) : defValue;
    }

    private static void putIntWithSPName(String spName, String key, int value) {
        SharedPreferences sp = getSharePreferencesWithSPName(spName, true);
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(key, value);
            editor.commit();
        }
    }

    private static long getLongWithSPName(String spName, String key, long defValue) {
        SharedPreferences sp = getSharePreferencesWithSPName(spName, true);
        return sp != null ? sp.getLong(key, defValue) : defValue;
    }

    private static void putLongWithSPName(String spName, String key, long value) {
        SharedPreferences sp = getSharePreferencesWithSPName(spName, true);
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong(key, value);
            editor.commit();
        }
    }

    private static String getStringWithSPName(String spName, String key, String defValue) {
        SharedPreferences sp = getSharePreferencesWithSPName(spName, true);
        return (sp != null) ? sp.getString(key, defValue) : defValue;
    }

    private static String getStringWithSPName(Context context, String spName, String key, String defValue) {
        SharedPreferences sp = getSharePreferencesWithSPName(context, spName, true);
        return (sp != null) ? sp.getString(key, defValue) : defValue;
    }


    private static void putStringWithSPName(String spName, String key, String value) {
        SharedPreferences sp = getSharePreferencesWithSPName(spName, true);
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    private static void putStringWithSPName(Context context, String spName, String key, String value) {
        SharedPreferences sp = getSharePreferencesWithSPName(context, spName, true);
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }


    private static String getSharePreferenceName(String accountName) {
        if (TextUtils.isEmpty(accountName)) {
            return null;
        }
        return accountName + APP_NAME;
    }

    private static SharedPreferences getSharePreferencesWithSPName(String spName, boolean createNewIfNotExist) {
        return getSharePreferencesWithSPName(AppProfile.getContext(), spName, createNewIfNotExist);
    }

    private static SharedPreferences getSharePreferencesWithSPName(Context context, String spName, boolean createNewIfNotExist) {
        if (TextUtils.isEmpty(spName)) {
            return null;
        }
        SharedPreferences sp = null;
        if (sNeedCache) {
            sp = sAccount2SP.get(spName);
        }
        if (sp == null && createNewIfNotExist) {
            sp = context.getSharedPreferences(spName, Context.MODE_MULTI_PROCESS);
            sAccount2SP.put(spName, sp);
        }
        return sp;
    }

}
