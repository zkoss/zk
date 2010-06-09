/* TreeDataEvent.java

	Purpose:
		
	Description:
		
	History:
		Aug 10 2007, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import org.zkoss.zul.TreeModel;

/**
 * Defines an event that encapsulates changes to a tree. 
 *
 * @author Jeff Liu
 * @since ZK 3.0.0
 */
public class TreeDataEvent {
	/** Identifies changing contents of nodes. */
	public static final int CONTENTS_CHANGED = 0;
    /** Identifies the addition of children to a node. */    
	public static final int INTERVAL_ADDED = 1;
    /** Identifies the removal of children to a node. */   
	public static final int INTERVAL_REMOVED = 2;

	private final TreeModel _model;
	private final int _type;
	private final int _indexFrom;
	private final int _indexTo;
	private final Object _parent;

	/** Contructor.
	 *
	 * @param type one of {@link #CONTENTS_CHANGED},
	 * {@link #INTERVAL_ADDED}, or {@link #INTERVAL_REMOVED}.
	 * @param parent - the parent node that its children being modified .
	 * @param indexFrom the lower index of the change range
	 * @param indexTo the upper index of the change range
	 */
	public TreeDataEvent(TreeModel model, int type, Object parent, int indexFrom, int indexTo) {
		if (model == null)
			throw new NullPointerException();
		checkInterval(indexFrom,indexTo,model.getChildCount(parent));
		_model = model;
		_type = type;
		_parent = parent;
		_indexFrom = indexFrom;
		_indexTo = indexTo;
	}
	
	/*
	 * Check the interval
	 */
	private static void checkInterval(int from, int to, int len) {
        if(from > to)
            throw new IllegalArgumentException("'from' should be less than or equal to 'to'. from: "+from+", to: "+to);
        if(from < 0)
            throw new ArrayIndexOutOfBoundsException("Out of bound. from : "+from);
    }
	
	/** Returns the tree model that fires this event.
	 */
	public TreeModel getModel() {
		return _model;
	}
	
	/** Returns the event type. One of {@link #CONTENTS_CHANGED},
	 * {@link #INTERVAL_ADDED}, or {@link #INTERVAL_REMOVED}.
	 */
	public int getType() {
		return _type;
	}
	
	/**
	 * Returns the parent node that one of its children being modified 
	 * @return the parent node that one of its children being modified 
	 */
	public Object getParent(){
		return _parent;
	}
	
	/**
	 * Return the lower index of the change range
	 * @return the lower index of the change range
	 */ 
	public int getIndexFrom(){
		return _indexFrom;
	}
	
	/**
	 * Return the upper index of the change range
	 * @return the upper index of the change range
	 */
	public int getIndexTo(){
		return _indexTo;
	}

}
