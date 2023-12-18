/* WebApp.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec  9 16:23:08     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.zkoss.util.resource.Locator;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.util.Configuration;

/**
 * Represents a Web application.
 * A web application is usually in form of a war file.
 * An application might have multiple Web applications.
 *
 * <p>In HTTP, Web application represents a servlet context that supports ZK.
 * In other word, a Web application is created if
 * {@link org.zkoss.zk.ui.http.DHtmlLayoutServlet} is declared in web.xml.
 *
 * <p>To get the current Web application, use {@link Desktop#getWebApp}.
 *
 * @author tomyeh
 */
public interface WebApp extends Scope, Locator {
	/** Returns the application name, never null.
	 * Developer can set it to any name that describes his application.
	 * <p>Default: ZK
	 */
	public String getAppName();

	/** Sets the application name.
	 * Developer can set it to any name that describes his application.
	 */
	public void setAppName(String name);

	/** Returns the ZK version, such as "1.1.0" and "2.0.0".
	 * @see #getSubversion
	 * @see org.zkoss.util.Utils#parseVersion
	 * @see org.zkoss.util.Utils#compareVersion
	 */
	public String getVersion();

	/** Returns the build identifier, such as 2007121316.
	 *
	 * <p>Each time ZK is built, a different build identifier is assigned.
	 * @since 3.0.1
	 */
	public String getBuild();

	/** Returns a portion of the version in an integer by specifying its index.
	 * For example, getSubversion(0) returns the so-called major version
	 * (2 in "2.4.0"), getSubversion(1) returns the so-called
	 * minor version (4 in "2.4.0"), and both getSubversion(2) and getSubversion(3)
	 * return 0.
	 *
	 * @param portion which portion of the version; starting from 0.
	 * If you want to retrieve the major version, specify 0.
	 * @since 3.0.0
	 * @see #getVersion
	 */
	public int getSubversion(int portion);

	/** Returns the value of the specified custom attribute.
	 */
	public Object getAttribute(String name);

	/** Sets the value of the specified custom attribute.
	 * @return the previous value if any (since ZK 5)
	 */
	public Object setAttribute(String name, Object value);

	/** Removes the specified custom attribute.
	 * @return the previous value if any (since ZK 5)
	 */
	public Object removeAttribute(String name);

	/** Returns a map of custom attributes associated with this object.
	 */
	public Map<String, Object> getAttributes();

	/** Returns the WebApp that corresponds to a specified URL on the server,
	 * or null if either none exists or the container wishes to restrict
	 * this access..
	 */
	public WebApp getWebApp(String uripath);

	/** Returns a URL to the resource that is mapped to a specified path.
	 *
	 * <p>Notice that, since 3.6.3, this method can retrieve the resource
	 * starting with "~./". If the path contains the wildcard ('*'),
	 * you can use {@link Execution#locate} to convert it to a proper
	 * string first.
	 */
	public URL getResource(String path);

	/** Returns the resource located at the named path as
	 * an InputStream object.
	 *
	 * <p>Notice that, since 3.6.3, this method can retrieve the resource
	 * starting with "~./". If the path contains the wildcard ('*'),
	 * you can use {@link Execution#locate} to convert it to a proper
	 * string first.
	 */
	public InputStream getResourceAsStream(String path);

	/** Returns a String containing the real path for a given virtual path.
	 * For example, the path "/index.html" returns the absolute file path
	 * on the server's filesystem would be served by a request for
	 * "http://host/contextPath/index.html", where contextPath is
	 * the context path of this {@link WebApp}.
	 *
	 * <p>Notice that ZK don't count on this method to retrieve resources.
	 * If you want to change the mapping of URI to different resources,
	 * override {@link org.zkoss.zk.ui.sys.UiFactory#getPageDefinition}
	 * instead.
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
	public Set<String> getResourcePaths(String path);

	/** Returns the value of the named context-wide initialization parameter,
	 * or null if the parameter does not exist.
	 */
	public String getInitParameter(String name);

	/** Returns the names of the context's initialization parameters as
	 * an iterable String objects (never null).
	 * @since 6.0.0
	 */
	public Iterable<String> getInitParameterNames();

	/** Returns the URI for asynchronous update.
	 * <p>Both {@link #getUpdateURI} and {@link Desktop#getUpdateURI}
	 * are encoded with {@link Execution#encodeURL}
	 * @see Desktop#getUpdateURI
	 * @exception NullPointerException if the current execution is not available
	 * @since 3.6.2
	 */
	public String getUpdateURI();

	/** Returns the URI for asynchronous update that can be encoded or
	 * not.
	 *
	 * @param encode whether to encode with {@link Execution#encodeURL}.
	 * It is the same as {@link #getUpdateURI()} if <code>encode</code> is true.
	 * @since 5.0.0
	 * @exception NullPointerException if the current execution is not available
	 * and encode is true.
	 */
	public String getUpdateURI(boolean encode);

	/** Returns the configuration.
	 */
	public Configuration getConfiguration();

	/** Returns the servlet context of this application.
	 * @since 6.0.0
	 */
	public ServletContext getServletContext();

	/** Writes the specified message to a servlet log file, usually an event log.
	 * The name and type of the servlet log file is specific to the servlet container.
	 * @since 6.0.0
	 */
	public void log(String msg);

	/** Writes an explanatory message and a stack trace for a given Throwable
	 * exception to the servlet log file. The name and type of the servlet
	 * log file is specific to the servlet container, usually an event log. 
	 * @since 6.0.0
	 */
	public void log(String msg, Throwable ex);

	/**
	 * Returns the URI for ZK resource.
	 * <p>Both {@link #getResourceURI} and {@link Desktop#getResourceURI} are encoded with {@link Execution#encodeURL}
	 * @see Desktop#getResourceURI
	 * @exception NullPointerException if the current execution is not available
	 * @since 9.5.0
	 */
	public default String getResourceURI() {
		return getUpdateURI();
	}

	/**
	 * Returns the URI for ZK resource that can be encoded or not.
	 * @param encode whether to encode with {@link Execution#encodeURL}.
	 * It is the same as {@link #getResourceURI()} if <code>encode</code> is true.
	 * @since 9.5.0
	 * @exception NullPointerException if the current execution is not available
	 * and encode is true.
	 */
	public default String getResourceURI(boolean encode) {
		return getUpdateURI(encode);
	}
}
