/* DataLoadingEvent.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 29, 2009 12:44:00 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import java.util.Map;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;

/**
 * Represent onDataLoading event.
 * 
 * @author henrichen
 * @since 5.0.0
 */
public class DataLoadingEvent extends Event {
	private final int _offset;
	private final int _limit;

	/** Converts an AU request to a data loading event.
	 */
	public static final DataLoadingEvent getDataLoadingEvent(AuRequest request, int preload) {
		final Map data = request.getData();
		return new DataLoadingEvent(request.getCommand(),
			request.getComponent(),
			AuRequests.getInt(data, "offset", 0), AuRequests.getInt(data, "limit", 20)+preload);
	}

	public DataLoadingEvent(String name, Component comp, int offset, int limit) {
		super(name, comp);
		_offset = offset;
		_limit = limit;
	}

	/** Returns the offset of the data chunk to be loaded.
	 * @return the  offset of the data chunk to be loaded.
	 */
	public int getOffset() {
		return _offset;
	}
	
	/**
	 * Returns the limit of the data chunk to be loaded.
	 * @return the limit of the data chunk to be loaded.
	 */
	public int getLimit() {
		return _limit;
	}
}
