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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B96_ZK_4789Test extends WebDriverTestCase {
	@RegisterExtension
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
		Assertions.assertTrue(println.contains("Client recev"), "You should see Client recev in server console");
		Assertions.assertTrue(println.contains("Client cmplt"), "You should see Client cmplt in server console");
	}
}
