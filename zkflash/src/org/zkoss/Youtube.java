/* Youtube.java

 {{IS_NOTE
 Purpose: ZK Youtube Component
 
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

/**
 * @author Jeff
 */
public class Youtube extends Flash {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Initials the default height and width
	 *
	 */
	public Youtube()
	{
		//Recommanded Height and Width by youtube
		super.setHeight("350");
		super.setWidth("425");
	}
	
	/**
	 * Sets whether the Yourtube clip plays repeatly 
	 */
	public void setLoop(boolean loop)
	{
		if(super.isLoop() != loop)
		{
			super.setLoop(loop);
			invalidate();
		}
	}
	
	/**
	 *  Gets the source path of Youtube clip and add
	 *  loop and autoplay query agruments to the src
	 */
	public String getSrc()
	{
		String src =super.getSrc();
		if(super.isAutoPlay())
		{
			src += "&autoplay=1";
		}
		if(this.isLoop())
		{
			src += "&loop=1";
		}	
		return src;
		
	}
	
	/**
	 * Sets whether the Youtube clip plays repeatly
	 * @param autoplay
	 */
	public void setAutoPlay(boolean autoplay) {
			super.setAutoPlay(autoplay);
			invalidate();
	}
	
	
}
