/* SmartAuWriter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec  4 09:58:48     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.au.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.util.ScalableTimer;
import org.zkoss.util.ScalableTimerTask;

import org.zkoss.zk.au.AuWriter;
import org.zkoss.zk.au.http.HttpAuWriter;

/**
 * A smart AU writer that will generate some output to client first if
 * the processing takes more than the time specified in the timeout argument
 * of {@link #open}.
 *
 * @author tomyeh
 * @since 3.0.1
 */
public class SmartAuWriter extends HttpAuWriter {
	private static final ScalableTimer _timer = new ScalableTimer(20, 25);
	private Task _tmoutTask;
	/** The output stream. It is not null only if _tmoutTask is not null. */
	private HttpServletResponse _res;
	/** true if timeout happens. */
	private boolean _timeout;

	public SmartAuWriter() {
	}

	/** Opens the connection.
	 * It starts a timer and generate some output to client first if
	 * the processing take more than the time specified in the timeout argument.
	 */
	public AuWriter open(Object request, Object response, int timeout)
	throws IOException {
		if (timeout > 0) {
			_res = (HttpServletResponse)response;
			_timer.schedule(_tmoutTask = new Task(), timeout);
		}
		return super.open(request, response, timeout);
	}
	protected void flush(HttpServletRequest request, HttpServletResponse response)
	throws IOException {
		final boolean timeout;
		final Task task = _tmoutTask;
		if (task != null)
			task.cancel();

		synchronized (this) {
			_tmoutTask = null;
			_res = null;
			timeout = _timeout;
			_timeout = false;
		}

		if (timeout) {
			response.getOutputStream()
				.write(_out.getBuffer().substring(OUTPUT_HEAD.length()).getBytes("UTF-8"));
			response.flushBuffer();
		} else {
			super.flush(request, response);
		}
	}

	private class Task extends ScalableTimerTask {
		public void exec() {
			synchronized (this) {
				if (_tmoutTask != null) {
					//we have to write something to client to let the Ajax request
					//advance XMLHttpRequest.readyState (to 3).
					//Otherwise, the client may consider it as timeout and resend
					try {
						_res.getOutputStream().write(OUTPUT_HEAD_BYTES);
					} catch (IOException ex) { //ignore it
					}

					_tmoutTask = null; //mark as done
					_res = null; //clean up
					_timeout = true; //let flush know OUTPUT_HEAD is sent
					_res.flushBuffer();
				}
			}
		}
	}
}
