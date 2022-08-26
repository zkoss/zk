/* B86_ZK_4271Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 29 17:18:55 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;

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
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class B86_ZK_4271Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent btn = desktop.query("button");
		ComponentAgent menuitem = desktop.query("menuitem");
		ComponentAgent dropupload = desktop.query("dropupload");
		Assertions.assertTrue(getUploadNative(btn));
		Assertions.assertTrue(getUploadNative(menuitem));
		Assertions.assertTrue(getUploadNative(dropupload));

		UploadAgent uploadAgent = btn.as(UploadAgent.class);
		uploadAgent.upload("test.txt", newContent(), "text/plain");
		uploadAgent.finish();
		assertThat(desktop.query("#result1").as(Label.class).getValue(), endsWith(", binary=true"));

		UploadAgent uploadAgent2 = menuitem.as(UploadAgent.class);
		uploadAgent2.upload("test.txt", newContent(), "text/plain");
		uploadAgent2.finish();
		assertThat(desktop.query("#result2").as(Label.class).getValue(), endsWith(", binary=true"));

		// not support UploadAgent for dropupload for now
	}

	private boolean getUploadNative(ComponentAgent comp) {
		return Boolean.TRUE.equals(comp.getAttribute(Attributes.UPLOAD_NATIVE));
	}

	private InputStream newContent() {
		return new ByteArrayInputStream("Hello".getBytes(StandardCharsets.UTF_8));
	}
}
