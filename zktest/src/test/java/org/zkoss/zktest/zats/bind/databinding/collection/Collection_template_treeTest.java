/* Collection_template_treeTest.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 28 17:09:27 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class Collection_template_treeTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/bind/databinding/collection/collection-template-tree.zul");
		int length = jq(".z-treerow").length();
		for (int i = 0; i < length; i++) {
			JQuery treerow = jq(".z-treerow").eq(i);
			String indexString = treerow.find(".z-treecell-content:eq(0)>.z-treecell-text").text();
			String name = treerow.find(".z-treecell-content:eq(1)").text();
			String template = treerow.find(".z-treecell-content:eq(3)").text();
			Assertions.assertEquals((name.startsWith("A") || "1".equals(indexString)) ? "Model1" : "Model2" , template);
		}
	}
}
