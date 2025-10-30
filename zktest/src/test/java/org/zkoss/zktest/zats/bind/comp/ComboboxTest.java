/* ComboboxTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 10:47:14 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.comp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.OpenAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class ComboboxTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		final OpenAgent combobox = desktop.query("combobox").as(OpenAgent.class);
		final Label open = desktop.query("#open").as(Label.class);
		combobox.open(true);
		Assertions.assertEquals("true", open.getValue());

		combobox.open(false);
		Assertions.assertEquals("false", open.getValue());
	}
}
