/* Va11Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class Va11Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();

		ComponentAgent ageBoxAgent = desktopAgent.query("#ageBox");
		ComponentAgent submitButton = desktopAgent.query("#submitButton");
		Label less13 = desktopAgent.query("#less13").as(Label.class);
		Label less18 = desktopAgent.query("#less18").as(Label.class);
		Label over18 = desktopAgent.query("#over18").as(Label.class);

		ageBoxAgent.input(-1);
		submitButton.click();
		assertTrue(less13.isVisible());
		assertTrue(less18.isVisible());
		assertFalse(over18.isVisible());

		ageBoxAgent.input(1);
		submitButton.click();
		assertTrue(less13.isVisible());
		assertTrue(less18.isVisible());
		assertFalse(over18.isVisible());

		ageBoxAgent.input(15);
		submitButton.click();
		assertFalse(less13.isVisible());
		assertTrue(less18.isVisible());
		assertFalse(over18.isVisible());

		ageBoxAgent.input(18);
		submitButton.click();
		assertFalse(less13.isVisible());
		assertFalse(less18.isVisible());
		assertTrue(over18.isVisible());
	}
}
