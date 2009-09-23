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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
	
	private final static String[] BROWSER_NAMES = new String[]{"firefox", "chrome", "safari", "opera", "iexplore"}; 
	private final static String[] PORTABLE_BROWSERS = new String[]{"firefox"};
	private final static String ALL_BROWSERS = "All";
	private static List<String> _allBrowsers = new LinkedList<String>();
	private static String _host;
	private static int _port;
	private static String _browserURL;
	private static String _interURL;
	private static String _delaySpeed;
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
	 * value : BrowserWrapper
	 */
	private static HashMap<String, BrowserWrapper> _browserWrapper=new HashMap<String, BrowserWrapper>();
	
	
	public static String getHost(){
		return _host;
	}
	
	public static int getPort(){
		return _port;
	}
	public static String getOpenUrl(){
		return _browserURL;
	}
	public static String getDelaySpeed(){
		return _delaySpeed;
	}
	
	
	public static HashMap<String, BrowserWrapper> getServerWrapperMap() throws Exception, IOException{
		initIfNeed();
		return _browserWrapper;
	}
	private static HashMap<String, BrowserWrapper> initServerWrapperByTarget(String target) throws FileNotFoundException, IOException{
		String[] testBrowsers = getTestBrowsersFromConfig(target).split(",");
		
		for(String browser : testBrowsers){
			String strBrowserKey = browser.trim();
			if(ALL_BROWSERS.equals(strBrowserKey)){
				addAllBrowsers(target);
				continue;
			}
			
			String strBrowser = _browserNameMap.get(strBrowserKey);
			if(strBrowser != null){
				BrowserWrapper wrapper = getServerWrapper(target);
				wrapper.addBrowser( _browserNameMap.get(strBrowserKey), getBrowserFromHolder(strBrowserKey));
			}		
		}
		return _browserWrapper;
	}
	private static void addAllBrowsers(String target){
		BrowserWrapper wrapper = getServerWrapper(target);
		for(String browserKey : _allBrowsers){
			wrapper.addBrowser(_browserNameMap.get(browserKey), getBrowserFromHolder(browserKey));
		}
	}
	/**
	 * 
	 * @param key : target , example : "B30-XXXXXX.zul"
	 * @return value : BrowserWrapper obj
	 */
	private static BrowserWrapper getServerWrapper(String target){
		BrowserWrapper wrapper = _browserWrapper.get(target);
		if(wrapper == null){
			wrapper = new BrowserWrapper(getTestUrl(target));
			_browserWrapper.put(target, wrapper);
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
		if(_browserNameMap.get(key) == null)
			throw new NullPointerException("Null Browser Type String");
		
		Selenium browser = _browserHolder.get(key);
		if(browser == null){
			browser = new DefaultSelenium(_host, _port, _browserNameMap.get(key), _browserURL);
			browser.setSpeed(getDelaySpeed());
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
	
	private static void addBrowserNameSetting(String browserName, String browserPath){
		
		String browser = null;
		for(String portable : PORTABLE_BROWSERS){
			if(browserName.toLowerCase().startsWith(portable)){
				browser = portable;
				break;
			}
		}
		if(browser == null)
			return;
		
		String setting = "*" + browser + " " + browserPath;
		_browserNameMap.put(browserName, setting);
	}
	
	@SuppressWarnings("unchecked")
	private static void initProperty() throws Exception, IOException{
		InputStream in = null;
		if(_prop == null){
			try{
				in = ClassLoader.getSystemResourceAsStream("config.properties");
				_prop = new Properties();
				_prop.load(in);
					
				_host = _prop.getProperty("serverHost");
				_port = Integer.parseInt(_prop.getProperty("serverPort"));
				_browserURL = _prop.getProperty("browserURL");
				_interURL = _prop.getProperty("interURL");
				_delaySpeed = _prop.getProperty("delaySpeed");
				
				
				for(Iterator iter = _prop.entrySet().iterator();iter.hasNext() ; ){
					final Map.Entry setting = (Map.Entry) iter.next();
					String strKey = (String)setting.getKey();
					if(isBrowserSetting(strKey)){
						addBrowserNameSetting(strKey, (String)setting.getValue());
						continue;
					}
				}
				
				String[] allBrowsers = _prop.getProperty(ALL_BROWSERS).split(",");
				for(String browser : allBrowsers){
					String browserKey = browser.trim();
					if(_browserNameMap.containsKey(browserKey)){
						_allBrowsers.add(browserKey);
					}
				}
				
				for(Iterator iter = _prop.entrySet().iterator();iter.hasNext() ; ){
					final Map.Entry setting = (Map.Entry) iter.next();
					String strKey = (String)setting.getKey();

					if(isTestcaseSetting(strKey)){
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
	
	private static boolean isBrowserSetting(String str){
		for(String browserStr : BROWSER_NAMES){
			if(str.toLowerCase().startsWith(browserStr))
				return true;
		}
		return false;
	}
	
	private static boolean isTestcaseSetting(String str){	
		if(isBrowserSetting(str))
			return false;
		
		if(str.contains("."))
			return true;
		
		return false;
	}
}
