/* B96_ZK_5181Test.java

	Purpose:
		
	Description:
		
	History:
		2:21 PM 2022/10/5, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;


import static org.hamcrest.Matchers.lessThan;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5181Test extends WebDriverTestCase {
	@Test
	public void testPerformance() {
		connect();
		waitResponse();
		String[] split = getZKLog().split("/n");
		String[] strings = split[split.length - 1].split(" ");
		long time = Long.parseLong(strings[strings.length - 1]);
		Assert.assertThat(time, lessThan(1000000L));
	}
}
