/* B104_ZK_5776Test.java

		Purpose:

		Description:

		History:
				Wed May 13 22:08:54 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B104_ZK_5776Test extends WebDriverTestCase {
    @RegisterExtension
    public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/B104-ZK-5776.xml");

    // A plain JS resource served by ClassWebResource.web0() (no DSP extendlet)
    private static final String STATIC_JS = "/zkau/web/js/zk/ext/fetch.js";

    @Test
    public void testETagResponseHeader() throws IOException {
        // First request: server should include ETag in the response
        HttpURLConnection conn = openGet(STATIC_JS);
        try {
            assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            String etag = conn.getHeaderField("ETag");
            assertNotNull(etag, "ETag response header must be present when cache.etag=true");
            // The .js path returns HTTP 200 with a "<path> not found" stub for a missing
            // resource, so assert the real fetch.js polyfill was served - otherwise this
            // test would pass even if STATIC_JS were renamed/removed.
            assertTrue(String.valueOf(conn.getContentType()).contains("javascript"),
                    "served resource must have a JavaScript content type");
            assertTrue(readBody(conn).contains("ajaxFetch"),
                    "response body must be the real fetch.js polyfill, not a not-found stub");
        } finally {
            conn.disconnect();
        }
    }

    @Test
    public void testIfNoneMatchReturns304() throws IOException {
        // First request to obtain the ETag value
        String etag;
        HttpURLConnection first = openGet(STATIC_JS);
        try {
            assertEquals(HttpURLConnection.HTTP_OK, first.getResponseCode());
            etag = first.getHeaderField("ETag");
            assertNotNull(etag, "ETag must be present on first request");
        } finally {
            first.disconnect();
        }

        // Second request: send If-None-Match with the same ETag value
        // Server must respond with 304 Not Modified
        HttpURLConnection second = openGet(STATIC_JS);
        second.setRequestProperty("If-None-Match", etag);
        try {
            assertEquals(HttpURLConnection.HTTP_NOT_MODIFIED, second.getResponseCode(),
                    "Server must return 304 when If-None-Match matches ETag");
        } finally {
            second.disconnect();
        }
    }

    private HttpURLConnection openGet(String path) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(getAddress() + path).openConnection();
        conn.setRequestMethod("GET");
        // Prevent the JVM from sending its own If-None-Match / If-Modified-Since
        conn.setUseCaches(false);
        // Disable gzip so the served body can be read as plain text for verification.
        conn.setRequestProperty("Accept-Encoding", "identity");
        // Do not connect() here: callers must be able to set request headers
        // (e.g. If-None-Match) first; HttpURLConnection connects lazily on getResponseCode().
        return conn;
    }

    private String readBody(HttpURLConnection conn) throws IOException {
        try (InputStream in = conn.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
