/* ComponentParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 14:55:04 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class ComponentParamTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		desktop.query("button").click();
		desktop.query("#div").click();
		Assertions.assertArrayEquals(
			new String[] {
				"zul.wgt.Button",
				"zul.wgt.Div"
			},
			desktop.getZkLog().toArray()
		);
	}
}
