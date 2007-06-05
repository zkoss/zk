/* AbstractUiFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 15 15:00:32     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;

import org.xml.sax.Attributes;
import org.zkoss.zkmob.impl.Zk;

/**
 * An UiFactory skeleton which registers itself to the {@link UiManager}.
 *
 * @author henrichen
 */
public abstract class AbstractUiFactory implements UiFactory {

	protected AbstractUiFactory(String name) {
		UiManager.registerUiFactory(name, this);
	}
}
