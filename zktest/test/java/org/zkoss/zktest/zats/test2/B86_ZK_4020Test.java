/* B86_ZK_4020Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Aug 10 10:17:34 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4020Test extends WebDriverTestCase {

	private String result1 = "render Row number 1\n" +
			"render Row number 2\n" +
			"render Row number 3\n" +
			"render Row number 4\n" +
			"render Row number 5\n" +
			"render Row number 6\n" +
			"render Row number 7\n" +
			"render Row number 8\n" +
			"render Row number 9\n" +
			"render Row number 10\n" +
			"render Row number 11\n" +
			"render Row number 12\n" +
			"render Row number 13\n" +
			"render Row number 14\n" +
			"render Row number 15\n" +
			"render Row number 16\n" +
			"render Row number 17\n" +
			"render Row number 18\n" +
			"render Row number 19\n" +
			"render Row number 20\n" +
			"render Row number 21\n" +
			"render Row number 22\n" +
			"render Row number 23\n" +
			"render Row number 24\n" +
			"render Row number 25\n" +
			"render Row number 26\n" +
			"render Row number 27\n" +
			"render Row number 28\n" +
			"render Row number 29\n" +
			"render Row number 30\n" +
			"render Row number 31\n" +
			"render Row number 32\n" +
			"render Row number 33\n" +
			"render Row number 34\n" +
			"render Row number 35\n" +
			"render Row number 36\n" +
			"render Row number 37\n" +
			"render Row number 38\n" +
			"render Row number 39\n" +
			"render Row number 40\n" +
			"render Row number 41\n" +
			"render Row number 42\n" +
			"render Row number 43\n" +
			"render Row number 44\n" +
			"render Row number 45\n" +
			"render Row number 46\n" +
			"render Row number 47\n" +
			"render Row number 48\n" +
			"render Row number 49\n" +
			"render Row number 50";

	private String result2 = "render listitem number 1\n" +
			"render listitem number 2\n" +
			"render listitem number 3\n" +
			"render listitem number 4\n" +
			"render listitem number 5\n" +
			"render listitem number 6\n" +
			"render listitem number 7\n" +
			"render listitem number 8\n" +
			"render listitem number 9\n" +
			"render listitem number 10\n" +
			"render listitem number 11\n" +
			"render listitem number 12\n" +
			"render listitem number 13\n" +
			"render listitem number 14\n" +
			"render listitem number 15\n" +
			"render listitem number 16\n" +
			"render listitem number 17\n" +
			"render listitem number 18\n" +
			"render listitem number 19\n" +
			"render listitem number 20\n" +
			"render listitem number 21\n" +
			"render listitem number 22\n" +
			"render listitem number 23\n" +
			"render listitem number 24\n" +
			"render listitem number 25\n" +
			"render listitem number 26\n" +
			"render listitem number 27\n" +
			"render listitem number 28\n" +
			"render listitem number 29\n" +
			"render listitem number 30\n" +
			"render listitem number 31\n" +
			"render listitem number 32\n" +
			"render listitem number 33\n" +
			"render listitem number 34\n" +
			"render listitem number 35\n" +
			"render listitem number 36\n" +
			"render listitem number 37\n" +
			"render listitem number 38\n" +
			"render listitem number 39\n" +
			"render listitem number 40\n" +
			"render listitem number 41\n" +
			"render listitem number 42\n" +
			"render listitem number 43\n" +
			"render listitem number 44\n" +
			"render listitem number 45\n" +
			"render listitem number 46\n" +
			"render listitem number 47\n" +
			"render listitem number 48\n" +
			"render listitem number 49\n" +
			"render listitem number 50";
	

	@Test
	public void test() {
		connect();
		waitResponse();
		jq("#zk_log").toElement().set("value","");
		click(jq("$rowFilter"));
		waitResponse();
		Assert.assertEquals(result1.trim(), jq("#zk_log").toElement().get("value").trim());
		jq("#zk_log").toElement().set("value","");
		click(jq("$itemFilter"));
		waitResponse();
		Assert.assertEquals(result2.trim(), jq("#zk_log").toElement().get("value").trim());
	}

}
