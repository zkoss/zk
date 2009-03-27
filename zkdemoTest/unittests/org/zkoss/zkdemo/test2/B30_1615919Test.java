/* B30_1615919Test.java

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

public class B30_1615919Test extends ZKTestCase {
	private Selenium browser;
	private String url;

	public B30_1615919Test() {
		super();
		// you can use your own browser if you want
		browser = getBrowser();
		url = getUrl();
	}

	public void test1() {
		String windowtitle = "zk-comp-4!caption";
		String ButtonCreate = "zk-comp-2!real";

		// Open The url of test case
		try {
			browser.open(url + "/B30-1615919.zul");
			// TODO The test progress
			browser.windowFocus();
			Thread.sleep(1000);
			browser.focus(ButtonCreate);
			browser.click(ButtonCreate);
			Thread.sleep(1000);
			browser.setSpeed("100");			
			int left = browser.getElementPositionLeft(windowtitle).intValue();
			int top = browser.getElementPositionTop(windowtitle).intValue();
			for (int i = 0; i < 10; i++) {
				browser.dragAndDrop(windowtitle, "-10,30");
			}
			int left2 = browser.getElementPositionLeft(windowtitle).intValue();
			int top2 = browser.getElementPositionTop(windowtitle).intValue();

			Thread.sleep(2000);
			if (top != (top2 - 300) || left != (left2 + 100)) {
				fail("Drag Failed, top/left : " + top + "/" + left
						+ " top2/left2 : " + top2 + "/" + left2);
			}

			Thread.sleep(2000);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Browser Close
		browser.close();
	}
}
