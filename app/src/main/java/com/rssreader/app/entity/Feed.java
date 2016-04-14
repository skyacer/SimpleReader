package com.rssreader.app.entity;


/**
 * @author LuoChangAn
 */
public class Feed
{
	public static final String tag = "Feed";
	
	private String title;
	private String url;
	private int selectStatus;//是否已经选中
	
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	public boolean isSelected()
	{
		if(selectStatus == 1)
			return true;
		return false;
	}
	
	public void setSelectStatus(int selectStatus)
	{
		this.selectStatus = selectStatus;
	}
	public int getSelectStatus()
	{
		return selectStatus;
	}
	
}