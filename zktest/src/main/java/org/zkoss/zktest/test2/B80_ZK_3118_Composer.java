/* B80_ZK_3118_Composer.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr  7 12:27:01 CST 2016, Created by wenninghsu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;

/**
 * 
 * @author wenninghsu
 */
public class B80_ZK_3118_Composer extends GenericForwardComposer {

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Clients.showBusy(comp, "showbusy");
	}

}
