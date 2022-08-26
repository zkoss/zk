/* B86_ZK_4244Test.java

	Purpose:
		
	Description:
		
	History:
		Thu May 16 14:52:13 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4244Test extends WebDriverTestCase {
	@Test
	public void test() throws FileNotFoundException {
		connect();

		dropUploadFile(jq("@dropupload"), Paths.get("src/main/webapp/test2/img/sun.jpg"));
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
	}
}
