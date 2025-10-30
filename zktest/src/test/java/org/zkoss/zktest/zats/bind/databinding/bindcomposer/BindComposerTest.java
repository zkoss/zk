/* BindComposerTest.java
	Purpose:

	Description:

	History:
		Tue May 04 15:42:43 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.bindcomposer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class BindComposerTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		//[Step 1]
		Label result1Label = desktopAgent.query("#win1 #result1").as(Label.class);
		assertEquals("4.0", result1Label.getValue());
		//[Step 2]
		desktopAgent.query("#win1 #btn2").click();
		assertEquals("doGlobalCommand called", desktopAgent.getZkLog().get(0));
		//[Step 3]
		assertTrue(desktopAgent.query("#win1 #result3").as(Label.class).getValue().startsWith("org.zkoss.zkmax.bind.impl.AnnotateBinderEx@"));
		//[Step 4]
		assertEquals("123", desktopAgent.query("vlayout #result4").as(Label.class).getValue());
		//[Step 5]
		assertEquals("00", desktopAgent.query("vlayout #result5").as(Label.class).getValue());
	}
}
