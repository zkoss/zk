/* B86_ZK_4220Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 30 09:52:04 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.UploadAgent;
import org.zkoss.zk.ui.impl.Attributes;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4220Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent btn = desktop.query("toolbarbutton");
		Assertions.assertTrue(getUploadNative(btn));

		UploadAgent uploadAgent = btn.as(UploadAgent.class);
		uploadAgent.upload("test.txt", newContent(), "text/plain");
		uploadAgent.finish();
		// if an IllegalStateException: Use getStringData() instead is thrown, the test would be failed.
	}

	private boolean getUploadNative(ComponentAgent comp) {
		return Boolean.TRUE.equals(comp.getAttribute(Attributes.UPLOAD_NATIVE));
	}

	private InputStream newContent() {
		return new ByteArrayInputStream("Hello".getBytes(StandardCharsets.UTF_8));
	}
}
