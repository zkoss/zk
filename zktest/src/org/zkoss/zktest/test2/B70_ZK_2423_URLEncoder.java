package org.zkoss.zktest.test2;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.zkoss.web.servlet.http.Encodes.URLEncoder;

public class B70_ZK_2423_URLEncoder implements URLEncoder {

	public String encodeURL(ServletContext ctx, ServletRequest request,
			ServletResponse response, String url, URLEncoder defaultEncoder)
					throws Exception {
		String encodedURL = defaultEncoder.encodeURL(ctx, request, response, url, defaultEncoder);
		if (request.toString().contains("B70-ZK-2423.zul")) {
			if(encodedURL.contains("?")) {
				encodedURL += "&token=abcdef";
			} else {
				encodedURL += "?token=abcdef";
			}
		}
		return encodedURL;
	}
}