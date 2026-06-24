/* B104_ZK_6011Test.java

		Purpose:

		Description:

		History:
				Wed Jun 24 13:11:06 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * ZK-6011: an invalid POST AU request (no session, no desktop id) used to
 * return HTTP 200 with only a {@code ZK-Error: 410} header. Each check sends a
 * cookie-less POST to {@code /zkau} (like the reported {@code curl} repro):
 * <ul>
 * <li>a bare request without a desktop id is incomplete/invalid, so it must
 * return HTTP 467 "Incomplete request" - the same status the async-update path
 * already uses for a missing dtid - not a misleading 200;</li>
 * <li>a multipart request whose body carries no desktop id is equally
 * malformed, so it must also return 467;</li>
 * <li>a multipart AU request that DOES carry a desktop id in its body is a
 * genuine client whose session expired, so it must keep the HTTP 200 +
 * {@code ZK-Error} timeout signal rather than being rejected.</li>
 * </ul>
 */
public class B104_ZK_6011Test extends WebDriverTestCase {
	/** ZK's non-standard "Incomplete request" status (see DHtmlUpdateServlet). */
	private static final int SC_INCOMPLETE_REQUEST = 467;

	@Test
	public void test() throws Exception {
		// ensure the webapp is deployed and the ZUL page parses
		connect();
		waitResponse();

		final String auUri = getAddress() + "/zkau";

		// the reported repro: a bare cookie-less POST has no desktop id and is
		// not a recognizable AU request -> HTTP 467, not a misleading 200.
		assertEquals(SC_INCOMPLETE_REQUEST, postStatus(auUri, null, null),
				"An invalid AU request without a desktop id must return HTTP 467, not 200");

		// a multipart request whose body carries no dtid is equally malformed -> 467.
		assertEquals(SC_INCOMPLETE_REQUEST,
				postStatus(auUri, "multipart/form-data; boundary=zk6011", null),
				"A multipart AU request without a desktop id must return HTTP 467, not 200");

		// regression guard: a multipart AU request that DOES carry a dtid in its
		// body is a genuine client whose session expired -> keep the 200 timeout
		// signal, do NOT reject it.
		final String multipartBody = "--zk6011\r\n"
				+ "Content-Disposition: form-data; name=\"data\"\r\n\r\n"
				+ "dtid=z_zk6011\r\n"
				+ "--zk6011--\r\n";
		assertEquals(HttpURLConnection.HTTP_OK,
				postStatus(auUri, "multipart/form-data; boundary=zk6011", multipartBody),
				"A multipart AU request carrying a desktop id must keep the timeout signal (200)");
	}

	/**
	 * POSTs {@code body} (no cookies, so no session) to {@code url} with the given
	 * content type and returns the HTTP status. A {@code null} body posts an empty
	 * request.
	 */
	private static int postStatus(String url, String contentType, String body) throws IOException {
		final HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
		try {
			http.setRequestMethod("POST");
			if (contentType != null)
				http.setRequestProperty("Content-Type", contentType);
			http.setDoOutput(true);
			final byte[] payload = body != null ? body.getBytes(StandardCharsets.UTF_8) : new byte[0];
			http.setFixedLengthStreamingMode(payload.length);
			try (OutputStream os = http.getOutputStream()) {
				if (payload.length > 0)
					os.write(payload);
			}
			return http.getResponseCode();
		} finally {
			http.disconnect();
		}
	}
}
