package org.zkoss.zktest.http;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;

public class ZKTestServlet extends DHtmlLayoutServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String zktheme = request.getParameter("zktheme");
		boolean hasThemeSetting = !Strings.isBlank(zktheme);
		boolean isDefaultTheme = "default".equalsIgnoreCase(zktheme);
		if (hasThemeSetting) { // fixed for HtmlUnit issue
			Cookie cookie = new Cookie("zktheme", zktheme);
			cookie.setSecure(request.isSecure());
			String contextPath = request.getContextPath();
			if (Strings.isEmpty(contextPath)) contextPath = "/";
			cookie.setPath(contextPath);
			cookie.setMaxAge(isDefaultTheme ? 0 : -1); // delete cookie if default
			response.addCookie(cookie);
			response.sendRedirect(request.getRequestURL().toString());
			return;
		}
		super.doGet(request, response);
	}
}
