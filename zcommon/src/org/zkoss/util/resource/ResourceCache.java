/* ResourceCache.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 08:59:12     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.resource;

import org.zkoss.lang.D;
import org.zkoss.lang.Library;
import org.zkoss.lang.PotentialDeadLockException;
import org.zkoss.lang.SystemException;
import org.zkoss.util.CacheMap;
import org.zkoss.util.logging.Log;
import org.zkoss.util.WaitLock;

/**
 * Used to cache resouces.
 * To use this class, you have to implement {@link Loader} and then
 * ResourceCache will use it to check whether a resource is gone,
 * modified and load the resource.
 *
 * <p>Unlike {@link CacheMap}, it is thread-safe.
 *
 * <p>The default check period depends on the libary propety called
 * org.zkoss.util.resource.checkPeriod (unit: second). If not specified, 5 seconds are assumed
 *
 * @author tomyeh
 */
public class ResourceCache<K, V> extends CacheMap<Object, Object> {
	private static final Log log = Log.lookup(ResourceCache.class);

	/** The loader. */
	protected final Loader<K, V> _loader;
	/** unit=milliseconds. */
	private int _checkPeriod;

	/** Constructor.
	 * @param loader the loader to load resource
	 */
	public ResourceCache(Loader<K, V> loader) {
		if (loader == null)
			throw new NullPointerException();
		_loader = loader;
		_checkPeriod = getInitCheckPeriod();
	}
	/** Constructor.
	 * @param loader the loader to load resource
	 * @param initsz the initial size of the map
	 */
	public ResourceCache(Loader<K, V> loader, int initsz) {
		super(initsz);
		if (loader == null)
			throw new NullPointerException();
		_loader = loader;
		_checkPeriod = getInitCheckPeriod();
	}
	private static int getInitCheckPeriod() {
		final int v = Library.getIntProperty("org.zkoss.util.resource.checkPeriod", 5);
		return v > 0 ? v * 1000: v;
	}

	/** Returns the loader.
	 */
	public Loader<K, V> getLoader() {
		return _loader;
	}
	/** Returns how often to check (unit=milliseconds).
	 * <p>Default: 5000
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
	@SuppressWarnings("unchecked")
	public V get(Object src) {
		WaitLock lock = null;
		for (;;) {
			Info ri = null;
			synchronized (this) {
				Object o = super.get(src);
				if (o instanceof WaitLock) {
					lock = (WaitLock)o;
				} else if (o != null) { //was loaded
					ri = (Info)o;
				} else {
					super.put(src, lock = new WaitLock());
					break; //then, load it
				}
			} //sync(this)

			//check whether cached is valid
			if (ri != null) {
				synchronized (ri) {
					if (ri.isValid())
						return ri.getResource(); //reuse cached
				}
				//invalid, so remove it (if not updated by others)
				synchronized (this) {
					if (super.get(src) == ri) super.remove(src);
				}
			} else if (!lock.waitUntilUnlock(300*1000)) { //5 minute
				final PotentialDeadLockException ex =
					new PotentialDeadLockException(
					"Unable to load from "+src+"\nCause: conflict too long.");
				log.warningBriefly(ex); //very rare, possibly a bug
				throw ex;
			}
		} //for (;;)

		//load it
		try {
			boolean cache;
			final Info ri = new Info((K)src);
			V resource = ri.getResource();

			if (resource instanceof Loader.Resource) {
				final Loader.Resource lr = (Loader.Resource)resource;
				resource = (V)lr.resource;
				cache = lr.cacheable;
			} else
				cache = resource != null;

			synchronized (this) {
				if (cache) {
					super.put(src, ri);
				} else {
					super.remove(src); //remove lock
				}
			}

			return resource;
		} catch (Throwable ex) {
			synchronized (this) {
				super.remove(src); //remove lock
			}
			throw SystemException.Aide.wrap(ex);
		} finally {
			lock.unlock();
		}
	}
	/** Don't use it.
	 * @exception UnsupportedOperationException if called
	 */
	public Object put(Object src, Object val) {
		throw new UnsupportedOperationException("Used only internally");
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
	private class Info {
		/** The source. */
		private final K _src;
		/** The result resource. */
		private V _resource;
		private long _lastModified;
		/* When to check lastModified again. */
		private long _nextCheck;

		/**
		 * @param src the source
		 */
		public Info(K src) throws Exception {
			//if (D.ON && log.debugable()) log.debug("Loading from "+src);
			_src = src;
			load();
		}
		/** Returns the result resource.
		 */
		public final V getResource() {
			return _resource;
		}
		/** Quick check whether the page is still valid. */
		public boolean isValid() {
			final long now = System.currentTimeMillis();
			if (!_loader.shallCheck(_src, now - _nextCheck))
				return true;

			final long lastmod = _loader.getLastModified(_src);
			if (lastmod == -1) //removed or not support last-modified
				return false; //reload is required

			final boolean valid = lastmod == _lastModified;
			if (!valid)
				log.info("Source is changed: "+_src);
			//else if (D.ON && log.finerable())
			//	log.finer("Source not changed: "+_src);
			return valid;
		}
		/** Loads the file. */
		protected void load() throws Exception {
			_resource = _loader.load(_src);
			if (_resource != null) {
				_lastModified = _loader.getLastModified(_src);
				_nextCheck = System.currentTimeMillis() + _checkPeriod;
			}
		}
	}
}
