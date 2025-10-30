/* B80_ZK_3258Test.java

	Purpose:

	Description:

	History:
		Mon Aug 8 17:34:32 CST 2016, Created by sefi

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author sefi
 */
public class B80_ZK_3258Test extends ZATSTestCase {
    @Test
	public void test() {
        try {
            connect();
        } catch (Exception e){
            fail("Should not throw any exception");
        }
        assertTrue(true);
    }
}
