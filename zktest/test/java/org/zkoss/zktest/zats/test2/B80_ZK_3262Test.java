package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * Created by wenninghsu on 8/16/16.
 */
public class B80_ZK_3262Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		click(jq(".z-datebox-button"));
		waitResponse(true);
		Assert.assertTrue(jq("@calendar").exists());
		click(jq(".z-calendar-weekday").eq(20));
		waitResponse(true);
		Assert.assertFalse(jq("@errorbox").exists());
	}

}
