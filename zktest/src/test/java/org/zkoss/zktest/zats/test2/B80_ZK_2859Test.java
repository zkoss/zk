/* B80_ZK_2859Test.java

	Purpose:
		
	Description:
		
	History:
		Tue, Jan 19, 2016  3:30:28 PM, Created by Christopher

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * 
 * @author Christopher
 */
public class B80_ZK_2859Test extends WebDriverTestCase {
	
	@Test
	public void testExpandClose() {
		// open and close the tree should not cause an exception
		connect();
		click(jq(".z-tree-icon"));
		waitResponse(true);
		click(jq(".z-tree-icon"));
		waitResponse(true);
		Assert.assertFalse("Not expecting to see exception here", jq(".z-messagebox").exists());
	}
	
	@Test
	public void testPageChange() {
		// open the tree to trigger paging, switch to next page and back should not cause an exception
		connect();
		click(jq(".z-tree-icon"));
		waitResponse(true);
		click(jq(".z-paging-button.z-paging-next"));
		waitResponse(true);
		// go to previous page
		click(jq(".z-paging-button.z-paging-previous"));
		waitResponse(true);
		Assert.assertFalse("Not expecting to see exception here", jq(".z-messagebox").exists());
	}
}
