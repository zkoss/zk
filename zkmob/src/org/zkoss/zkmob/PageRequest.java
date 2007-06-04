/* PageLoader.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 22, 2007 11:08:24 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

import javax.microedition.lcdui.Display;

/**
 * PageLoader that load page in another thread and setup associated Display.  
 * @author henrichen
 */
public class PageRequest implements Runnable {
	private Display _disp;
	private String _url;
	private UiManager _uiManager;

	public PageRequest(Display disp, String url, UiManager uiManager) {
		_disp = disp;
		_url = url;
		_uiManager = uiManager;
	}

	public void run() {
		_uiManager.loadPage(_disp, _url);
	}
}
