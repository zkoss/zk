/* B96_ZK_4896Test.java

	Purpose:
		
	Description:
		
	History:
		Tue May 11 18:17:47 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.startsWith;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.ZatsException;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4896Test extends ZATSTestCase {
	@Test
	public void test() {
		Throwable t = Assert.assertThrows(ZatsException.class, this::connect);
		MatcherAssert.assertThat(t.getMessage(), startsWith("Unsupported parent for when"));
	}
}
