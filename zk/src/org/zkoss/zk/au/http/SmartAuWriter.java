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
package org.zkoss.zk.au.http;

import java.util.Collection;
import java.util.Iterator;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.idom.Verifier;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.ScalableTimer;
import org.zkoss.util.ScalableTimerTask;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuWriter;

/**
 * A smart AU writer that will generate some output to client first if
 * the processing takes more than the time specified in the timeout argument
 * of {@link #open}.
 *
 * @author tomyeh
 * @since 2.4.3
 */
public class SmartAuWriter implements AuWriter {
	private static final Log log = Log.lookup(SmartAuWriter.class);

	/** The first few characters of the output content. */
	private static final String OUTPUT_HEAD =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	/** The content type of the output. */
	private static final String CONTENT_TYPE = "text/xml;charset=UTF-8";

	/** The first few bytes of the output header (byte[]). */
	private static final byte[] OUTPUT_HEAD_BYTES = OUTPUT_HEAD.getBytes();
	private static ScalableTimer _timer;

	/** The writer used to generate the output to. */
	private StringWriter _out;
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
	 * @since 2.4.3
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
		final HttpServletResponse hres = (HttpServletResponse)response;
		hres.setContentType(CONTENT_TYPE);
			//Bug 1907640: with Glassfish v1, we cannot change content type
			//in another thread, so we have to do it here

		_out = new StringWriter();
		_out.write(OUTPUT_HEAD);
		_out.write("<rs>\n");

		if (timeout > 0)
			_timer.schedule(_tmoutTask = new Task(hres), timeout);

		return this;
	}
	public void close(Object request, Object response)
	throws IOException {
		final HttpServletRequest hreq = (HttpServletRequest)request;
		final HttpServletResponse hres = (HttpServletResponse)response;

		_out.write("\n</rs>");

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
			//write the rest of _out (except OUTPUT_HEAD)
			//and no compress
			hres.getOutputStream()
				.write(_out.getBuffer().substring(OUTPUT_HEAD.length()).getBytes("UTF-8"));
			hres.flushBuffer();
		} else {
			//default: compress and generate _out

			//Use OutputStream due to Bug 1528592 (Jetty 6)
			byte[] data = _out.toString().getBytes("UTF-8");
			if (data.length > 200) {
				byte[] bs = Https.gzip(hreq, hres, null, data);
				if (bs != null) data = bs; //yes, browser support compress
			}

			hres.setContentType(CONTENT_TYPE);
				//we have to set content-type again. otherwise, tomcat might
				//fail to preserve what is set in open()
			hres.setContentLength(data.length);
			hres.getOutputStream().write(data);
			hres.flushBuffer();
		}
	}

	public void write(AuResponse response) throws IOException {
		_out.write("\n<r><c>");
		_out.write(response.getCommand());
		_out.write("</c>");
		final String[] data = response.getData();
		if (data != null) {
			for (int j = 0; j < data.length; ++j) {
				_out.write("\n<d>");
				encodeXML(data[j], _out);
				_out.write("</d>");
			}
		}
		_out.write("\n</r>");
	}
	public void write(Collection responses) throws IOException {
		for (Iterator it = responses.iterator(); it.hasNext();)
			write((AuResponse)it.next());
	}

	//Utilities//
	private static void encodeXML(String data, Writer out)
	throws IOException {
		if (data == null || data.length() == 0)
			return;

		//20051208: Tom Yeh
		//The following codes are tricky.
		//Reason:
		//1. Nested CDATA is not allowed
		//2. Illegal character must be encoded
		//3. Firefox (1.0.7)'s XML parser cannot handle over 4096 chars
		//	if CDATA is not used
		int k = 0, len = data.length();
		for (int j = 0; j < len;) {
			final char cc = data.charAt(j);
			if (cc == ']') {
				if (j + 2 < len && data.charAt(j + 1) == ']'
				&& data.charAt(j + 2) == '>') { //]]>
					encodeByCData(data.substring(k, j + 2), out);
					out.write("&gt;");
					k = j += 3;
					continue;
				}
			} else if (!Verifier.isXMLCharacter(cc)) {
				encodeByCData(data.substring(k, j), out);
				out.write('?');
					//No way to represent illegal character (&#xn not allowed, either)
					//FUTURE: consider to use a special escape sequence
					//and restore it at the client. But, it slows down
					//the performance and might not be worth
				k = ++j;
				continue;
			}
			++j;
		}

		if (k < len)
			encodeByCData(data.substring(k), out);
	}
	private static void encodeByCData(String data, Writer out)
	throws IOException {
		int j = data.length();
		if (j > 100) { //Not to scan if it is long
			out.write("<![CDATA[");
			out.write(data);
			out.write("]]>");
			return;
		}

		while (--j >= 0) {
			final char cc = data.charAt(j);
			if (cc == '<' || cc == '>' || cc == '&') {
				out.write("<![CDATA[");
				out.write(data);
				out.write("]]>");
				return;
			}
		}
		out.write(data);
	}

	//Class//
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
						_res.getOutputStream().write(OUTPUT_HEAD_BYTES);
						_timeout = true; //let flush() know OUTPUT_HEAD is sent
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
