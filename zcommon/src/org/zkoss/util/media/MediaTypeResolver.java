/* MediaTypeResolver.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov 13 09:29:08 CST 2015, Created by wenning

Copyright (C) 2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.util.media;

/**
 * The MediaTypeResolver interface.
 *
 * <p>For using customized class for content-type
 * <p>ZK default will be used, if not implemented.
 * Specify the library property of <code>org.zkoss.util.media.MediaTypeResolverImpl.class</code>
 * in zk.xml to provide a customized class for content-type. (since 7.0.1)
 * <p>Should first specify the library property of <a href="http://books.zkoss.org/wiki/ZK%20Configuration%20Reference/zk.xml/The%20Library%20Properties/org.zkoss.web.util.resource.dir">org.zkoss.web.util.resource.dir</a> in zk.xml, the files in this path will get content-type by the customized class.
 * <p>For example, if the value of <code>org.zkoss.web.util.resource.dir</code> in zk.xml is "/WEB-INF/cwr", and src in your zul should be set as "~./yourfile.jpg".
 * <p>it will load "yourfile.jpg" from "/WEB-INF/cwr", and will use your customized class for content-type.
 * @author wenning
 * @since 8.0.1
 */
public interface MediaTypeResolver {

	/** Returns the content-type of the specified format for the specified filename extension,
	 * or null if not found.
	 */
	public String resolve(String format);

}
