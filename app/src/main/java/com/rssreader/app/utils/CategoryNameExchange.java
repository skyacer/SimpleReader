package com.rssreader.app.utils;

import android.content.Context;

import com.rssreader.app.ui.R;


/**
 * @author LuoChangAn
 */
public class CategoryNameExchange
{
	private String[] cates_zh;
	private String[] cates_en;

    public static String[] zh_type = {"读书文娱","新闻资讯","科学技术","体育赛事","动漫游戏","外语资讯","娱乐八卦","名人博客","其他分类"};

	public CategoryNameExchange(Context context)
	{
		cates_zh = context.getResources()
				.getStringArray(R.array.feed_category);
		cates_en = context.getResources()
				.getStringArray(R.array.feed_category_en);
	}
	
	public String zh2en(String zh)
	{
		for(int i = 0; i < cates_zh.length; i++)
		{
			if(zh.equals(cates_zh[i]))
				return cates_en[i];
		}
		return null;
	}
	
	public String en2zh(String en)
	{
		for(int i = 0; i < cates_en.length; i++)
		{
			if(en.equals(cates_en[i]))
				return cates_zh[i];
		}
		return null;
	}

    public static int zh2cid(String name){
        if (name.equals(zh_type[0])){
            return 1;
        }else if (name.equals(zh_type[1])){
            return 2;
        }else if (name.equals(zh_type[2])){
            return 3;
        }else if (name.equals(zh_type[3])){
            return 4;
        }else if (name.equals(zh_type[4])){
            return 5;
        }else if (name.equals(zh_type[5])){
            return 6;
        }else if (name.equals(zh_type[6])){
            return 7;
        }else if (name.equals(zh_type[7])){
            return 8;
        }else {
            return 9;
        }
    }
}
