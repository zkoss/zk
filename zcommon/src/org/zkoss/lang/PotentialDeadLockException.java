/* PotentialDeadLockException.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec  5 10:53:52     2003, Created by tomyeh

Copyright (C) 2003 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

/**
 * Denote a potential dead lock might occur.
 * More precisely, it occurs if a transaction is waiting a lock too long.
 *
 * <p>How it happens:<br>
 * Transaction A writes bean X and transaction B writes bean Y, and then
 * A tries to read or write bean Y and B tries to read or write bean X.
 *
 * <p>This exception shall be rare, because CmpManager sorted beans to
 * be updated in a special order. However, it still might happen, because
 * a transaction might trigger multiple syncBeans.
 *
 * @author tomyeh
 */
public class PotentialDeadLockException extends OperationException {
	public PotentialDeadLockException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public PotentialDeadLockException(String s) {
		super(s);
	}
	public PotentialDeadLockException(Throwable cause) {
		super(cause);
	}
	public PotentialDeadLockException() {
	}

	public PotentialDeadLockException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public PotentialDeadLockException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public PotentialDeadLockException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public PotentialDeadLockException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public PotentialDeadLockException(int code, Throwable cause) {
		super(code, cause);
	}
	public PotentialDeadLockException(int code) {
		super(code);
	}
}
