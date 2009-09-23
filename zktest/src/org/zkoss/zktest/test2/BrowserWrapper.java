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
	
	private List<Selenium> _browsers = new LinkedList<Selenium>();
	private List<String> _browserTypes = new LinkedList<String>();
	
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
	
	public void addBrowser(String browserType, Selenium browser){
		if(browserType == null || browser == null)
			throw new NullPointerException("BrowserWrapper addBrowser : Browser is NULL");
		if(!_browsers.contains(browser))
			_browsers.add(browser);
		if(!_browserTypes.contains(browserType))
			_browserTypes.add(browserType);
	}
	
	public List<Selenium> getBrowsers(){
		if(_browsers == null || _browsers.size() == 0)
			throw new NullPointerException("Selenium list Null, please check config property file for testing browser");
		
		return _browsers;
	}
	
	public List<String> getBrowserTypes(){
		if(_browserTypes == null || _browserTypes.size() == 0)
			throw new NullPointerException("Selenium list Null, please check config property file for testing browser");
			
		return _browserTypes;
	}
}
