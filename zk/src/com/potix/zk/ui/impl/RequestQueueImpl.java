/* RequestQueueImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 20 09:51:39     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import com.potix.lang.D;
import com.potix.lang.Objects;
import com.potix.util.logging.Log;
import com.potix.zk.au.AuRequest;
import com.potix.zk.au.Command;
import com.potix.zk.ui.sys.RequestQueue;

/**
 * An implementation of {@link RequestQueue} behaving as
 * a queue of {@link AuRequest}.
 * There is one queue for each desktop.
 *
 * <p>Implementation Note:
 * Unlike only of desktop members, this class must be thread-safe.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class RequestQueueImpl implements RequestQueue {
	private static final Log log = Log.lookup(RequestQueueImpl.class);

	/** A list of pending {@link AuRequest}. */
	private final List _requests = new LinkedList();
	/** The in-process flag to denote Whether this queue is being processed
	 * by an execution.
	 */
	private boolean _process;

	//-- RequestQueue --//
	synchronized public boolean hasRequest() {
		_process = false;
		return !_requests.isEmpty();
	}

	synchronized public AuRequest nextRequest() {
		//if (D.ON && log.finerable()) log.finer("Next req "+_requests);
		if (_requests.isEmpty()) {
			_process = false;
			return null;
		}
		return (AuRequest)_requests.remove(0);
	}

	synchronized public void setInProcess() {
		_process = true;
	}

	synchronized public boolean addRequests(Collection requests) {
		for (Iterator it = requests.iterator(); it.hasNext();)
			addRequest((AuRequest)it.next());
		return _process;
	}
	private void addRequest(AuRequest request) {
		//if (D.ON && log.finerable()) log.finer("Arrive "+request+". Current "+_requests);

		//case 1, IGNORABLE: Drop any existent ignorable requests
		//We don't need to iterate all because requests is added one-by-one
		//In other words, if any temporty request, it must be the last
		{
			int last = _requests.size() - 1;
			if (last < 0) { //optimize the most common case
				_requests.add(request);
				return;
			}

			final AuRequest req2 = (AuRequest)_requests.get(last);
			final Command cmd2 = req2.getCommand();
			if ((cmd2.getFlags() & Command.IGNORABLE) != 0) {
				if (D.ON && log.debugable()) log.debug("Eat request: "+req2);
				_requests.remove(last); //drop it
				if (last == 0) {
					_requests.add(request);
					return;
				}
			}
		}

		//Case 2, CTRL_GROUP: drop new request if similar already exists
		final Command cmd = request.getCommand();
		final int flags = cmd.getFlags();
		if ((flags & Command.CTRL_GROUP) != 0) {
			for (Iterator it = _requests.iterator(); it.hasNext();) {
				final AuRequest req2 = (AuRequest)it.next();
				final Command cmd2 = req2.getCommand();
				if ((cmd2.getFlags() & Command.CTRL_GROUP) != 0) {
					if (D.ON && log.debugable()) log.debug("Eat request: "+request);
					return; //eat new
				}
			}

		//case 3, IGNORE_OLD_EQUIV: drop existent request if they are the same
		//as the arrival.
		} else if ((flags & Command.IGNORE_OLD_EQUIV) != 0) {
			final String uuid = request.getComponentUuid();
			for (Iterator it = _requests.iterator(); it.hasNext();) {
				final AuRequest req2 = (AuRequest)it.next();
				if (req2.getCommand() == cmd
				&& Objects.equals(req2.getComponentUuid(), uuid)) {
					if (D.ON && log.debugable()) log.debug("Eat request: "+req2);
					it.remove(); //drop req2
					break; //no need to iterate because impossible to have more
				}
			}

		//Case 4, IGNORE_IMMEDIATE_OLD_EQUIV: drop existent if the immediate
		//following is the same
		} else if ((flags & Command.IGNORE_IMMEDIATE_OLD_EQUIV) != 0) {
			final int last = _requests.size() - 1;
			final AuRequest req2 = (AuRequest)_requests.get(last);
			if (req2.getCommand() == cmd
			&& Objects.equals(req2.getComponentUuid(), request.getComponentUuid())) {
				if (D.ON && log.debugable()) log.debug("Eat request: "+req2);
				_requests.remove(last);
			}
		}

		_requests.add(request);
	}
}
