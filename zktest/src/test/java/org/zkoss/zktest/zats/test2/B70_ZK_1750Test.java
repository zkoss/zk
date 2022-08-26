/* B70_ZK_1750Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 27 14:19:15 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.zsoup.Zsoup;
import org.zkoss.zsoup.nodes.Document;
import org.zkoss.zsoup.nodes.Element;
import org.zkoss.zsoup.select.Elements;

/**
 * @author rudyhuang
 */
public class B70_ZK_1750Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		String url = getAddress() + getTestURL("B70-ZK-1750.zul");
		Document document = Zsoup.connect(url).get();
		Elements runOnces = document.select("script.z-runonce");
		for (Element runonce : runOnces) {
			String script = runonce.html();
			if (!script.contains("zkmx("))
				continue;
			assertThat(script, containsString(",cu:'\\x2F"));
			assertThat(script, containsString(",uu:'\\x2F"));
			assertThat(script, containsString(",ru:'\\x2F"));
		}
	}
}
