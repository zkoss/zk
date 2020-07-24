/* ColorboxTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 23 09:45:01 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jameschu
 */
@Ignore("Needs aXe >= 3.5.6, https://github.com/dequelabs/axe-core/pull/2304")
public class ColorboxTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
		click(jq("@colorbox"));
		verifyA11y();
		click(jq(".z-colorbox-pickericon"));
		verifyA11y();
	}
}
