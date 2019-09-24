/* B86_ZK_4353Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Aug 08 10:17:04 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.lessThanOrEqualTo;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B86_ZK_4353Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery themesLinks = jq(".z-a");
		for (int i = 0; i < themesLinks.length(); i++) {
			click(themesLinks.eq(i));
			waitResponse();
			checkNavImageSize();
		}
	}

	private void checkNavImageSize() {
		JQuery navImage = jq(".z-nav-image");
		Assert.assertThat(navImage.height(), lessThanOrEqualTo(50));
		Assert.assertThat(navImage.width(), lessThanOrEqualTo(50));
	}
}
