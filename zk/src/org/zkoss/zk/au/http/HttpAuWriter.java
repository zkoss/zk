/* HttpAuWriter.java

	Purpose:
		
	Description:
		
	History:
		Mon Dec  3 16:18:18     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
	/** The result that shall be sent instead of _rs. */
	private byte[] _result;
	private boolean _compress = true;

	public HttpAuWriter() {
	}

	/** Returns whether to compress the output.
	 * @since 3.6.3
	 */
	public boolean isCompress() {
		return _compress;
	}

	//AuWriter//
	public void setCompress(boolean compress) {
		_compress = compress;
	}
	/** Opens the connection.
	 *
	 * <p>Default: it creates an object to store the responses.
	 *
	 * <p>This implementation doesn't support the timeout argument.
	 */
	public AuWriter open(Object request, Object response, int timeout)
	throws IOException {
		((HttpServletResponse)response).setContentType(AuWriters.CONTENT_TYPE);
			//Bug 1907640: with Glassfish v1, we cannot change content type
			//in another thread, so we have to do it here

		_out = AuWriters.getJSONOutput(_rs = new JSONArray());
		return this;
	}
	public void close(Object request, Object response) throws IOException {
		flush(request, response, _compress);
	}
	public void resend(Object prevContent) throws IOException {
		if (prevContent == null)
			throw new IllegalArgumentException();
		if (_result != null || !_rs.isEmpty())
			throw new IllegalStateException(_rs.isEmpty() ? "resend twice or complete?": "write called");
		_result = restore(prevContent);
		_out = null;
		_rs = null;
	}

	public Object complete() throws IOException {
		if (_result != null)
			throw new IllegalStateException();
		_result = _out.toString().getBytes("UTF-8");
		_out = null;
		_rs = null;
		return save(_result);
	}
	/** Flush the result of responses to client.
	 * @param bCompress whether to compress (if allowed).
	 * @since 5.0.4
	 */
	protected void flush(Object request, Object response, boolean bCompress)
	throws IOException {
		if (_result == null) {
			_result = _out.toString().getBytes("UTF-8");
			_out = null;
			_rs = null;
		}

		final HttpServletRequest hreq = (HttpServletRequest)request;
		final HttpServletResponse hres = (HttpServletResponse)response;
		if (bCompress && _result.length > 200) {
			final byte[] bs = Https.gzip(hreq, hres, null, _result);
			if (bs != null)
				 _result = bs; //yes, browser support compress
		}
		hres.setContentType(AuWriters.CONTENT_TYPE);
			//we have to set content-type again. otherwise, tomcat might
			//fail to preserve what is set in open()
		hres.setContentLength(_result.length);
		hres.getOutputStream().write(_result);
			//Use OutputStream due to Bug 1528592 (Jetty 6)
		hres.flushBuffer();
	}
	/** Called to encode the last response for repeated request before storing.
	 * <p>Default: does nothing but return the input argument, data.
	 *  The derived class might override this method to compress it.
	 * @see #restore
	 * @since 5.0.4
	 */
	protected Object save(byte[] data) {
		return data;
	}
	/** Called to decode the last response for repeated request before retrieving.
	 * <p>Default: does nothing but return the input argument, data.
	 *  The derived class might override this method to uncompress it.
	 * @param data the data returned by {@link #save}
	 * @return the byte array that is passed to {@link #save}.
	 * @since 5.0.4
	 */
	protected byte[] restore(Object data) {
		return (byte[])data;
	}

	public void writeResponseId(int resId) throws IOException {
		_out.put("rid", new Integer(resId));
	}
	public void write(AuResponse response) throws IOException {
		_rs.add(AuWriters.toJSON(response));
	}
	public void write(Collection responses) throws IOException {
		for (Iterator it = responses.iterator(); it.hasNext();)
			write((AuResponse)it.next());
	}
}
