/* F95_ZK_3269Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct 23 15:06:59 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F95_ZK_3269Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();

		final Path uploadFile = Paths.get("src/main/webapp/test2/img/sun.jpg");
		dropUploadFile(jq("@dropupload:eq(0)"), uploadFile);
		waitResponse();
		Assertions.assertFalse(hasError(),
				"The size-exceeded message should be suppressed");

		dropUploadFile(jq("@dropupload:eq(1)"), uploadFile);
		waitResponse();
		Assertions.assertTrue(hasError(),
				"The size-exceeded message shouldn't be suppressed");
	}
}
