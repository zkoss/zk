/* Va06Test.java

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
import org.zkoss.zul.Checkbox;

/**
 * @author jameschu
 */
public class Va06Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();

		ComponentAgent ageBoxAgent = desktopAgent.query("#ageBox");
		Checkbox checkBox = desktopAgent.query("#adultBox").as(Checkbox.class);
		ComponentAgent checkButton = desktopAgent.query("#checkButton");

		ageBoxAgent.input(-1);
		checkButton.click();
		assertFalse(checkBox.isChecked());

		ageBoxAgent.input(22);
		checkButton.click();
		assertTrue(checkBox.isChecked());

		ageBoxAgent.input(1);
		checkButton.click();
		assertFalse(checkBox.isChecked());
	}
}
