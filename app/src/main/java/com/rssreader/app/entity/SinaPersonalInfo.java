package com.rssreader.app.entity;

/**
 * Created by LuoChangAn on 16/4/22.
 */
public class SinaPersonalInfo {
    String uid;
    int favourites_count;
    String location;
    String description;
    boolean verified;
    int friend_count;
    int gender;
    String screen_name;
    int status_name;
    int followers_count;
    String profile_image_url;
    String access_token;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getFavourites_count() {
        return favourites_count;
    }

    public void setFavourites_count(int favourites_count) {
        this.favourites_count = favourites_count;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getFriend_count() {
        return friend_count;
    }

    public void setFriend_count(int friend_count) {
        this.friend_count = friend_count;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public int getStatus_name() {
        return status_name;
    }

    public void setStatus_name(int status_name) {
        this.status_name = status_name;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
