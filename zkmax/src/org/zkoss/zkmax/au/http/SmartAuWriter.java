/* SmartAuWriter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec  4 09:58:48     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.au.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.Exceptions;
import org.zkoss.util.ScalableTimer;
import org.zkoss.util.ScalableTimerTask;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.au.AuWriter;
import org.zkoss.zk.au.AuWriters;
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
	private static final Log log = Log.lookup(SmartAuWriter.class);

	/** The first few bytes of the output header (byte[]). */
	private static final byte[] CONTENT_HEAD_BYTES = AuWriters.CONTENT_HEAD.getBytes();
	private static ScalableTimer _timer;

	private Task _tmoutTask;
	/** true if timeout happens. */
	private boolean _timeout;

	public SmartAuWriter() {
		if (_timer == null)
			_timer = newTimer();
	}

	/** Creates an instance of {@link ScalableTimer}.
	 * <p>By default, it creates a scalable timer that will use at most
	 * 50 real timers (i.e., new ScalableTimer(50, 25)).
	 * If you want to change it, you can override this method.
	 * @since 3.0.4
	 */
	protected ScalableTimer newTimer() {
		return new ScalableTimer(50, 25);
	}
	/** Opens the connection.
	 * It starts a timer and generate some output to client first if
	 * the processing take more than the time specified in the timeout argument.
	 */
	public AuWriter open(Object request, Object response, int timeout)
	throws IOException {
		if (timeout > 0)
			_timer.schedule(
				_tmoutTask = new Task((HttpServletResponse)response), timeout);
		return super.open(request, response, timeout);
	}
	protected void flush(HttpServletRequest request, HttpServletResponse response)
	throws IOException {
		final Task task = _tmoutTask;
		if (task != null)
			task.cancel();

		final boolean timeout;
		synchronized (this) {
			_tmoutTask = null;
			timeout = _timeout;
			_timeout = false;
		}

		if (timeout) {
			//write the rest of _out (except CONTENT_HEAD)
			//and no compress
			response.getOutputStream()
				.write(_out.getBuffer().substring(AuWriters.CONTENT_HEAD.length()).getBytes("UTF-8"));
			response.flushBuffer();
		} else {
			//default: compress and generate _out
			super.flush(request, response);
		}
	}

	private class Task extends ScalableTimerTask {
		/** The output stream. It is not null only if _tmoutTask is not null. */
		private final HttpServletResponse _res;

		private Task(HttpServletResponse response) {
			_res = response;
		}
		public void exec() {
			synchronized (SmartAuWriter.this) {
				if (_tmoutTask != null) {
					//we have to write something to client to let the Ajax request
					//advance XMLHttpRequest.readyState (to 3).
					//Otherwise, the client may consider it as timeout and resend
					try {
						_res.getOutputStream().write(CONTENT_HEAD_BYTES);
						_timeout = true; //let flush() know CONTENT_HEAD is sent
						_res.flushBuffer();
					} catch (Throwable ex) {
						log.warning("Ignored: failed to send the head\n"+Exceptions.getMessage(ex));
					}
					_tmoutTask = null; //mark as done
				}
			}
		}
	}
}
