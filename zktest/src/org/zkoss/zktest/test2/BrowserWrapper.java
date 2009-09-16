/* BrowserWrapper.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 12:49:43 TST 2009, Created by sam

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.selenium.Selenium;
/**
 * 
 * @author sam
 *
 */
public class BrowserWrapper {
	List<Selenium> _browsers = new LinkedList<Selenium>();
	private String _testUrl;
	
	public BrowserWrapper(String url){
		_testUrl = url;
	}
	
	public void setUrl(String url){
		_testUrl = url;
	}
	
	public String getUrl(){
		return _testUrl;
	}
	
	public void addBrowser(Selenium browser){
		_browsers.add(browser);
	}
	
	public List<Selenium> getBrowsers(){
		return _browsers;
	}
}
