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

public class B30_1895856Test extends ZKTestCase {
	private Selenium browser;
	private String url;

	public B30_1895856Test() {
		super();
		// you can use your own browser if you want
		browser = getBrowser();
		url = getUrl();
	}

	public void test1() {
		String Input1 = "zk-comp-7";
		String Label1 = "zk-comp-13";
		String Button = "_test-btn!real";

		// Open The url of test case
		try {
			browser.open(url + "/B30-1895856.zul");

			// TODO The test progress
			browser.windowFocus();
			Thread.sleep(1000);
			browser.focus(Input1);
			browser.type(Input1, "Hi");
			Thread.sleep(1000);
			browser.focus(Button);
			browser.click(Button);
			Thread.sleep(1500);
			assertEquals("Hi ZK", browser.getText(Label1).trim());

			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Browser Close
		browser.close();
	}
}
