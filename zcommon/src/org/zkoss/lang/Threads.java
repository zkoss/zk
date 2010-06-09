/* Threads.java

	Purpose:
		
	Description:
		
	History:
		Sat Feb 21 23:12:06     2004, Created by tomyeh

Copyright (C) 2003 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

import org.zkoss.mesg.MCommon;
import org.zkoss.util.logging.Log;

/**
 * Thread relevant utilities.
 *
 * @author tomyeh
 */
public class Threads {
	private static final Log log = Log.lookup(Threads.class);

	/** Put the current thread to sleep for a while.
	 * @exception SystemException if it is interrupted.
	 * @since 3.0.0
	 */
	public static final void sleep(int millisecs) {
		try {
			Thread.sleep(millisecs);
		} catch (InterruptedException ex) {
			throw SystemException.Aide.wrap(ex);
		}
	}
	/** Put the current thread to sleep for a while.
	 * @deprecated As of release 3.0.0, replaced by {@link #sleep}
	 */
	public static final void pause(int millisecs) {
		sleep(millisecs);
	}

	/** Sets the priority without throwing any exception but log warning.
	 */
	public static final void setPriority(Thread thd, int priority) {
		try {
			thd.setPriority(priority);
		}catch(Exception ex) {
			log.warningBriefly("Unable to change priority to "+priority, ex);
		}
	}
	/** Sets the priority without throwing any exception but log warning.
	 */
	public static final void setDaemon(Thread thd, boolean daemon) {
		try {
			thd.setDaemon(daemon);
		}catch(Exception ex) {
			log.warningBriefly("Unable to set DAEMON", ex);
		}
	}

	/** Waits a thread to die (and interrupt once a while)
	 * @param timeout how long to wait (0 means forever)
	 */
	public static final void joinAndInterrupt(Thread thd, int timeout)
	throws InterruptedException {
		if (timeout == 0)
			timeout = Integer.MAX_VALUE;

		final int PERIOD = 5000; //5 secs
		for (int j = 0; timeout > 0; timeout -= PERIOD) {
			thd.join(timeout > PERIOD ? PERIOD: timeout);
			if (!thd.isAlive())
				return; //done;

			try {
				thd.interrupt(); //just in case
			} catch (Throwable e2) { //ignore it
			}

			if ((++j & 0x7) == 0)
				log.info("Wait another thread to die over "+j*PERIOD/1000+" seconds");
		}
	}

	/** A dummy function that is used to avoid compiler from optimizing
	 * statments around it.
	 */
	public static final void dummy(Object o) {
		new Integer(Objects.hashCode(o));
	}
}
