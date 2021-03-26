package org.zkoss.zktest.test2;

import org.zkoss.web.servlet.http.Encodes.URLEncoder;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * @author bob peng
 */
public class B85_ZK_3620_CustomUrlEncoder implements URLEncoder {
	@Override
	public String encodeURL(ServletContext ctx, ServletRequest req, ServletResponse res, String url,
		URLEncoder defaultEncoder) throws Exception {
		if (req.toString().contains("B85-ZK-3620.zul")) {
			url += "?test=test";
		}
		return defaultEncoder.encodeURL(ctx, req, res, url, defaultEncoder);
	}
}
