/* WireEvent.java

	Purpose:
		
	Description:
		
	History:
		Jan 7, 2010 10:05:59 AM , Created by joy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * @author Joy Lo
 * @since 5.0.0
 */
public class WireEvent extends Event {

	private final String _sourceId, _targetId, _joints, _config, _domtargetId, _uuid;
	
	/** Converts an AU request to a wire event.
	 * @param desktop 
	 */
	public static final WireEvent getWireEvent(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, request);
		final Map data = request.getData();
		if (data.get("domtargetId") == null || data.get("uuid") == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});
		return new WireEvent(request.getCommand(), comp,
			(String)data.get("sourceId"), (String)data.get("targetId"),
			(String)data.get("joints"), (String)data.get("config"),
			(String)data.get("domtargetId"), (String)data.get("uuid"));
	}
	
	public WireEvent(String name, Component target, String sourceId, String targetId, String joints, 
			String config, String domtargetId, String uuid) {
		super(name, target);
		_sourceId = sourceId;
		_targetId = targetId;
		_joints = joints;
		_config = config;
		_domtargetId = domtargetId;
		_uuid = uuid;
	}
	/** Returns the sourceId of the component.
	 */
	public final String getSourceId() {
		return _sourceId;
	}
	/** Returns the targetId of the component.
	 */
	public final String getTargetId() {
		return _targetId;
	}
	/** Returns the joints of the component.
	 */
	public final String getJoints() {
		return _joints;
	}
	/** Returns the config of the component.
	 */
	public final String getConfig() {
		return _config;
	}
	/** Returns the domtargetId of the component.
	 */
	public final String getDomtargetId() {
		return _domtargetId;
	}
	/** Returns the uuid of the component.
	 */
	public final String getUuid() {
		return _uuid;
	}
}
