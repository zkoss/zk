/* WaitLock.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar  2 10:55:54     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import org.zkoss.lang.SystemException;
import org.zkoss.lang.PotentialDeadLockException;

/**
 * A simple lock used to implement load-on-deman mechanism.
 * Typical use: a thread, say A, checks whether a resource is loaded, and
 * put a WaitLock instance if not loaded yet. Then, another thread, say B,
 * if find WaitLock, it simply calls {@link #waitUntilUnlock} to wait.
 * Meanwhile, once A completes the loading, it put back the resouce
 * and calls {@link #unlock}.
 *
 * <pre><code>WaitLock lock = null;
for (;;) {
	synchronized (map) {
		Object o = map.get(key);
		if (o == null) {
			map.put(key, lock = new WaitLock());
			break; //go to load resource
		}
	}
	if (o instanceof MyResource)
	 	return (MyResource)o;
	if (!((Lock)o).waitUntilUnlock(60000))
	 	log.waring("Takes too long");
}
//load resource
try {
	....
	synchronized (map) {
		map.put(key, resource);
	}
	return resource;
} catch (Throwable ex) {
	synchronized (map) {
		map.remove(key);
	}
	throw SystemException.Aide.wrap(ex);
} finally {
	lock.unlock();
}
</code></pre>
 *
 * Refer to i3sys's SketchPostImpl, zweb's JspLoaderServlet for examples.
 *
 * @author tomyeh
 */
public class WaitLock {
	private final Thread _locker = Thread.currentThread();
	private boolean _unlocked;

	/** Once created, it is default to be locked.
	 * In other words, other thread's invocation of {@link #waitUntilUnlock}
	 * won't return until {@link #unlock} is called.
	 */
	public WaitLock() {
	}

	/** Waits this lock to unlock.
	 *
	 * @return whether it is unlocked successfully
	 * @exception SystemException if this thread is interrupted
	 * @exception PotentialDeadLockException if the thread itself creates
	 * this lock. In other words, it tried to wait for itself to complete.
	 */
	synchronized public boolean waitUntilUnlock(int timeout) {
		if (!_unlocked) {
			if (Thread.currentThread().equals(_locker))
				throw new PotentialDeadLockException("Wait for itself?");
			try {
				this.wait(timeout);
			} catch (InterruptedException ex) {
				throw SystemException.Aide.wrap(ex);
			}
		}
		return _unlocked;
	}
	/** Unlocks any other threads blocked by {@link #waitUntilUnlock}.
	 */
	synchronized public void unlock() {
		_unlocked = true;
		this.notifyAll(); //wake up all pending
	}
}
