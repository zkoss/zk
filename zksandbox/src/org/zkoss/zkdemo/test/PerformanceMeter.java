/* PerformanceMeter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 24 19:14:55     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import org.zkoss.zk.ui.Execution;

/**
 * A simple performance meter.
 *
 * @author tomyeh
 */
public class PerformanceMeter implements org.zkoss.zk.ui.util.PerformanceMeter {
	public void requestStartAtClient(
	String requestId, Execution exec, long time) {
		System.out.println("Client start: "+requestId+"="+time);
	}
	public void requestReceiveAtClient(
	String requestId, Execution exec, long time) {
		System.out.println("Client recev: "+requestId+"="+time);
	}
	public void requestCompleteAtClient(
	String requestId, Execution exec, long time) {
		System.out.println("Client cmplt: "+requestId+"="+time);
	}

	public void requestStartAtServer(
	String requestId, Execution exec, long time) {
		System.out.println("Server start: "+requestId+"="+time);
	}
	public void requestCompleteAtServer(
	String requestId, Execution exec, long time) {
		System.out.println("Server cmplt: "+requestId+"="+time);
	}
}
