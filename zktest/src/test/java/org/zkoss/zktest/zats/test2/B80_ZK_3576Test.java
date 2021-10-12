/* B80_ZK_3576Test.java

	Purpose:

	Description:

	History:
		Thu Jan 19 11:00:31 CST 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * 
 * @author jameschu
 */
public class B80_ZK_3576Test extends ZATSTestCase{
    @Test
    public void test(){
		DesktopAgent desktop = connect();
		List<ComponentAgent> labels = desktop.queryAll("label");
		assertEquals(1, labels.size());
		assertEquals("test", labels.get(0).as(Label.class).getValue());
    }
}
