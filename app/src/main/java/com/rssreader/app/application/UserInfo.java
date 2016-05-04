package com.rssreader.app.application;

import com.rssreader.app.commons.SharePreferenceHelper;

/**
 * Created by LuoChangAn on 16/5/3.
 */
public class UserInfo {
    public static String USER_SP = "user_sp";

    public static String USER_AVATAR_ADDRESS = "user_avatar";
    private static String USER_GENDER_KEY = "user_gender";
    private static String USER_ID_KEY = "user_id";
    private static String USER_NICK_NAME_KEY="user_nickname";
    private static String USER_AREA_KEY = "user_area";
    private static String USER_LOGIN_PLATFORM_KEY = "user_login_platform";

    public static void setUserAvatar(String avatar){
        SharePreferenceHelper.putString(USER_SP,USER_AVATAR_ADDRESS, avatar);
    }

    public static String getUserAvatar(){
        return SharePreferenceHelper.getString(USER_SP,USER_AVATAR_ADDRESS,null);
    }

    public static void setUserGender(int gender){
        SharePreferenceHelper.putInt(USER_SP,USER_GENDER_KEY, gender);
    }

    public static int getUserGender(){
        return SharePreferenceHelper.getInt(USER_SP,USER_GENDER_KEY,-1);
    }

    public static void setUserInfoId(String id){
        SharePreferenceHelper.putString(USER_SP,USER_ID_KEY, id);
    }

    public static String getUserInfoId(){
        return SharePreferenceHelper.getString(USER_SP,USER_ID_KEY, null);
    }

    public static void setUserNickName(String nickName){
        SharePreferenceHelper.putString(USER_SP,USER_NICK_NAME_KEY, nickName);
    }

    public static String getUserNickName(){
        return SharePreferenceHelper.getString(USER_SP,USER_NICK_NAME_KEY, null);
    }

    public static void setUserArea(String area){
        SharePreferenceHelper.putString(USER_SP,USER_AREA_KEY,area);
    }

    public static String getUserArea(){
        return SharePreferenceHelper.getString(USER_SP,USER_AREA_KEY,null);
    }

    public static void setUserPlatForm(int platform){
        SharePreferenceHelper.putInt(USER_SP,USER_LOGIN_PLATFORM_KEY,platform);
    }

    public static int getUserPlatForm(){
        return SharePreferenceHelper.getInt(USER_SP,USER_LOGIN_PLATFORM_KEY,-1);
    }

    public static void clearData(){
        SharePreferenceHelper.deleteSharePreference(USER_SP);
    }
}
