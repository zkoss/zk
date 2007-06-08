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
	private Browser _browser;
	private String _url;

	public PageRequest(Browser browser, String url) {
		_browser = browser;
		_url = url;
	}

	public void run() {
		UiManager.loadPage(_browser, _url);
	}
}
