/* XMLResourcesLocator.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 23 15:47:23 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.util.resource;

import java.util.Enumeration;
import java.util.List;
import java.io.IOException;
import java.net.URL;

import org.zkoss.idom.Document;

/**
 * Represents a locator used to locate XML resources.
 *
 * @author tomyeh
 * @since 5.1.0
 */
public interface XMLResourcesLocator extends Locator {
	/** Returns an enumeration of resources.
	 * Unlike {@link #getDependentXMLResources}, it doesn't resolve the dependence
	 * among the resouces.
	 *
	 * @param name the resouce name, such as "metainfo/config.xml".
	 */
	public Enumeration getResources(String name) throws IOException;
	/** Returns a list of resources ({@link Resource}) after resolving
	 * the dependence.
	 * The resource is returned in the format of {@link Resource}
	 *
	 * <p>To resolve the dependence, it assumes each resource has two
	 * element whose name is identified by elName and elDepends.
	 * The elName element specifies the unique name of each resource.
	 * The elDepends element specifies a list of names of resources
	 * that this resource depends on. If not found, it assumes it could
	 * be loaded first.
	 *
	 * @param name the resouce name, such as "metainfo/config.xml".
	 * @param elName the element used to specify the name.
	 * @param elDepends the element used to specify the dependence.
	 * @return a list of {@link Resource} of the specified name.
	 */
	public List getDependentXMLResources(String name, String elName,
	String elDepends) throws IOException;

	/** An item of the list returned by {@link XMLResourcesLocator#getDependentXMLResources}.
	 * @since 5.1.0
	 */
	public static class Resource {
		/** The URL of the resource. */
		public final URL url;
		/** The content of the resource. */
		public final Document document;

		public Resource(URL url, Document document) {
			this.url = url;
			this.document = document;
		}
		//Object//
		public int hashCode() {
			return url.hashCode() ^ document.hashCode();
		}
		public boolean equals(Object o) {
			return o instanceof Resource && ((Resource)o).url.equals(url)
				&& ((Resource)o).document.equals(document);
		}
		public String toString() {
			return "[res: " + url + ']';
		}
	}
}
