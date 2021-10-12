/** ApplyZhtmlTest.java.

	Purpose:
		
	Description:
		
	History:
		11:50:26 AM Feb 26, 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.mvvm._apply;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;

/**
 * @author jameschu
 *
 */
public class ApplyZhtmlTest extends ZutiBasicTestCase {
	@Test
	public void run() {
		DesktopAgent desktop = connect(getTestURL("apply.zhtml"));
		ComponentAgent content = desktop.query("#content");
		ComponentAgent n1 = content.getChildren().get(3);
		ComponentAgent n2 = content.getChildren().get(4);
		assertEquals("1231", n1.as(HtmlNativeComponent.class).getPrologContent().trim());
		assertEquals("1232", n2.as(HtmlNativeComponent.class).getPrologContent().trim());
		
		ComponentAgent t1 = content.getChildren().get(5);
		assertEquals("1233", t1.as(Text.class).getValue().trim());
		
		ComponentAgent n3 = content.getChildren().get(7);
		assertEquals("With Template URI 1-1", n3.as(HtmlNativeComponent.class).getPrologContent().trim());
		ComponentAgent t2 = content.getChildren().get(8);
		assertEquals("111", t2.as(Text.class).getValue().trim());
		ComponentAgent n4 = content.getChildren().get(9);
		assertEquals("With Template URI 1-2", n4.as(HtmlNativeComponent.class).getPrologContent().trim());
		
		content.getChildren().get(1).click();
		content = desktop.query("#content");
		
		n3 = content.getChildren().get(7);
		assertEquals("With Template URI 2-1", n3.as(HtmlNativeComponent.class).getPrologContent().trim());
		t2 = content.getChildren().get(8);
		assertEquals("222", t2.as(Text.class).getValue().trim());
		n4 = content.getChildren().get(9);
		assertEquals("With Template URI 2-2", n4.as(HtmlNativeComponent.class).getPrologContent().trim());
	}
}
