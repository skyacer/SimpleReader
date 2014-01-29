package com.dreamteam.app.commons;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import com.dreamteam.app.entity.ItemListEntity;


/**
 * @description 
 * @author zcloud
 * @date 2013/11/15
 */
public class SerializationHelper
{
	private static SerializationHelper helper;
	
	private SerializationHelper(){}
	
	/**
	 * @param file
	 * @return Serializable|null
	 */
	public Serializable readObject(File file)
	{
		if(file.exists() == false)
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		
		try
		{
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			ItemListEntity ile = (ItemListEntity) ois.readObject();
			return ile;
		}
		catch(StreamCorruptedException e)
		{
			e.printStackTrace();
			return null;
		}
		catch(OptionalDataException e)
		{
			e.printStackTrace();
			return null;
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			if(fis != null)
			{
				try
				{
					fis.close();
					fis = null;
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			if(ois != null)
			{
				try
				{
					ois.close();
					ois = null;
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * @param seria
	 * @param file
	 * @return
	 * true:save successful
	 * false:save failed
	 */
	public boolean saveObject(Serializable seria, File file)
	{
		if(file.exists() == false)
			return false;
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		try
		{
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(seria);
			return true;
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(fos != null)
			{
				try
				{
					fos.close();
					fos = null;
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			if(oos != null)
			{
				try
				{
					oos.flush();
					oos.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public static SerializationHelper newInstance()
	{
		if(helper == null)
		{
			helper = new SerializationHelper();
		}
		return helper;
	}
}
