/* HttpAuWriter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Dec  3 16:18:18     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.http;

import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.json.*;
import org.zkoss.web.servlet.http.Https;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuWriter;
import org.zkoss.zk.au.AuWriters;

/**
 * The writer used to write the output back to the client.
 *
 * @author tomyeh
 * @since 3.0.1
 */
public class HttpAuWriter implements AuWriter{
	/** The writer used to generate the output to.
	 */
	private JSONObject _out;
	private JSONArray _rs;

	public HttpAuWriter() {
	}

	//AuWriter//
	/** Returns au to represent the response channel for AU requests.
	 * @since 3.5.0
	 */
	public String getChannel() {
		return "au";
	}
	/** Opens the connection.
	 *
	 * <p>Default: it creates an instance of {@link JSONObject}
	 * to store the responses.
	 *
	 * <p>This implementation doesn't support the timeout argument.
	 */
	public AuWriter open(Object request, Object response, int timeout)
	throws IOException {
		((HttpServletResponse)response).setContentType(AuWriters.CONTENT_TYPE);
			//Bug 1907640: with Glassfish v1, we cannot change content type
			//in another thread, so we have to do it here

		_out = new JSONObject();
		try {
			_out.put("rs", _rs = new JSONArray());
		} catch (JSONException ex) {
			throw new UiException(ex);
		}
		return this;
	}
	/** Closes the connection.
	 */
	public void close(Object request, Object response)
	throws IOException {
		final HttpServletRequest hreq = (HttpServletRequest)request;
		final HttpServletResponse hres = (HttpServletResponse)response;

		//Use OutputStream due to Bug 1528592 (Jetty 6)
		byte[] data = getResult().getBytes("UTF-8");
		if (data.length > 200) {
			byte[] bs = Https.gzip(hreq, hres, null, data);
			if (bs != null) data = bs; //yes, browser support compress
		}

		hres.setContentType(AuWriters.CONTENT_TYPE);
			//we have to set content-type again. otherwise, tomcat might
			//fail to preserve what is set in open()
		hres.setContentLength(data.length);
		hres.getOutputStream().write(data);
		hres.flushBuffer();
	}
	/** Returns the result of responses that will be sent to client
	 * (never null).
	 * It is called by {@link #close} to retrieve the output.
	 * After invocation, the writer is reset.
	 * @since 5.0.0
	 */
	protected String getResult() {
		final String data = _out.toString();
		_out = null;
		_rs = null;
		return data;
	}
	public void writeResponseId(int resId) throws IOException {
		try {
			_out.put("rid", resId);
		} catch (JSONException ex) {
			throw new UiException(ex);
		}
	}
	public void write(AuResponse response) throws IOException {
		final JSONArray r = new JSONArray();
		r.put(response.getCommand());
		r.put(response.getEncodedData());
		_rs.put(r);
	}
	public void write(Collection responses) throws IOException {
		for (Iterator it = responses.iterator(); it.hasNext();)
			write((AuResponse)it.next());
	}

}
