/* StubEvent.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 10 16:05:12 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.StubComponent;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;

/**
 * The event sent from a stub component ({@link org.zkoss.zk.ui.sys.StubComponent}.
 * <p>The target component ({@link #getTarget}) is the nearest non-stub
 * parent. While the stub component causes the event to be sent can be
 * found by use of {@link #getId}, if it is assigned with an ID.
 *
 * @author tomyeh
 * @since 5.5.0
 */
public class StubEvent extends Event {
	private final String _cmd, _id;
	private final Map _data;

	/** Converts an AU request to a stub event.
	 */
	public static final StubEvent getStubEvent(AuRequest request) {
		Component comp = request.getComponent(), target;
		for (target = comp; target != null && (target instanceof Native
		|| target instanceof StubComponent); target = target.getParent())
			;

		final Map data = request.getData();
		return new StubEvent("onStub", target, request.getCommand(),
			getId(comp, request.getUuid()), data);
	}
	private static final String getId(Component comp, String uuid) {
		if (comp instanceof StubComponent)
			return ((StubComponent)comp).getId(uuid);
		if (comp != null) {
			String id = comp.getId();
			if (id != null && id.length() > 0)
				return id;
		}
		return null;
	}

	/** Constructs a check-relevant event.
	 * @param id the ID of the stub component causes this event.
	 */
	public StubEvent(String name, Component target,
	String cmd, String id, Map data) {
		super(name, target);
		_cmd = cmd;
		_id = id;
		_data = data;
	}

	/** Returns the command of the AU request, such as onChange.
	 */
	public String getCommand() {
		return _cmd;
	}
	/** Returns the ID of the stub component sending the request,
	 * or null if not available.
	 */
	public String getId() {
		return _id;
	}
	/** Returns the data carried in the request.
	 * The content depends on the request ({@link AuRequest}).
	 */
	public Map getRequestData() {
		return _data;
	}
}
