/* ZKTestSuite.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Mar 12 12:36:59 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkdemo.test2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;
import com.thoughtworks.selenium.Selenium;

/**
 * 
 * @author Flyworld
 */

public class ZKTestCase extends SeleneseTestCase {

	final static String CONFIG = "test.properties";
	private Properties _prop;
	private Map _map = new HashMap();
	private String _url = null;
	private Selenium _server = null;

	public ZKTestCase() {
		try {
			_prop = new Properties();
			_prop.load(new FileInputStream(new File(CONFIG)));
			for (Iterator it = _prop.entrySet().iterator(); it.hasNext();) {
				final Map.Entry setup = (Map.Entry) it.next();
				_map.put(setup.getKey(), setup.getValue());
			}
			/*
			 * System.out.println(_map.get("seleniumUrl").toString());
			 * System.out.println(_map.get("seleniumPort").toString());
			 * System.out.println(_map.get("targetBrowser").toString());
			 * System.out.println(_map.get("browserUrl").toString());
			 */
			_url = _map.get("browserUrl").toString();
			_server = new DefaultSelenium(_map.get("seleniumUrl").toString(),
					Integer.parseInt(_map.get("seleniumPort").toString()), _map
							.get("targetBrowser").toString(), _map.get(
							"browserUrl").toString());
		} catch (IOException e) {
			System.out.println("failed to load _properties file, \nCause: "
					+ e.getStackTrace());
		} catch (NullPointerException e) {
			System.out.println("Setup error :" + e.getStackTrace());
		}
	}

	public void setUp() {
		getServer().start();
	}

	public void tearDown() {
		getServer().stop();
	}

	protected Selenium getServer() {
		return _server;
	}

	protected String getUrl() {
		return _url;

	}

}
