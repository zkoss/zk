/* B70_ZK_2729Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 02 14:46:43 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
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
public class B70_ZK_2729Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();

		Path file = Paths.get("src/archive/test2/img/sun.jpg");
		dropUploadFile(jq("@dropupload"), file);
		waitResponse();

		jq("@div").scrollTop(100);
		waitResponse();

		dropUploadFile(jq("@dropupload"), file);
		waitResponse();

		int anchorOffsetTop = jq("@image").offsetTop();
		jq("@dropupload").eval("show()"); // Visible element has offsetTop
		int dropUploadOffsetTop = jq("@dropupload").offsetTop();
		Assert.assertEquals(anchorOffsetTop, dropUploadOffsetTop, 3);
	}
}
