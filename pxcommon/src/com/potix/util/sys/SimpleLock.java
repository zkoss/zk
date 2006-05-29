/* SimpleLock.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Apr 12 16:33:21     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.sys;

import com.potix.lang.PotentialDeadLockException;
import com.potix.lang.SystemException;
import com.potix.util.logging.Log;

/**
 * A simple lock for accessing a critical section.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class SimpleLock {
	private static final Log log = Log.lookup(SimpleLock.class);

	private Thread _owner;
	private int _cnt;

	/** Returns whether it is already locked by the current thread.
	 */
	synchronized public boolean isGranted() {
		return _owner != null && _owner.equals(Thread.currentThread());
	}
	/** Locks. Returns only if the lock is granted.
	 * It supports nested locking.
	 */
	synchronized public void lock() {
		final Thread curr = Thread.currentThread();
		for (;;) {
			if (_owner == null) {
				_owner = curr;
				_cnt = 1;
				return; //granted
			} else if (_owner.equals(curr)) {
				++_cnt;
				return; //granted;
			}
			try {
				wait(10*60000);
				if (_owner != null)
					log.warningBriefly(new PotentialDeadLockException("Waiting too long and try again."));
			} catch (InterruptedException ex) {
				throw SystemException.Aide.wrap(ex);
			}
		}
	}
	/** Unlocks.
	 */
	synchronized public void unlock() {
		final Thread curr = Thread.currentThread();
		if (_owner == null || !_owner.equals(curr))
			throw new SystemException("Unlock that you don't own");
		if (--_cnt == 0) {
			_owner = null;
			notify();
		}
	}
}
