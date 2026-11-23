/* F96_ZK_4779Test.java

		Purpose:
		
		Description:
		
		History:
				Thu May 27 14:07:01 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class F96_ZK_4779Test extends WebDriverTestCase {
	@Test
	public void playAndEndTest() {
		connect();
		click(jq("$play"));
		waitResponse();
		Assert.assertEquals("1", getZKLog());
		checkIsState("$isPlaying");
		sleep(5000); // wait until audio end
		Assert.assertEquals("2\n3", getZKLog());
		checkIsState("$isEnded");
		checkIsState("$isStopped"); // end also mean stopped
	}
	
	@Test
	public void pauseAndStopTest() {
		connect();
		click(jq("$play"));
		waitResponse();
		sleep(1000);
		click(jq("$pause")); // pause before music end
		waitResponse();
		Assert.assertEquals("1\n2", getZKLog());
		checkIsState("$isPaused");
		click(jq("$stop"));
		waitResponse();
		Assert.assertEquals("0", getZKLog());
		checkIsState("$isStopped");
	}
	
	private void checkIsState(String btnId) {
		closeZKLog();
		click(jq(btnId));
		waitResponse();
		Assert.assertEquals("true", getZKLog());
		closeZKLog();
	}
}