/* F2790411.java

	Purpose:
		
	Description:
		
	History:
		Tue May 12 13:03:38     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zkdemo.test2;

import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.*;

/**
 * Working thread invokes createComponents.
 *
 * @author tomyeh
 */
public class F2790411Thread extends Thread {
	private static final Log log = Log.lookup(F2790411Thread.class);

	private final WebApp _wapp;
	private final String _uri;
	private Component _comp;
	private String _errmsg;
	private boolean _done;

	public F2790411Thread(WebApp wapp, String uri) {
		_wapp = wapp;
		_uri = uri;
	}
	public Component getResult() {
		return _comp;
	}
	public boolean isDone() {
		return _done;
	}
	public String getError() {
		return _errmsg;
	}
	
	public void run() {
		_done = false;
		try {
			_comp = Executions.createComponents(_wapp, _uri, null)[0];
		} catch (Throwable ex) {
			log.realCauseBriefly(ex);
			_errmsg = Exceptions.getMessage(ex);
		}
		_done = true;
	}
}
