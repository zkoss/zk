/* B80_ZK_2715Test.java

	Purpose:
		
	Description:
		
	History:
		Thu, Aug 18, 2016  6:16:01 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import junit.framework.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Tab;

import java.util.List;

/**
 * 
 * @author Sefi
 */
public class B80_ZK_2715Test extends ZATSTestCase{
    @Test
    public void test() {
        DesktopAgent desktop = connect();
        ComponentAgent button = desktop.query("button");
        ComponentAgent tab = desktop.queryAll("tab").get(0);
        Assert.assertEquals("tab2", tab.as(Tab.class).getLabel());
        String uuid = desktop.queryAll("tab").get(1).getUuid();
        button.click();
        tab = desktop.queryAll("tab").get(1);
        Assert.assertEquals("tab3", ((Tab) tab.as(Tab.class)).getLabel());
        Assert.assertNotSame(uuid, tab.getUuid());
    }
}
