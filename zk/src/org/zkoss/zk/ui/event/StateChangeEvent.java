/* StateChangeEvent.java

		Purpose:
		
		Description:
		
		History:
				Thu May 27 11:07:59 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.Component;

/**
 * Represents an event that state of component was changed.
 *
 * @author leon
 * @since 9.6.0
 */
public class StateChangeEvent extends Event {
	
	private int _state;
	
	/**
	 * Constructs the state change event.
	 *
	 * @param state the state being changed.
	 */
	public StateChangeEvent(String name, Component target, Integer state) {
		super(name, target, state);
		_state = state;
	}
	
	/**
	 * Returns the current state that has been changed.
	 *
	 * @return the current state.
	 */
	public int getState() {
		return _state;
	}
}
