/* B80_ZK_3201Test.java

	Purpose:
		
	Description:
		
	History:
		Thu, Sep  8, 2016  4:32:29 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

/**
 * 
 * @author Sefi
 */
public class B80_ZK_3201Test extends ZATSTestCase{
    @Test
    public void test() {
        try{
            DesktopAgent desktop = connect();
            desktop.query("#btn").click();
        } catch (ZatsException e) {
            fail();
        }
        assertTrue(true);
    }
}
