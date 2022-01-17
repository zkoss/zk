/* F86_ZK_4046Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 18 15:28:16 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F86_ZK_4046Test extends WebDriverTestCase {
	private Path lbl = Paths.get("./src/main/webapp/WEB-INF/zk-label.properties");
	private Path lblBak = Paths.get("./src/main/webapp/WEB-INF/zk-label.properties.bak");

	@Test
	public void test() throws Exception {
		connect();
		try {
			test0();
		} finally {
			cleanup();
		}
	}

	private void test0() throws IOException {
		click(jq("@button:contains(Disable)"));
		waitResponse();

		String expected = "A message from zktest/src/main/webapp/WEB-INF/zk-label.properties";
		Assert.assertEquals(expected, jq("$lbl1").text());
		Assert.assertEquals(expected, jq("$lbl2").text());
		Assert.assertEquals(expected, jq("$lbl3").text());

		modifyZkLabel();
		click(jq("@button:contains(Refresh)"));
		waitResponse();

		expected = "yoyoyo";
		Assert.assertEquals(expected, jq("$lbl1").text());
		Assert.assertEquals(expected, jq("$lbl2").text());
		Assert.assertEquals(expected, jq("$lbl3").text());
	}

	private void modifyZkLabel() throws IOException {
		Files.copy(lbl, lblBak);
		Files.write(lbl, "ZK4046.test=yoyoyo".getBytes());
	}

	private void cleanup() throws IOException {
		click(jq("@button:contains(Enable)"));
		if (Files.exists(lblBak)) {
			Files.copy(lblBak, lbl, StandardCopyOption.REPLACE_EXISTING);
			Files.delete(lblBak);
		}
	}
}
