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

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;
import com.thoughtworks.selenium.Selenium;

/**
 * 
 * @author Flyworld
 */

public class ZKTestCase extends SeleneseTestCase {

	/* The Test Url */
	protected String url = "http://localhost:606/";
	
	/* The Test Browser */
	protected Selenium Server = new DefaultSelenium("localhost", 4444,
			"*firefox", "http://localhost:606/");

	public ZKTestCase() {

	}

}
