/* ConfigHelper.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 12:49:43 TST 2009, Created by sam

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * 
 * @author sam
 *
 */
public class ConfigHelper {
	
	private static String _host;
	private static int _port;
	private static String _browserURL;
	private static String _interURL;
	private static Properties _prop;

	/**
	 * key : Firefox, IE ...
	 * value : *firefox, *iexplore ...
	 */
	private static HashMap<String, String> _browserNameMap;

	/**
	 * key : Firefox, IE ...
	 * value : Selenium browser
	 */
	private static HashMap<String, Selenium> _browserHolder=new HashMap<String, Selenium>();

	/**
	 * key : test target ("B30-XXXXXX.zul")
	 * value : ServerWrapper
	 */
	private static HashMap<String, BrowserWrapper> _browserTester=new HashMap<String, BrowserWrapper>();
	
	
	public static HashMap<String, BrowserWrapper> getServerWrapperMap() throws Exception, IOException{
		initIfNeed();
		return _browserTester;
	}
	private static HashMap<String, BrowserWrapper> initServerWrapperByTarget(String target) throws FileNotFoundException, IOException{
		String testBrowsers = getTestBrowsersFromConfig(target);
		
		Iterator<String> testBrowserType = _browserNameMap.keySet().iterator();
		for(;testBrowserType.hasNext();){
			String strBrowser = testBrowserType.next();
			if(testBrowsers.contains(strBrowser)){
				BrowserWrapper wrapper = getServerWrapper(target);
				wrapper.addBrowser(getBrowserFromHolder(strBrowser));
			}
		}
		return _browserTester;
	}
	/**
	 * 
	 * @param key : target , example : "B30-XXXXXX.zul"
	 * @return value : ServerWrapper obj
	 */
	private static BrowserWrapper getServerWrapper(String target){
		BrowserWrapper wrapper = _browserTester.get(target);
		if(wrapper == null){
			wrapper = new BrowserWrapper(getTestUrl(target));
			_browserTester.put(target, wrapper);
		}
		return wrapper;
	}
	private static String getTestUrl(String target){
		return _browserURL + _interURL + target;
	}
	
	/**
	 * 
	 * @param key 
	 * @return value : 
	 */
	private static Selenium getBrowserFromHolder(String key){
		Selenium browser = _browserHolder.get(key);
		if(browser == null){
			browser = new DefaultSelenium(_host, _port, _browserNameMap.get(key), _browserURL);
			_browserHolder.put(key, browser);
		}
		return browser;
	}
	
	private static String getTestBrowsersFromConfig(String target) throws FileNotFoundException, IOException{
		return _prop.getProperty(target);
	}
	
	private static void initIfNeed() throws IOException, Exception{
		if(_browserNameMap == null){
			_browserNameMap = new HashMap<String, String>();
			
			_browserNameMap.put("Firefox", "*firefox");
			_browserNameMap.put("IE", "*iexplore");
		}
		initProperty();
	}
	
	@SuppressWarnings("unchecked")
	private static void initProperty() throws Exception, IOException{
		InputStream in = null;
		if(_prop == null){
			try{
				in = new FileInputStream("config.properties");
				_prop = new Properties();
				_prop.load(in);
					
				_host = _prop.getProperty("serverHost");
				_port = Integer.parseInt(_prop.getProperty("serverPort"));
				_browserURL = _prop.getProperty("browserURL");
				_interURL = _prop.getProperty("interURL");
				
				
				for(Iterator iter = _prop.entrySet().iterator();iter.hasNext() ; ){
					final Map.Entry setting = (Map.Entry) iter.next();
					String strKey = (String)setting.getKey();
					if(strKey.contains(".")){
						initServerWrapperByTarget(strKey);
					}
				}
			}finally{
				if(in != null){
					in.close();
				}
			}
		}
	}
}
