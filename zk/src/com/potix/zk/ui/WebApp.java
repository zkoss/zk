/* WebApp.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Dec  9 16:23:08     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.net.URL;
import java.io.InputStream;

import com.potix.util.resource.Locator;
import com.potix.zk.ui.util.Configuration;

/**
 * Represents a Web application.
 * A web application is usually in form of a war file.
 * An application might have multiple Web applications.
 *
 * <p>In HTTP, Web application represents a servlet context that supports ZK.
 * In other word, a Web application is created if
 * {@link com.potix.zk.ui.http.DHtmlLayoutServlet} is declared in web.xml.
 *
 * <p>To get the current Web application, use {@link Desktop#getWebApp}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface WebApp extends Locator {
	/** Returns the application name, never null.
	 * Developer can set it to any name that describes his application.
	 * <p>Default: ZK
	 */
	public String getAppName();
	/** Sets the applicationname.
	 * Developer can set it to any name that describes his application.
	 */
	public void setAppName(String name); 

	/** Returns the ZK version, such as "1.0.1" and "2.0.0".
	 */
	public String getVersion();

	/** Returns the value of the specified custom attribute.
	 */
	public Object getAttribute(String name);
	/** Sets the value of the specified custom attribute.
	 */
	public void setAttribute(String name, Object value);
	/** Removes the specified custom attribute.
	 */
	public void removeAttribute(String name);

	/** Returns a map of custom attributes associated with this object.
	 */
	public Map getAttributes();

	/** Returns the WebApp that corresponds to a specified URL on the server,
	 * or null if either none exists or the container wishes to restrict
	 * this access..
	 */
	public WebApp getWebApp(String uripath);

	/** Returns a URL to the resource that is mapped to a specified path.
	 */
	public URL getResource(String path);
	/** Returns the resource located at the named path as
	 * an InputStream object.
	 */
	public InputStream getResourceAsStream(String path);
	/** Returns a String containing the real path for a given virtual path.
	 * For example, the path "/index.html" returns the absolute file path
	 * on the server's filesystem would be served by a request for
	 * "http://host/contextPath/index.html", where contextPath is
	 * the context path of this {@link WebApp}.
	 */
	public String getRealPath(String path);

	/** Returns the MIME type of the specified file, or null if the MIME type
	 * is not known. The MIME type is determined by the configuration of
	 * the Web container.
	 * <p>Common MIME types are "text/html" and "image/gif".
	 */
	public String getMimeType(String file);

	/** Returns a directory-like listing of all the paths to resources
	 * within the web application whose longest sub-path matches the
	 * supplied path argument. Paths indicating subdirectory paths
	 * end with a '/'. The returned paths are all relative to
	 * the root of the web application and have a leading '/'.
	 * For example, for a web application containing
<pre><code>
/welcome.html
/catalog/index.html
/catalog/products.html
/catalog/offers/books.html
/catalog/offers/music.html
/customer/login.jsp
/WEB-INF/web.xml
/WEB-INF/classes/com.acme.OrderServlet.class,

getResourcePaths("/") returns {"/welcome.html", "/catalog/", "/customer/", "/WEB-INF/"}
getResourcePaths("/catalog/") returns {"/catalog/index.html", "/catalog/products.html", "/catalog/offers/"}.
</code>
</pre>
	 */
	public Set getResourcePaths(String path);

	/** Returns the value of the named context-wide initialization parameter,
	 * or null if the parameter does not exist.
	 */
	public String getInitParameter(String name);
	/** Returns the names of the context's initialization parameters as
	 * an Iterator of String objects, or an empty Iterator if the context
	 * has no initialization parameters.
	 */
	public Iterator getInitParameterNames();

	/** Returns the configuration.
	 */
	public Configuration getConfiguration();

	/** Returns the native application context, or null if not available.
	 *
	 * <p>The returned object depends on the Web container.
	 * If it is based Java servlet container, an instance of
	 * javax.servlet.Servletcontext is returned.
	 */
	public Object getNativeContext();
}
