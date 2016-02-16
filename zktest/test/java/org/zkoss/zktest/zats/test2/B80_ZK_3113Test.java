/* B80_ZK_3113Test.java

	Purpose:
		
	Description:
		
	History:
		Tue, Feb  2, 2016  4:09:44 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.zkoss.lang.Classes;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Textbox;
/**
 * 
 * @author Sefi
 */
public class B80_ZK_3113Test extends ZATSTestCase {
    @Test public void test() {
        try {
           	connect();
        } catch (Exception e){
            fail("connect fail : " + e.getMessage());
        }
        Textbox textbox = new Textbox();
        Integer i = null;
        try {
            PropertyAccess pa1 = textbox.getPropertyAccess("rows");
            pa1.setValue(textbox, Classes.coerce(pa1.getType(), i));
            PropertyAccess pa2 = textbox.getPropertyAccess("cols");
            pa2.setValue(textbox, Classes.coerce(pa2.getType(), null));

        } catch (NullPointerException e){
            fail("NullPointerException exception" + e.getMessage());
        } catch (WrongValueException e){
            assertTrue(true);
        }
    }
}
