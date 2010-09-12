/* ResourceCache.java

	Purpose:
		
	Description:
		
	History:
		Sun Sep 12 12:41:51 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.web.util.resource;

/**
 * Used to cache Servlet resources.
 * It is used with {@link ResourceCaches} and {@link ResourceLoader}.
 *
 * @author tomyeh
 * @since 6.0.0
 */
public class ResourceCache<V> extends org.zkoss.util.resource.ResourceCache<ResourceInfo, V> {
	/** Constructor.
	 * @param loader the loader to load resource
	 */
	public ResourceCache(ResourceLoader<V> loader) {
		super(loader);
	}
	/** Constructor.
	 * @param loader the loader to load resource
	 * @param initsz the initial size of the map
	 */
	public ResourceCache(ResourceLoader<V> loader, int initsz) {
		super(loader, initsz);
	}
}
