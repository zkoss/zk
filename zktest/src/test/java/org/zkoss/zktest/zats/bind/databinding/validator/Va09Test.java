/* Va09Test.java

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
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class Va09Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		ComponentAgent keywordBoxAgent = desktopAgent.query("#keywordBox");
		Label keywordLabel = desktopAgent.query("#keywordLabel").as(Label.class);
		ComponentAgent lengthBoxAgent = desktopAgent.query("#lengthBox");

		keywordBoxAgent.input("123");
		assertEquals("123", keywordLabel.getValue());

		keywordBoxAgent.input("1234");
		assertEquals("123", keywordLabel.getValue());

		lengthBoxAgent.input(5);
		keywordBoxAgent.input("12345");
		assertEquals("12345", keywordLabel.getValue());
	}
}
