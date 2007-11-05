
/* Odeo.java

 {{IS_NOTE
 Purpose:ZK Odeo Component
 
 Description:
 
 History:
 Jul 17, 2007 , Created by jeffliu
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package  org.zkoss.flash;

import org.zkoss.zul.Flash;

import java.util.Random;

import org.zkoss.lang.Objects;

/**
 * 
 * @author Jeff
 */
public class Odeo extends Flash  {
	private static final long serialVersionUID = 1L;
	
	private String _skinSize ="standard";
	
	private String _skinColor ="gray";
	
	private String _audioDuration = "180.0"; 
	
	/**
	 * Initials the skin size of odeo player
	 *
	 */
	public Odeo()
	{
		setSkinSize(_skinSize);
	}
	
	/**
	 * Sets the audio duration in second
	 * @param duration
	 */
	public void setAudioDuration(String duration)
	{
		_audioDuration = duration;
	}
	
	/**
	 * Gets the audio duraction in second
	 * @return the audio duraction in second
	 */
	public String getAudioDuration()
	{
		return _audioDuration;
	}

	
	/**
	 * Sets the skin size of Odeo player
	 * @param size
	 */
	public void setSkinSize(String size)
	{
		String[] skins = {"standard","midsize","tiny"};
		String height ="";
		String width ="";
		
		//Change height and width of Odeo play with skin size 
		switch(findSkinhelper(skins, size))
		{
			case 0:
			{
				height="52";
				width="300";
				break;
			}
			case 1:
			{
				height="60";
				width="150";
				break;
			}
			case 2:
			{
				height="25";
				width="145";
				break;
			}
		}

		super.setHeight(height);
		super.setWidth(width);
		
		_skinSize = size;
		invalidate();
	}
	
	/**
	 * Helper function: Find the index of a skin in skins
	 * @param skins
	 * @param skin
	 * @return the index of a skin in skins
	 */
	private int findSkinhelper(String[] skins, String skin)
	{
		for(int i=0; i<skins.length;i++)
		{
			if(Objects.equals(skins[i], skin))
				return i;
		}
		return -1;
	}
	
	/**
	 * Sets the skin size of Odeo player
	 * @return the skin size of Odeo player
	 */
	public String getSkinSize()
	{
		return _skinSize;
	}
	
	/**
	 * Sets the skin color of Odeo player
	 * @param color
	 */
	public void setSkinColor(String color)
	{
		_skinColor = color;
		invalidate();
	}
	
	/**
	 * Gets the skin color of Odeo player
	 * @return the skin color of Odeo player
	 */
	public String getSkinColor()
	{
		return _skinColor;
	}
	
	/**
	 * Returns the song source url
	 * @return the song source url
	 */
	public String getSongUrl() {
		
		//Prevent IE caching causes flash player problem by adding a random number behing URL 
		Random generator = new Random(); 
		int seed = generator.nextInt();
		String _autoPlayQuery = "";
		if(super.isAutoPlay())
		{
			_autoPlayQuery = "&auto_play=true";
		}
		return super.getSrc()+_autoPlayQuery+"&l="+seed;
	}
	
	/**
	 * Sets the song source url
	 * @param songUrl
	 */
	public void setSongUrl(String songUrl) {
		super.setSrc(songUrl);
		invalidate();
	}
	
	/**
	 * Start Playing the mp3 file
	 *
	 */
	public void play()
	{
		super.setAutoPlay(true);
		invalidate();
	}
	
	/**
	 * Stop Playing the mp3 file
	 *
	 */
	public void stop()
	{
		super.setAutoPlay(false);
		invalidate();
	}
	
	/**
	 * Sets visiblity of Odeo player
	 */
	public boolean setVisible(boolean visible)
	{
		if(super.isVisible() != visible)
		{
			super.setVisible(visible);
			invalidate();
		}
		return super.isVisible();
	}
	
	/**
	 * Sets whether Odeo play is going to play repeatly
	 */
	public void setAutoPlay(boolean autoplay)
	{
		if(super.isAutoPlay() != autoplay)
		{
			super.setAutoPlay(autoplay);
			invalidate();
		}
	}
}
