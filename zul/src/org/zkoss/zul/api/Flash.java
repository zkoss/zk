/* Flash.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */

package org.zkoss.zul.api;

/**
 * A generic flash component.
 * 
 * <p>
 * Non XUL extension.
 * 
 * @author Jeff
 * @since 3.5.2
 */
public interface Flash extends org.zkoss.zk.ui.api.HtmlBasedComponent {

	/**
	 * Gets the background color of Flash movie
	 * 
	 * @return the background color of Flash movie,[ hexadecimal RGB value]
	 */
	public String getBgcolor();

	/**
	 * Sets the background color of Flash movie
	 * 
	 * @param bgcolor
	 *            [ hexadecimal RGB value]
	 */
	public void setBgcolor(String bgcolor);

	/**
	 * Returns true if the Flash movie plays repeatly
	 * 
	 * @return true if the Flash movie plays repeatly
	 */
	public boolean isLoop();

	/**
	 * Sets whether the Flash movie plays repeatly
	 * 
	 * @param loop
	 */
	public void setLoop(boolean loop);

	/**
	 * Return true if the Flash movie starts playing automatically
	 * 
	 * @return true if the Flash movie starts playing automatically
	 */
	public boolean isAutoPlay();

	/**
	 * Sets wether the song Flash movie playing automatically
	 * 
	 * @param play
	 */
	public void setAutoPlay(boolean play);

	/**
	 * Returns the Window mode property of the Flash movie
	 * 
	 * @return the Window mode property of the Flash movie
	 */
	public String getWmode();

	/**
	 * Sets the Window Mode property of the Flash movie for transparency,
	 * layering, and positioning in the browser.
	 * 
	 * @param wmode
	 *            Possible values: window, opaque, transparent.
	 */
	public void setWode(String wmode);

	/**
	 * Gets the source path of Flash movie
	 * 
	 * @return the source path of Flash movie
	 */
	public String getSrc();

	/**
	 * Sets the source path of Flash movie and redraw the component
	 * 
	 * @param src
	 */
	public void setSrc(String src);

}
