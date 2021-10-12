/* Z30_tree_0010Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 11:43:09 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class Z30_tree_0010Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		Assert.assertTrue(jq(".z-treecell:contains(Created by Composer)").exists());
	}
}
