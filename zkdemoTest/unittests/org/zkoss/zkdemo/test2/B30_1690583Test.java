/* B30_1690583Test.java

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

public class B30_1690583Test extends ZKTestCase {
	private Selenium browser;
	private String url;

	public B30_1690583Test() {
		super();
		// you can use your own browser if you want
		browser = getBrowser();
		url = getUrl();
	}

	public void test1() {
		String Input = "_test-t";
		String Button = "zk-comp-8!real";

		// Open The url of test case
		try {
			browser.open(url + "/B30-1690583.zul");
			// TODO The test progress
			browser.windowFocus();
			Thread.sleep(500);
			browser.focus(Button);
			browser.click(Button);
			Thread.sleep(1000);
			String a = browser.getAttribute(Input + "@class");
			System.out.print(browser.getAttribute(Input + "@offsetHeight"));
			assertEquals(a, "z-textbox");
			browser.focus(Button);
			browser.click(Button);
			Thread.sleep(1000);
			assertEquals(browser.getElementWidth(Input).toString(), "0");
			Thread.sleep(1000);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Browser Close
		browser.close();
	}
}
