/* AuRequest.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 30, 2007 6:44:53 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

import javax.microedition.lcdui.Display;

import org.zkoss.zkmob.impl.Zk;

/**
 * @author henrichen
 *
 */
public class UpdateRequest implements Runnable {
	private Zk _zk;
	private String _url;
	private String _request;

	public UpdateRequest(Zk zk, String url, String request) {
		_zk = zk;
		_url = url;
		_request = request;
	}
	
	public void run() {
		_zk.update(_url, _request);
	}
}
