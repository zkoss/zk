/* ZKTestCase.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 12:49:43 TST 2009, Created by sam

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.thoughtworks.selenium.SeleneseTestCase;
import com.thoughtworks.selenium.Selenium;

/**
 * 
 * @author sam
 *
 */
public class ZKTestCase extends SeleneseTestCase {
	/**
	 * The prefix is depended on what the ID generator is.
	 */
	private static String PREFIX = "zk_comp_";
	private HashMap<String, BrowserWrapper> _map;

	public ZKTestCase() {
		try {
			_map = ConfigHelper.getServerWrapperMap();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected final static String uuid(int number) {
		return PREFIX + number;
	}
	
	protected List<Selenium> getBrowsers(String target) {
		return _map.get(target).getBrowsers();
	}

	protected String getUrl(String target) {
		return _map.get(target).getUrl();
	}
	
	protected CompPos getCompPos(Selenium browser, String compId){
		return new CompPos(browser, compId);
	}

	public void setUp() {
	}

	public void tearDown() {
	}
}
