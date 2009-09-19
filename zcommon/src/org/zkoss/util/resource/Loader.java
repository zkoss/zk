/* Loader.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 09:13:02     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.resource;

/**
 * A loader that could load a resource to another format.
 * It is mainly designed to work with {@link ResourceCache}.
 *
 * @author tomyeh
 */
public interface Loader {
	/** Returns whether to call {@link #getLastModified}.
	 * If false, it assumes the current cached content is up-to-date.
	 *
	 * @param expiredMillis how many milli-seconds are expired after the last
	 * check. In most cases, just return true if expiredMillis > 0
	 */
	public boolean shallCheck(Object src, long expiredMillis);
	/** Returns the last modified time, or -1 if reload is required or not exists.
	 */
	public long getLastModified(Object src);
	/** Loads the resource.
	 *
	 * <p>The returned resource could be anything. It will be returned
	 * by {@link ResourceCache#get}.
	 * However, if you want to have more control (e.g., whether to cache),
	 * you can return an instance
	 * of {@link Resource}. Then, the return value of {@link ResourceCache#get}
	 * will be {@link Resource#resource}.
	 *
	 * @return null if not found
	 * @exception Exception you might throw any exception which will be
	 * passed back to the caller of {@link ResourceCache#get}
	 */
	public Object load(Object src) throws Exception;

	/** Represents the more information about an object loaded by
	 * {@link Loader#load}.
	 *
	 * @since 5.0.0
	 */
	public class Resource {
		/** The real resource that shall be returned by {@link ResourceCache#get}.
		 */
		public final Object resource;
		/** Whether the resource shall be cached in {@link ResourceCache}.
		 */
		public final boolean cacheable;

		public Resource(Object resource, boolean cacheable) {
			this.cacheable = cacheable;
			this.resource = resource;
		}
	}
}
