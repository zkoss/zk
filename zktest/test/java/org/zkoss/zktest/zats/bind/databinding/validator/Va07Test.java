/* Va07Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.validator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class Va07Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		ComponentAgent ageBoxAgent = desktopAgent.query("#ageBox");
		Label ageLabel = desktopAgent.query("#ageLabel").as(Label.class);

		ageBoxAgent.input(-1);
		assertEquals("0", ageLabel.getValue());

		ageBoxAgent.input(2);
		assertEquals("2", ageLabel.getValue());
	}
}
