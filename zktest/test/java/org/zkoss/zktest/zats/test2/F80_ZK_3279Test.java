/* F80_ZK_3255Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 18 15:44:23 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class F80_ZK_3279Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button:eq(0)"));
		waitResponse();
		assertFalse(jq("$l1").isVisible());
		click(jq("@button:eq(1)"));
		waitResponse();
		assertTrue(jq("$l1").isVisible());
		click(jq("@button:eq(2)"));
		waitResponse();
		assertFalse(jq("$p1").html().contains("id1") && jq("$p1").html().contains("z-transparent "));
		click(jq("@button:eq(3)"));
		waitResponse();
		String content = jq("$p2").html().replaceAll("\\n", "").replaceAll("\\r", "");
		assertTrue(content.matches(".*<\\!\\-\\-z\\-transparent .*start.*id2.*end\\-\\->.*"));
		click(jq("@button:eq(4)"));
		waitResponse();
		content = jq("$p3").html().replaceAll("\\n", "").replaceAll("\\r", "");
		assertTrue(content.matches(".*3rd.*<\\!\\-\\-z\\-transparent .*start.*id2.*end\\-\\->.*"));

		click(jq("@button:eq(5)"));
		waitResponse();
		content = jq("$p3").html().replaceAll("\\n", "").replaceAll("\\r", "");
		assertTrue(content.matches(".*3rd.*new1.*<\\!\\-\\-z\\-transparent .*start.*id2.*end\\-\\->.*"));

		click(jq("@button:eq(6)"));
		waitResponse();
		content = jq("$p3").html().replaceAll("\\n", "").replaceAll("\\r", "");
		assertTrue(content.matches(".*3rd.*new1.*<\\!\\-\\-z\\-transparent .*start.*id2.*end\\-\\->.*new2.*"));

		click(jq("@button:eq(7)"));
		waitResponse();
		content = jq("$p3").html().replaceAll("\\n", "").replaceAll("\\r", "");
		assertTrue(content.matches(".*3rd.*new1.*<\\!\\-\\-z\\-transparent .*start.*new3.*id2.*end\\-\\->.*new2.*"));

		click(jq("@button:eq(8)"));
		waitResponse();
		content = jq("$p3").html().replaceAll("\\n", "").replaceAll("\\r", "");
		assertTrue(content.matches(".*3rd.*new1.*<\\!\\-\\-z\\-transparent .*start.*new3.*id2.*new4.*end\\-\\->.*new2.*"));
	}
}
