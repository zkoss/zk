/* ResourceCache.java

{{IS_NOTE
	$Id: ResourceCache.java,v 1.11 2006/05/25 06:22:23 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 08:59:12     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.resource;

import com.potix.lang.D;
import com.potix.lang.PotentialDeadLockException;
import com.potix.lang.SystemException;
import com.potix.util.CacheMap;
import com.potix.util.logging.Log;
import com.potix.util.sys.WaitLock;

/**
 * Used to cache resouces.
 * To use this class, you have to implement {@link Loader} and then
 * ResourceCache will use it to check whether a resource is gone,
 * modified and load the resource.
 *
 * <p>Unlike {@link CacheMap}, it is thread-safe.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.11 $ $Date: 2006/05/25 06:22:23 $
 */
public class ResourceCache extends CacheMap {
	private static final Log log = Log.lookup(ResourceCache.class);

	/** The loader. */
	protected final Loader _loader;
	/** unit=milliseconds. */
	private int _checkPeriod = 5*1000;

	/** Constructor.
	 * @param loader the loader to load resource
	 */
	public ResourceCache(Loader loader) {
		if (loader == null)
			throw new NullPointerException();
		_loader = loader;
	}
	/** Constructor.
	 * @param loader the loader to load resource
	 * @param initsz the initial size of the map
	 */
	public ResourceCache(Loader loader, int initsz) {
		super(initsz);
		if (loader == null)
			throw new NullPointerException();
		_loader = loader;
	}

	/** Returns the loader.
	 */
	public Loader getLoader() {
		return _loader;
	}
	/** Returns how often to check (unit=milliseconds).
	 * <p>Default: 300000
	 */
	public int getCheckPeriod() {
		return _checkPeriod;
	}
	/** Sets how often to check (unit=milliseconds).
	 * @return this object
	 */
	public ResourceCache setCheckPeriod(int checkPeriod) {
		_checkPeriod = checkPeriod;
		return this;
	}
	
	//-- Map --//
	/** Returns the resource, or null if not found.
	 */
	public Object get(Object src) {
		WaitLock lock = null;
		for (;;) {
			synchronized (this) {
				Object o = super.get(src);
				if (o instanceof ResourceInfo) { //was loaded
					final ResourceInfo ri = (ResourceInfo)o;
					if (ri.isValid()) { //quick check
						final Object resource = ri.getResource();
						if (resource == null) //removed
							remove(src);
						return resource;
					}
					o = null; //reload
				}

				if (o == null) {
					put(src, lock = new WaitLock());
					break; //then, load it
				} else {
					lock = (WaitLock)o;
				}
			} //sync(this)

			if (!lock.waitUntilUnlock(300*1000)) { //5 minute
				final PotentialDeadLockException ex =
					new PotentialDeadLockException(
					"Unable to load from "+src+"\nCause: conflict too long.");
				log.warningBriefly(ex); //very rare, possibly a bug
				throw ex;
			}
		} //for (;;)

		//load it
		try {
			final ResourceInfo ri = new ResourceInfo(src);
			final Object resource = ri.getResource();
			synchronized (this) {
				if (resource != null) {
					put(src, ri);
				} else {
					remove(src); //remove lock
				}
			}

			return resource;
		} catch (Throwable ex) {
			synchronized (this) {
				remove(src); //remove lock
			}
			throw SystemException.Aide.wrap(ex);
		} finally {
			lock.unlock();
		}
	}
	/** Used only internally.
	 */
	public Object put(Object src, Object val) {
		if (!(val instanceof ResourceInfo) && !(val instanceof WaitLock))
			throw new UnsupportedOperationException("Don't put content directly.");
		return super.put(src, val);
	}
	/** It is OK to remove the resource if you don't want to cache it.
	 * It is thread safe.
	 */
	public Object remove(Object src) {
		synchronized (this) {
			return super.remove(src);
		}
	}
	/** It is OK to clear up all cached resources if you don't want to cache it.
	 * It is thread safe.
	 */
	public void clear() {
		synchronized (this) {
			super.clear();
		}
	}

	//-- private --//
	/** Providing info about a resource. */
	private class ResourceInfo {
		/** The source. */
		private final Object _src;
		/** The result resource. */
		private Object _resource;
		private long _lastModified;
		/* When to check lastModified again. */
		private long _nextCheck;

		/**
		 * @param src the source
		 */
		public ResourceInfo(Object src) throws Exception {
			if (D.ON && log.debugable()) log.debug("Loading from "+src);
			_src = src;
			load();
		}
		/** Returns the result resource.
		 */
		public final Object getResource() {
			return _resource;
		}
		/** Quick check whether the page is still valid. */
		public boolean isValid() {
			final long now = System.currentTimeMillis();
			if (now < _nextCheck)
				return true;
			final long lastmod = _loader.getLastModified(_src);
			if (lastmod == -1) {
				log.info("Source is removed: "+_src);
				_resource = null;
				return true; //yes (but with null _resource)
			}
			final boolean valid = lastmod == _lastModified;
			if (!valid)
				log.info("Source is changed: "+_src);
			else if (D.ON && log.finerable())
				log.finer("Source not changed: "+_src);
			return valid;
		}
		/** Loads the file. */
		protected void load() throws Exception {
			_resource = _loader.load(_src);
			_lastModified = _loader.getLastModified(_src);
			_nextCheck = System.currentTimeMillis() + _checkPeriod;
		}
	}
}
