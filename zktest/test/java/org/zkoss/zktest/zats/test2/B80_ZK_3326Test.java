/* B80_ZK_3326Test.java

	Purpose:
		
	Description:
		
	History:
		Fri, Sep 23, 2016  3:11:12 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import junit.framework.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * 
 * @author Sefi
 */
public class B80_ZK_3326Test extends WebDriverTestCase{
    @Test
    public void test () {
        connect();
        click(jq(".z-bandbox-button"));
        waitResponse();
        click(jq("@listitem"));
        waitResponse();
        click(jq("@bandpopup"));
        waitResponse();
        Assert.assertFalse(getZKLog().contains("bandbox changing"));
    }
}
