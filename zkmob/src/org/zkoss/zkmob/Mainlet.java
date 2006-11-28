/* Mainlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Nov 27 17:22:06     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkmob;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Display;

/**
 * The main MIDlet of ZK Mobile Client.
 *
 * @author tomyeh
 */
public class Mainlet extends MIDlet {
	private Display _disp;

	public Mainlet() {
	}

	//super//
	protected void startApp() {
		init();
	}
	protected void pauseApp() {
	}
	protected void destroyApp(boolean unconditional) {
	}

	//private//
	/** Initializes this MIDlet. */
	private void init() {
		if (_disp == null) {
			_disp = Display.getDisplay(this);
			//_disp.setCurrent();
		}
	}
}
