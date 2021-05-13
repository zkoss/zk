/* Va02Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.validator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class Va02Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		String UNDER_AGE = "Under Age";
		String ADULT = "Adult";

		ComponentAgent ageBoxAgent = desktopAgent.query("#ageBox");
		Intbox ageBox = ageBoxAgent.as(Intbox.class);
		ComponentAgent addButton = desktopAgent.query("#addButton");
		Label adultLabel = desktopAgent.query("#adultLabel").as(Label.class);
		Label beforeAge = desktopAgent.query("#beforeAge").as(Label.class);
		Label ageLabel = desktopAgent.query("#ageLabel").as(Label.class);

		assertEquals(new Integer(-1), ageBox.getValue());
		addButton.click();
		assertEquals(UNDER_AGE, adultLabel.getValue());

		ageBoxAgent.input(1);
		addButton.click();
		assertEquals("1", beforeAge.getValue());
		assertEquals("11", ageLabel.getValue());
		assertEquals(UNDER_AGE, adultLabel.getValue());

		ageBoxAgent.input(18);
		addButton.click();
		assertEquals(ADULT, adultLabel.getValue());
	}
}
