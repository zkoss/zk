/* B86_ZK_4025Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 13 15:33:03 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4025Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		// Yes, the same as B86_ZK_3996Test
		String errorMessage = jq("#zk_err .messagecontent").text();
		boolean hasFragmentLexicalError = errorMessage.contains("Failed to parse the content: Lexical error");
		Assert.assertFalse("Fragment has a lexical error", hasFragmentLexicalError);
	}
}
