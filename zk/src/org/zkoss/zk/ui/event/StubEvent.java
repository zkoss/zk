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
 * @since 6.0.0
 */
public class StubEvent extends Event {
	private final String _cmd, _uuid, _id;
	private final Map<String, Object> _data;

	/** Converts an AU request to a stub event.
	 */
	public static final StubEvent getStubEvent(AuRequest request) {
		final Component target = request.getComponent();
		final Map<String, Object> data = request.getData();
		final String uuid = request.getUuid();
		return new StubEvent("onStub", target, request.getCommand(),
			uuid, getId(target, uuid), data);
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
	String cmd, String uuid, String id, Map<String, Object> data) {
		super(name, target);
		_cmd = cmd;
		_uuid = uuid;
		_id = id;
		_data = data;
	}
	public StubEvent(StubEvent evt, Component target) {
		this(evt.getName(), target, evt._cmd, evt._uuid, evt._id, evt._data);
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
	/** Returns the UUID of the stub component sending the request.
	 */
	public String getUuid() {
		return _uuid;
	}
	/** Returns the data carried in the request.
	 * The content depends on the request ({@link AuRequest}).
	 */
	public Map<String, Object> getRequestData() {
		return _data;
	}
}
