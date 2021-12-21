/* B96_ZK_5042Test.java

	Purpose:
		
	Description:
		
	History:
		10:18 AM 2021/11/16, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5042Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		//1. scroll down to the last node
		//2. click "expand" to open all tree nodes
		//3. scroll down to the middle node
		//4. click "collapse" to close all tree nodes
		//5. all nodes are close

		Assert.assertEquals(0, jq(".z-tree-line").length());

		int scrollHeight = Integer.parseInt(jq(".z-tree-body").toElement().get("scrollHeight"));
		jq(".z-tree-body").scrollTop(scrollHeight); // scroll to the end of the list
		waitResponse();

		click(jq("@button:contains(expand)"));
		waitResponse();

		scrollHeight = Integer.parseInt(jq(".z-tree-body").toElement().get("scrollHeight"));
		jq(".z-tree-body").scrollTop(scrollHeight/2); // scroll to the end of the list

		waitResponse();

		Assert.assertTrue(100 < jq(".z-tree-line").length());

		click(jq("@button:contains(collapse)"));
		waitResponse();
		Assert.assertEquals(0, jq(".z-tree-line").length());
	}

	@Test
	public void test2() {
		connect();

		//1. click "expand" to open all tree nodes
		//2. scroll down to the node 50
		//3. click "collapse" to close all tree nodes
		//4. all nodes are close

		Assert.assertEquals(0, jq(".z-tree-line").length());

		click(jq("@button:contains(expand)"));
		waitResponse();

		int scrollHeight = Integer.parseInt(jq(".z-tree-body").toElement().get("scrollHeight"));
		jq(".z-tree-body").scrollTop(scrollHeight/2);
		waitResponse();

		Assert.assertTrue(100 < jq(".z-tree-line").length());

		click(jq("@button:contains(collapse)"));
		waitResponse();
		Assert.assertEquals(0, jq(".z-tree-line").length());
	}

	@Test
	public void test3() {
		connect();

		//1. click "expand" to open all tree nodes
		//2. scroll down to the last node
		//3. click "collapse" to close all tree nodes
		//4. all nodes are close

		Assert.assertEquals(0, jq(".z-tree-line").length());

		click(jq("@button:contains(expand)"));
		waitResponse();

		int scrollHeight = Integer.parseInt(jq(".z-tree-body").toElement().get("scrollHeight"));
		jq(".z-tree-body").scrollTop(scrollHeight); // scroll to the end of the list
		waitResponse();

		Assert.assertTrue(100 < jq(".z-tree-line").length());

		click(jq("@button:contains(collapse)"));
		waitResponse();
		Assert.assertEquals(0, jq(".z-tree-line").length());
	}

	@Test
	public void test4() {
		connect();

		//1. click "expand" to open all tree nodes
		//2. scroll down to the last node
		//3. click "collapse" to close all tree nodes
		//4. all nodes are close
		//5. test again from No1 ~ No4

		Assert.assertEquals(0, jq(".z-tree-line").length());

		click(jq("@button:contains(expand)"));
		waitResponse();

		int scrollHeight = Integer.parseInt(jq(".z-tree-body").toElement().get("scrollHeight"));
		jq(".z-tree-body").scrollTop(scrollHeight); // scroll to the end of the list
		waitResponse();

		Assert.assertTrue(100 < jq(".z-tree-line").length());

		click(jq("@button:contains(collapse)"));
		waitResponse();
		Assert.assertEquals(0, jq(".z-tree-line").length());

		click(jq("@button:contains(expand)"));
		waitResponse();

		scrollHeight = Integer.parseInt(jq(".z-tree-body").toElement().get("scrollHeight"));
		jq(".z-tree-body").scrollTop(scrollHeight); // scroll to the end of the list
		waitResponse();

		Assert.assertEquals(100, jq(".z-tree-line").length());

		click(jq("@button:contains(collapse)"));
		waitResponse();
		Assert.assertEquals(0, jq(".z-tree-line").length());
	}
}
