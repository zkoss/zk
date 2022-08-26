/* Va03Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class Va04Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		String ADULT = "Adult";

		ComponentAgent ageBoxAgent = desktopAgent.query("#ageBox");
		ComponentAgent addButton = desktopAgent.query("#addButton");
		Label afterAge = desktopAgent.query("#afterAge").as(Label.class);
		Label originalAge = desktopAgent.query("#originalAge").as(Label.class);
		Label adultLabel = desktopAgent.query("#adultLabel").as(Label.class);

		ageBoxAgent.input(1);
		addButton.click();
		assertEquals("1", originalAge.getValue());
		assertEquals("11", afterAge.getValue());

		ageBoxAgent.input(22);
		addButton.click();
		assertEquals(ADULT, adultLabel.getValue());

		ageBoxAgent.input(33);
		addButton.click();
		assertEquals(ADULT, adultLabel.getValue());
	}
}
