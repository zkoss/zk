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

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F95_ZK_3269Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();

		final Path uploadFile = Paths.get("src/archive/test2/img/sun.jpg");
		dropUploadFile(jq("@dropupload:eq(0)"), uploadFile);
		waitResponse();
		Assert.assertFalse("The size-exceeded message should be suppressed", hasError());

		dropUploadFile(jq("@dropupload:eq(1)"), uploadFile);
		waitResponse();
		Assert.assertTrue("The size-exceeded message shouldn't be suppressed", hasError());
	}
}
