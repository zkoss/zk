/* PartialByteStream.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 23 12:03:38 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.web.servlet.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

/*package*/ class PartialByteStream extends ByteArrayOutputStream {
	private final int _from, _to;
	private int _ofs, _cnt;

	/*package*/ PartialByteStream(int from, int to) {
		super(4096);
		_from = from;
		_to = to;
	}

	/*package*/ void responseTo(HttpServletResponse response) throws IOException {
		//Note: after all content are written, _ofs is the total number
		//while _cnt the number of bytes being written.
		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		response.setContentLength(_cnt);

		int from = _from <= _ofs ? _from : _ofs - 1;
		int to = _to >= 0 && _to <= _ofs ? _to : _ofs - 1;
		response.setHeader("Content-Range", "bytes " + from + "-" + to + "/" + _ofs);

		writeTo(response.getOutputStream());
	}

	public synchronized void write(int b) {
		int ofs = _ofs++;
		if (ofs >= _from && (_to < 0 || ofs <= _to)) {
			++_cnt;
			super.write(b);
		}
	}

	public synchronized void write(byte[] b, int ofs, int len) {
		while (--len >= 0)
			write(b[ofs++]);
	}
}
