/* XspfPlayer.java

 {{IS_NOTE
 Purpose: ZK XspfPlayer Component
 
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

/**
 *
 *@author Jeff 
 */
public class XspfPlayer extends Flash  {
	
	private static final long serialVersionUID = 1L;

	private String _songTitle ="";

	
	/**
	 * Returns the song title.
	 *@return the song title
	 */
	public String getSongTitle()
	{
		return _songTitle;
	}
	
	/**
	 * Sets the song title
	 * @param songTitle
	 */
	public void setSongTitle(String songTitle)
	{
		_songTitle = songTitle;
	}
	
	/**
	 * Sets visiblity of XspfPlay
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
	 * Sets whether XspfPlayer is going to play repeatly
	 */
	public void setAutoPlay(boolean autoplay)
	{
		if(super.isAutoPlay() != autoplay)
		{
			super.setAutoPlay(autoplay);
			invalidate();
		}
	}
	
	/**
	 * Returns the song source url
	 * @return  the song source url
	 */
	public String getSongUrl() {
		
		//Prevent IE caching causes flash player problem by adding a random number behing URL 
		Random generator = new Random(); 
		int seed = generator.nextInt();
		String _autoPlayQuery = "";
		if(super.isAutoPlay())
		{
			_autoPlayQuery = "&autoplay=true";
		}
		return super.getSrc()+"&song_title="+_songTitle+_autoPlayQuery+"&l="+seed;
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
	 * Stop playing the mp3 file
	 *
	 */
	public void stop()
	{
		super.setAutoPlay(false);
		invalidate();
	}

}
