/* ExecutionCarryOver.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 14 17:07:35     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.Collection;
import java.util.Locale;
import java.util.TimeZone;

import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;

/**
 * A utility to carry over the execution to a server-push thread.
 * It is usually used by the implementation of
 * {@link org.zkoss.zk.ui.sys.ServerPush}.
 *
 * <p>How it works: First, create an instance in the event listener.
 * Then, you can invoke {@link #carryOver} in the server-push thread.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class ExecutionCarryOver {
	/** The execution of onPiggyback. */
	private final Execution _exec;
	/** Part of the command: locale. */
	private Locale _locale;
	/** Part of the command: time zone. */
	private TimeZone _timeZone;

	public ExecutionCarryOver(Desktop desktop) {
		_exec = Executions.getCurrent();
		if (_exec == null || _exec.getDesktop() != desktop)
			throw new IllegalStateException("Wrong execution: "+_exec);
		_locale = Locales.getCurrent();
		_timeZone = TimeZones.getCurrent();
	}
	/** Carry over the info stored in the constructor to
	 * the current thread.
	 */
	public void carryOver() {
		ExecutionsCtrl.setCurrent(_exec);
		final ExecutionCtrl execCtrl = (ExecutionCtrl)_exec;
		execCtrl.onActivate();

		if (Locales.getThreadLocal() == null)
			Locales.setThreadLocal(_locale);
		else
			_locale = null;

		if (TimeZones.getThreadLocal() == null)
			TimeZones.setThreadLocal(_timeZone);
		else
			_timeZone = null;

		final Page page = _exec.getDesktop().getFirstPage();
		if (page != null)
			execCtrl.setCurrentPage(page);
	}
	/** Cleans up the info carried from onPiggyback to the current thread.
	 * <p>Note: {@link #carryOver} and {@link #cleanup} must be
	 * called in the same thread.
	 */
	public void cleanup() {
		((ExecutionCtrl)_exec).onDeactivate();
		ExecutionsCtrl.setCurrent(null);

		if (_locale != null)
			Locales.setThreadLocal(null);
		if (_timeZone != null)
			TimeZones.setThreadLocal(null);
	}
}
