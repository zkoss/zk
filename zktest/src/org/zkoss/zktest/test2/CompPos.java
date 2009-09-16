/* CompPos.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2009/9/16 ¤U¤È4:13:46 , Created by sam
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;

import com.thoughtworks.selenium.Selenium;

/**
 * @author sam
 *
 */
public class CompPos {
	private int _left;
	private int _top;
	private int _right;
	private int _bottom;
	private int _width;
	private int _height;
	
	public CompPos(Selenium browser, String compId){
		_left = browser.getElementPositionLeft(compId).intValue();
		_top = browser.getElementPositionTop(compId).intValue();
		_width = browser.getElementWidth(compId).intValue();
		_height = browser.getElementHeight(compId).intValue();
		
		_right = _left + _width;
		_bottom = _top + _height;
	}
	
	public int getLeft(){ return  _left;}
	public int getTop(){ return  _top;}
	public int getRight(){ return  _right;}
	public int getBottom(){ return  _bottom;}
	
	public int getWidth(){ return _width; }
	public int getHeight(){ return  _height;}

	
}
