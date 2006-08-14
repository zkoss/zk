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

		//case 1. Drop any existent temporaty requests
		//We don't need to iterate all because requests is added one-by-one
		//In other words, if any temporty request, it must be the last
		{
			int last = _requests.size() - 1;
			if (last < 0) { //optimize the most common case
				_requests.add(request);
				return;
			}

			final AuRequest req2 = (AuRequest)_requests.get(last);
			final AuRequest.Command cmd2 = req2.getCommand();
			if (cmd2 == AuRequest.ON_CHANGING || cmd2 == AuRequest.ON_SCROLLING
			|| cmd2 == AuRequest.GET_UPLOAD_INFO) {
				if (D.ON && log.debugable()) log.debug("Eat request: "+req2);
				_requests.remove(last); //drop it
				if (last == 0) {
					_requests.add(request);
					return;
				}
			}
		}

		//Case 2: drop new request if similar already exists
		final AuRequest.Command cmd = request.getCommand();
		if (cmd == AuRequest.ON_CLICK || cmd == AuRequest.ON_RIGHT_CLICK
		|| cmd == AuRequest.ON_OK || cmd == AuRequest.ON_CANCEL
		|| cmd == AuRequest.ON_CTRL_KEY
		|| cmd == AuRequest.ON_DOUBLE_CLICK) {
			for (Iterator it = _requests.iterator(); it.hasNext();) {
				final AuRequest req2 = (AuRequest)it.next();
				final AuRequest.Command cmd2 = req2.getCommand();
				if (cmd2 == AuRequest.ON_CLICK
				|| cmd2 == AuRequest.ON_OK || cmd2 == AuRequest.ON_CANCEL
				|| cmd2 == AuRequest.ON_CTRL_KEY) {
					if (D.ON && log.debugable()) log.debug("Eat request: "+request);
					return; //eat new
				}
			}

		//case 3: drop existent request if they are the same
		//ON_MOVE, ON_Z_INDEX: somehow it is similar to ON_CHANGE,
		//but, for better performance, we optimizes it out
		//--
		//ON_RENDER: zk_loaded is set only if replied from server, so
		//it is OK to drop if any follows -- which means users are
		//scrolling fast
		//--
		//ON_TIMER: it is easy to pipe a lot of pending requests,
		// if the listener spends too much time
		//--
		//ON_FOCUS, ON_BLUR
		} else if (cmd == AuRequest.ON_RENDER || cmd == AuRequest.ON_ERROR
		|| cmd == AuRequest.ON_MOVE || cmd == AuRequest.ON_Z_INDEX
		|| cmd == AuRequest.ON_TIMER
		|| cmd == AuRequest.ON_FOCUS || cmd == AuRequest.ON_BLUR
		|| cmd == AuRequest.ON_SORT || cmd == AuRequest.ON_BOOKMARK_CHANGED) {
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

		//Case 4. drop existent if the immediate following is the same
		} else if (cmd == AuRequest.ON_SELECT || cmd == AuRequest.ON_CHANGE
		|| cmd == AuRequest.ON_CHECK) {
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
