/* HttpBufferedResponse.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jan 17 14:02:29     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.http;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.zkoss.io.PrintWriterX;
import org.zkoss.web.servlet.ServletOutputStreamWrapper;

/**
 * A servlet response that uses another writer or stream as
 * the output.
 *
 * @author tomyeh
 */
public class HttpBufferedResponse extends HttpServletResponseWrapper {
	private Writer _writer;
	private OutputStream _stream;

	private PrintWriter _pwt;
	private ServletOutputStream _sos;
	private boolean _bSendRedirect;

	/** Returns a buffered response with a writer, if writer is not null;
	 * or the original response if writer is null.
	 * It is smart enough not to wrap the same writer twice.
	 */
	public static final HttpServletResponse
	getInstance(HttpServletResponse response, Writer writer) {
		if (writer != null
		&& (!(response instanceof HttpBufferedResponse)
			|| ((HttpBufferedResponse)response)._writer != writer))
			return new HttpBufferedResponse(response, writer);
		return response;
	}
	/** Returns a buffered response with a output stream, if stream is not null;
	 * or the original response if stream is null.
	 * It is smart enough not to wrap the same stream twice.
	 */
	public static final HttpServletResponse
	getInstance(HttpServletResponse response, OutputStream stream) {
		if (stream != null
		&& (!(response instanceof HttpBufferedResponse)
			|| ((HttpBufferedResponse)response)._stream != stream))
			return new HttpBufferedResponse(response, stream);
		return response;
	}

	/** Constructs a buffered response with a writer. */
	private HttpBufferedResponse(HttpServletResponse response,
	Writer writer) {
		super(response);
		if (writer == null)
			throw new IllegalArgumentException("null writer");
		_writer = writer;
	}
	/** Constructs a buffered response with an output stream. */
	private HttpBufferedResponse(HttpServletResponse response,
	OutputStream stream) {
		super(response);
		if (stream == null)
			throw new IllegalArgumentException("null stream");
		_stream = stream;
	}

	//extra//
	/** Returns whether {@link #sendRedirect} was called.
	 */
	public boolean isSendRedirect() {
		return _bSendRedirect;
	}

	//super//
	public PrintWriter getWriter() throws IOException {
		if (_sos != null)
			throw new IllegalStateException("getOutputStream was called");

		if (_pwt == null) {
			if (_writer != null) {
				if (_writer instanceof PrintWriter) {
					_pwt = (PrintWriter)_writer;
				} else {
					_pwt = new PrintWriterX(_writer);
				}
			} else {
				_pwt = new PrintWriterX(new OutputStreamWriter(
					_stream, getCharacterEncoding()));
			}
		}
		return _pwt;
	}
	public ServletOutputStream getOutputStream() throws IOException {
		if (_pwt != null)
			throw new IllegalStateException("getWriter was called");

		if (_sos == null) {
			if (_stream != null) {
				_sos = ServletOutputStreamWrapper.getInstance(_stream);
			} else {
				_sos = ServletOutputStreamWrapper.getInstance(_writer,
					getCharacterEncoding());
			}
		}
		return _sos;
	}

	public void flushBuffer() throws IOException {
		if (_writer != null) _writer.flush();
		else _stream.flush();
	}
	/** Useful only if StringWriter or ByteArrayOutputStream is used
	 * to construct this object.
	 */
	public void resetBuffer() {
		if (_writer instanceof StringWriter) {
			((StringWriter)_writer).getBuffer().setLength(0);
		} else if (_stream instanceof ByteArrayOutputStream) {
			((ByteArrayOutputStream)_stream).reset();
		}
	}

	public void sendRedirect(String location) throws IOException {
		super.sendRedirect(location);
		_bSendRedirect = true;
	}

	//deprecated//
	/**
	 * @deprecated
	 */
	public String encodeRedirectUrl(String url) {
		return super.encodeRedirectUrl(url);
	}
	/**
	 * @deprecated
	 */
	public String encodeUrl(String url) {
		return super.encodeUrl(url);
	}
	/**
	 * @deprecated
	 */
	public void setStatus(int sc, String sm) {
		super.setStatus(sc, sm);
	}
}
