/* B80_ZK_3251Test.java

	Purpose:
		
	Description:
		
	History:
		Fri, Jul  1, 2016  5:24:31 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.lang.PotentialDeadLockException;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * 
 * @author Sefi
 */
public class B80_ZK_3251Test extends ZATSTestCase{
    @Test
    public void test() {
        try {
            connect();
        } catch (PotentialDeadLockException e) {
            Assert.fail();
        }
        Assert.assertTrue(true);
    }
}
