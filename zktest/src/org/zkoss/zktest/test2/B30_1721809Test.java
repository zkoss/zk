/* B30_1721809Test.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 16, 2009 5:52:16 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;


import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.Selenium;

/**
 * @author jumperchen
 *
 */
public class B30_1721809Test extends ZKTestCase {

	private String _target = "B30-1721809.zul";
	private List<Selenium> _browsers;
	private String _url;
	
	/**
	 */
	@Override
	@Before
	public void setUp() {
		_browsers = getBrowsers(_target);
		_url = getUrl(_target);
	}
	
	@Test(expected = AssertionError.class)
	public void test1() {

		String comp1 = uuid(4);
		String comp2 = uuid(6);
		try {
			for (Selenium browser : _browsers) {
				try {
					browser.start();
					browser.open(_url);
					Thread.sleep(2000);

					browser.click(comp1);
					Thread.sleep(1000);
					
					assertEquals(browser.getText(comp2), "click");
					browser.click(comp1);
					Thread.sleep(1000);
					assertEquals(browser.getText(comp2), "click click");
				} finally {
					browser.stop();
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}

	}
}
