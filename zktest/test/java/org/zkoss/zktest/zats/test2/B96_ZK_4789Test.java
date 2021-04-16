/* B96_ZK_4789Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Apr 16 12:37:04 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_4789Test extends WebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/B96-ZK-4789-zk.xml");

	@Test
	public void test() {
		connect();
		waitResponse();
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		driver.navigate().refresh(); // trigger rmDesktop
		sleep(1000);
		String println = outContent.toString().trim();
		Assert.assertTrue("You should see Client recev in server console", println.contains("Client recev"));
		Assert.assertTrue("You should see Client cmplt in server console", println.contains("Client cmplt"));
	}
}
