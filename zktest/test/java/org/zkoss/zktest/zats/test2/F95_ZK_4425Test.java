/* F95_ZK_4425Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 1 17:59:06 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class F95_ZK_4425Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		getActions().moveToElement(driver.findElement(jq(".z-rangeslider-track")))
				.moveByOffset(15, 0)
				.clickAndHold()
				.moveByOffset(150, 0)
				.release()
				.perform();
		waitResponse();
		Assert.assertEquals(jq(".z-rangeslider-track").width(), jq("@rangeslider .z-sliderbuttons-button").last().positionLeft(), 1);
		getActions().moveToElement(driver.findElement(jq(".z-multislider-track")))
				.moveByOffset(15, 0)
				.clickAndHold()
				.moveByOffset(-150, 0)
				.release()
				.perform();
		waitResponse();
		Assert.assertEquals(0, jq("@multislider .z-sliderbuttons-button").first().positionLeft());
	}
}
