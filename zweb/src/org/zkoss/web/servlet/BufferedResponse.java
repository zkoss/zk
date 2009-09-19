/* BufferedResponse.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 17 14:02:29     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.io.PrintWriterX;
import org.zkoss.web.servlet.ServletOutputStreamWrapper;
import org.zkoss.web.servlet.http.HttpBufferedResponse;

/**
 * A servlet response that uses another writer or stream as
 * the output.
 *
 * @author tomyeh
 */
public class BufferedResponse extends ServletResponseWrapper {
	private Writer _writer;
	private OutputStream _stream;

	private PrintWriter _pwt;
	private ServletOutputStream _sos;

	/** Returns a buffered response with a writer, if writer is not null;
	 * or the original response if writer is null.
	 * If reponse is HttpServletResponse, {@link HttpBufferedResponse#getInstance}
	 * is returned.
	 * It is smart enough not to wrap the same writer twice.
	 */
	public static final ServletResponse
	getInstance(ServletResponse response, Writer writer) {
		if (response instanceof HttpServletResponse)
			return HttpBufferedResponse.getInstance(
				(HttpServletResponse)response, writer);

		if (writer != null
		&& (!(response instanceof BufferedResponse)
			|| ((BufferedResponse)response)._writer != writer))
			return new BufferedResponse(response, writer);
		return response;
	}
	/** Returns a buffered response with a output stream, if stream is not null;
	 * or the original response if stream is null.
	 * If reponse is HttpServletResponse, {@link HttpBufferedResponse#getInstance}
	 * is returned.
	 * It is smart enough not to wrap the same stream twice.
	 */
	public static final ServletResponse
	getInstance(ServletResponse response, OutputStream stream) {
		if (response instanceof HttpServletResponse)
			return HttpBufferedResponse.getInstance(
				(HttpServletResponse)response, stream);

		if (stream != null
		&& (!(response instanceof BufferedResponse)
			|| ((BufferedResponse)response)._stream != stream))
			return new BufferedResponse(response, stream);
		return response;
	}

	/** Constructs a buffered response with a writer. */
	private BufferedResponse(ServletResponse response,
	Writer writer) {
		super(response);
		if (writer == null)
			throw new NullPointerException("writer");
		_writer = writer;
	}
	/** Constructs a buffered response with an output stream. */
	private BufferedResponse(ServletResponse response,
	OutputStream stream) {
		super(response);
		if (stream == null)
			throw new NullPointerException("stream");
		_stream = stream;
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
				throw new UnsupportedOperationException("ServletOutputStream cannot wrap PrintWriter");
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
}
