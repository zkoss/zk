/* ZscriptComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Apr 17, 2009 7:17:36 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.zkdemo.test2;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.api.Button;

/**
 * Test whether ZScript variable can be wired.
 * @author henrichen
 *
 */
public class ZscriptComposer extends GenericForwardComposer {
	private Integer zscriptVar;
	private Button show;
	
	public void onClick$show() {
		show.setLabel("zscriptVar:"+zscriptVar);
	}
}
