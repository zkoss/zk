/* B96_ZK_4850Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 20 16:02:49 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_4850Test extends WebDriverTestCase {
	@Test
	public void testListbox() {
		test("/test2/B96-ZK-4850-listbox.zul");
	}

	@Test
	public void testGrid() {
		test("/test2/B96-ZK-4850-grid.zul");
	}

	@Test
	public void testTree() {
		test("/test2/B96-ZK-4850-tree.zul");
	}

	private void test(String zulPath) {
		connect(zulPath);
		waitResponse();

		click(jq(".z-paging-last"));
		waitResponse();

		Assert.assertEquals("should be able to navigate to the last page", "3", jq(".z-paging-input").val());
	}
}