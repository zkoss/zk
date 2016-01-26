/* B70_ZK_2958Test.java

	Purpose:
		
	Description:
		
	History:
		Fri, Jan 22, 2016  3:45:40 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.fail;
import org.junit.Test;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * 
 * @author Sefi
 */
public class B70_ZK_2958Test  extends ZATSTestCase {
    @Test
    public void test() {
        try {
            connect();
        } catch (Exception e) {
            fail();
        }
    }
}
