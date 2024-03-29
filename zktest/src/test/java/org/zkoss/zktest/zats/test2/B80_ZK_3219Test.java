/* B80_ZK_3219Test.java

	Purpose:
		
	Description:
		
	History:
		Tue, Jun 21, 2016  3:54:24 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * 
 * @author Sefi
 */
public class B80_ZK_3219Test extends ZATSTestCase{
    @Test
    public void test(){
        try {
            DesktopAgent desktop = connect();
            Assertions.assertTrue(true);
            List<ComponentAgent> buttons = desktop.queryAll("label");

            Assertions.assertEquals("test", buttons.get(1).as(Label.class).getValue());
            Assertions.assertTrue(buttons.get(2).as(Label.class).getValue().startsWith("{c=org.zkoss.util.resource.impl.LabelLoaderImpl$ExValue"));
            Assertions.assertEquals("test", buttons.get(3).as(Label.class).getValue());
        } catch (Exception e) {
            Assertions.fail();
        }
    }
}
