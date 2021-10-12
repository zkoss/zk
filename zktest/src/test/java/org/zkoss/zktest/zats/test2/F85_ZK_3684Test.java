/* F85_ZK_3684Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 09:43:56 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class F85_ZK_3684Test extends ZATSTestCase {
	private DesktopAgent desktop;

	@Test
	public void test() {
		desktop = connect();

		verify("", "[FirstName should be capitalized and only letters are accepted., FirstName cannot be empty.]");
		verify("123", "[FirstName should be capitalized and only letters are accepted.]");
		verify("Henry", "[]");
	}

	private void verify(String input, String text) {
		desktop.query("textbox").type(input);
		Assert.assertEquals(text, desktop.query("label").as(Label.class).getValue());
	}
}
