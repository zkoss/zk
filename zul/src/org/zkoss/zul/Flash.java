/* Flash.java

 {{IS_NOTE
 Purpose: ZK Flash Component
 
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

package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.HtmlBasedComponent;

/**
 * @author Jeff
 */
public class Flash extends HtmlBasedComponent {


	private static final long serialVersionUID = 1L;

	private String _src ="";

	private boolean _autoPlay =true;
	
	private boolean _loop =true;
	
	private String _wmode = "transparent";
	
	private String _bgcolor ="";
	
	/**
	 * Gets the background color of Flash movie
	 * @return
	 */
	public String getBgcolor() {
		return _bgcolor;
	}
	
	/**
	 * Sets the background color of Flash movie
	 * @param bgcolor
	 */
	public void setBgcolor(String bgcolor) {
		if(Objects.equals(_bgcolor, bgcolor))
		{
		 _bgcolor = bgcolor;
		 smartUpdate("z:bgcolor",bgcolor);
		}
	}
	
	/**
	 * Sets whether the Flash movie plays repeatly
	 * @return
	 */
	public boolean isLoop() {
		return _loop;
	}
	
	/**
	 * Returns true if the Flash movie plays repeatly
	 * @param loop
	 */
	public void setLoop(boolean loop) {
		if(_loop != loop)
		{
			_loop = loop;
			smartUpdate("z:loop",loop);
		}
	}
	
	/**
	 * Return true if the Flash movie starts playing automatically
	 * @return
	 */
	public boolean isAutoPlay() {
		return _autoPlay;
	}
	
	/**
	 * Sets wether the song Flash movie playing automatically
	 * @param play
	 */
	public void setAutoPlay(boolean play){
		if(_autoPlay != play)
		{
			_autoPlay = play;
			smartUpdate("z:play",play);
		}
	}
	
	/**
	 * Returns the Window mode property of the Flash movie 
	 * @return
	 */
	public String getWmode() {
		return _wmode;
	}
	
	/**
	 * Sets the Window Mode property of the Flash movie 
	 * for transparency, layering, and positioning in the browser.
	 * @param wmode
	 */
	public void setWode(String wmode) {
		if(!Objects.equals(_wmode, wmode))
		{
			_wmode = wmode;
		}
	}

	/**
	 * Gets the source path of Flash movie
	 * @return
	 */
	public String getSrc() {
		return _src;
	}
	
	/**
	 * Sets the source path of Flash movie 
	 * and redraw the component
	 * @param src
	 */
	public void setSrc(String src) {
		if(!Objects.equals(_src,src))
		{
			_src = src;
			invalidate();
		}
		
	}
	
	/**
	 * Sets the height of Flash movie
	 */
	public void setHeight(String height) {
		if(!Objects.equals(super.getHeight(), height))
		{
			super.setHeight(height);
			smartUpdate("z:height",height);
		}
	}
	
	/**
	 * Sets the width of Flash movie
	 */
	public void setWidth(String width) {
		if(!Objects.equals(super.getWidth(), width))
		{
			super.setWidth(width);
			smartUpdate("z:width",width);
		}
		
	}
}
