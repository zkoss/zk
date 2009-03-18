/* B30_1895856.java

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

import com.thoughtworks.selenium.Selenium;

public class B30_1895856 extends ZKTestCase {
	private Selenium browser;

	public void setUp() {
		browser = Server;
		browser.start();
	}
}
