package com.rssreader.app.commons;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.rssreader.app.application.AppConfig;
import com.rssreader.app.db.DbConstant;
import com.rssreader.app.commons.util.FileUtils;
import com.rssreader.app.commons.util.MD5;

import java.io.File;

public class DatabaseHelper
{
	
	public static void removeRecord(SQLiteDatabase db, String url)
	{
		db.delete(DbConstant.SECTION_TABLE_NAME, "url=?", new String[]{url});
		db.close();
	}
	
	public static void insertToSection(SQLiteDatabase db, String tableName, String title, String url)
	{
		ContentValues values = new ContentValues();
		values.put("table_name", tableName);
		values.put("title", title);
		values.put("url", url);
		db.insert(DbConstant.SECTION_TABLE_NAME, null, values);
	}

    public static void insertToFeed(SQLiteDatabase db,String fname,String url,int cid){
        if (db==null||fname==null||url==null||cid<=0||cid>=9){
            return;
        }
        ContentValues values = new ContentValues();
        values.put("fname",fname);
        values.put("url",url);
        values.put("cid",cid);
        db.insert(DbConstant.FEED_TABLE_NAME,null,values);
    }

    public static void removeRecordFromFeed(SQLiteDatabase db,String fname){
        if (db==null||fname==null){
            return;
        }

        String[] args = {fname};
        db.delete(DbConstant.FEED_TABLE_NAME,"fname=?",args);
    }

	public static File newSdCache(String url)
	{
		String name = AppConfig.APP_SECTION_DIR + File.separator
					+ MD5.Md5(url);
		return FileUtils.newAbsoluteFile(name);
	}

	public static File getSdCache(String url)
	{
		return new File(AppConfig.APP_SECTION_DIR + File.separator + MD5.Md5(url));
	}
}
