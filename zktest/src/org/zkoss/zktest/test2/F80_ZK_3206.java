/* F80_ZK_3206.java

	Purpose:
		
	Description:
		
	History:
		Thu May  5 15:54:25 CST 2016, Created by wenning

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;

/**
 * 
 * @author wenning
 */
public class F80_ZK_3206 extends SelectorComposer {

    @Listen("onClick=#btn1")
    public void clickBtn1(MouseEvent evt) {
        Clients.log(Integer.toString(evt.getKeys()));
    }

}
