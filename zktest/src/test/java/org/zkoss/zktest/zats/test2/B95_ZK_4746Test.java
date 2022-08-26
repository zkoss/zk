/* B95_ZK_4746Test.java

		Purpose:

		Description:

		History:
				Mon Dec 28 15:08:54 CST 2020, Created by katherinelin

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zk.ui.ext.Disable;
import org.zkoss.zul.Selectbox;

public class B95_ZK_4746Test {
	@Test
	public void Test() {
		Selectbox selectbox = new Selectbox();
		Assertions.assertTrue(selectbox instanceof Disable);
	}
}
