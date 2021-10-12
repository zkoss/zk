/* F96_ZK_4917Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 09 17:03:54 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThan;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author rudyhuang
 */
public class F96_ZK_4917Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitForPDFLoaded();

		final Widget pdfviewer = widget("$pv");
		final JQuery content = jq(pdfviewer.$n("content"));
		final JQuery zoomLevel = jq(pdfviewer.$n("toolbar-zoom"));
		final int widthFitWidth = content.width();
		// Layout fit-page-width
		Assert.assertEquals("fit-page-width", zoomLevel.val());
		Assert.assertEquals(jq(pdfviewer).innerWidth(), widthFitWidth + JQuery.scrollbarWidth(), 5);
		// Invalidate fit-page-width
		click(jq("@toolbarbutton:contains(Invalidate)"));
		waitResponse();
		waitForPDFLoaded();
		Assert.assertEquals("fit-page-width", zoomLevel.val());
		Assert.assertEquals(widthFitWidth, content.width(), 2);

		// Zoom mode changed
		click(jq("@toolbarbutton:contains(Zoom In)"));
		waitResponse();
		click(jq("@toolbarbutton:contains(Invalidate)"));
		waitResponse();
		waitForPDFLoaded();
		Assert.assertNotEquals("fit-page-width", zoomLevel.val());
		MatcherAssert.assertThat(content.width(), greaterThan(widthFitWidth));

		// Update fit-page-height
		click(jq("@toolbarbutton:contains(Zoom: Fit Height)"));
		waitResponse();
		final int heightFitHeight = content.height();
		Assert.assertEquals("fit-page-height", zoomLevel.val());
		Assert.assertEquals(jq(pdfviewer).innerHeight(), heightFitHeight, 5);
		// Invalidate fit-page-height
		click(jq("@toolbarbutton:contains(Invalidate)"));
		waitResponse();
		waitForPDFLoaded();
		Assert.assertEquals("fit-page-height", zoomLevel.val());
		Assert.assertEquals(heightFitHeight, content.height(), 2);
	}

	@Test
	public void testClient() {
		connect();
		waitForPDFLoaded();

		final Widget pdfviewer = widget("$pv");
		final JQuery content = jq(pdfviewer.$n("content"));
		final JQuery zoomLevel = jq(pdfviewer.$n("toolbar-zoom"));
		final int widthFitWidth = content.width();

		// Zoom mode changed
		click(pdfviewer.$n("toolbar-zoom-in"));
		waitResponse();
		click(jq("@toolbarbutton:contains(Invalidate)"));
		waitResponse();
		waitForPDFLoaded();
		Assert.assertNotEquals("fit-page-width", zoomLevel.val());
		MatcherAssert.assertThat(content.width(), greaterThan(widthFitWidth));

		// Update fit-page-height
		new Select(toElement(zoomLevel)).selectByVisibleText("Fit Height");
		waitResponse();
		final int heightFitHeight = content.height();
		Assert.assertEquals("fit-page-height", zoomLevel.val());
		Assert.assertEquals(jq(pdfviewer).innerHeight(), heightFitHeight, 5);
		// Invalidate fit-page-height
		click(jq("@toolbarbutton:contains(Invalidate)"));
		waitResponse();
		waitForPDFLoaded();
		Assert.assertEquals("fit-page-height", zoomLevel.val());
		Assert.assertEquals(heightFitHeight, content.height(), 2);
	}

	private void waitForPDFLoaded() {
		final WebDriverWait wait = new WebDriverWait(driver, 3);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("zk_log")));
		closeZKLog();
	}
}
