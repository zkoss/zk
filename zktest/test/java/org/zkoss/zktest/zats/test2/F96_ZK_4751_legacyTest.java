/* F96_ZK_4751_legacyTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 10 11:25:12 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.text.CharSequenceLength.hasLength;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hamcrest.MatcherAssert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
@Category(ForkJVMTestOnly.class)
public class F96_ZK_4751_legacyTest extends ZATSTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F96-ZK-4751-zk.xml");

	@Test
	public void test() {
		final DesktopAgent desktop = connect("/test2/F96-ZK-4751.zul");
		final Desktop dt = desktop.getDesktop();
		final WebApp webApp = dt.getWebApp();

		SessionsCtrl.setCurrent(dt.getSession());

		final String d1Id = getNewDesktop(webApp).getId();
		final String d2Id = getNewDesktop(webApp).getId();

		MatcherAssert.assertThat(d1Id, endsWith(d2Id.substring(4))); // skip z_??
		MatcherAssert.assertThat(d1Id, hasLength(lessThan(10))); // weak strength
		MatcherAssert.assertThat(d2Id, hasLength(lessThan(10)));
	}

	private Desktop getNewDesktop(WebApp webApp) {
		final UiFactory uiFactory = ((WebAppCtrl) webApp).getUiFactory();
		final RequestInfo reqInfo = mock(RequestInfo.class);
		when(reqInfo.getWebApp()).thenReturn(webApp);
		return uiFactory.newDesktop(reqInfo, "dummy", "dummy");
	}
}
