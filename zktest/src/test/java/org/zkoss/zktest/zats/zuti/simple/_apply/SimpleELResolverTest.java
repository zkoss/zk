/* SimpleELResolverTest.java

	Purpose:
		
	Description:
		
	History:
		3:05 PM 7/20/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.simple._apply;

import junit.framework.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zul.Textbox;

/**
 * @author jumperchen
 */
public class SimpleELResolverTest extends ZutiBasicTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent window = desktop.query("window");
		Assert.assertEquals(window.getFirstChild().<Textbox>getOwner().getValue(), "MyName");
	}
}
