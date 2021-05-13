/* Collection_index_treeTest.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 28 15:00:03 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.collection;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class Collection_index_treeTest extends WebDriverTestCase {
	@Test
	public void indexTest() {
		connect("/bind/databinding/collection/collection-index-tree.zul");

		int[] expectedIndex = {0, 1, 0, 1, 2, 2};
		String[] testString = {"A0", "B1", "B1-0", "B1-1", "B1-2", "C2"};

		click(jq(".z-tree-icon").eq(1));
		waitResponse();
		checkIndex(testString, expectedIndex);

		// Delete B1-1
		click(jq(".z-treerow").eq(3).find("@button:contains(Delete)"));
		waitResponse();
		int[] expectedIndex2 = {0, 1, 0, 1, 2};
		String[] testString2 = {"A0", "B1", "B1-0", "B1-2", "C2"};
		checkIndex(testString2, expectedIndex2);

		// Add before B1-0
		click(jq(".z-treerow").eq(2).find("@button:contains(Add Before)"));
		waitResponse();
		int[] expectedIndex3 = {0, 1, 0, 1, 2, 2};
		String[] testString3 = {"A0", "B1", "B1-0-before", "B1-0", "B1-2", "C2"};
		checkIndex(testString3, expectedIndex3);

		// Add after B1-0
		click(jq(".z-treerow").eq(3).find("@button:contains(Add After)"));
		waitResponse();
		int[] expectedIndex4 = {0, 1, 0, 1, 2, 3, 2};
		String[] testString4 = {"A0", "B1", "B1-0-before", "B1-0", "B1-0-after", "B1-2", "C2"};
		checkIndex(testString4, expectedIndex4);
	}

	private void checkIndex(String[] testString, int[] expectedIndex) {
		int size = expectedIndex.length;
		for (int i = 0; i < size; i++) {
			Assert.assertEquals(testString[i], jq(".z-treerow").eq(i).find(".z-treecell:eq(1)").text().trim());
			click(jq(".z-treerow").eq(i).find("@button:contains(Index)"));
			waitResponse();
			Assert.assertEquals("item index " + expectedIndex[i], jq("$msg").text());
		}
	}
}
