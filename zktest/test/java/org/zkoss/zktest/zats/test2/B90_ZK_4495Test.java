/* B90_ZK_4441Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Feb 10 14:33:20 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author jameschu
 */
public class B90_ZK_4495Test extends WebDriverTestCase {
	@Override
	protected String getFileExtension() {
		return ".html";
	}
	@Test
	public void test() {
		WebDriver wd = connect();
		wd.findElement(By.id("btn")).click();
		sleep(3000);
		assertEquals("aaa", wd.findElement(By.className("z-label")).getText());
	}
}
