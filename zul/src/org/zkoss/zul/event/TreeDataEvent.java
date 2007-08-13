/* TreeDataEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 10 2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import org.zkoss.zul.TreeModel;

/**
 * Defines an event that encapsulates changes to a tree. 
 *
 * @author Jeff Liu
 */
public class TreeDataEvent {
	/** Identifies changing contents of nodes. */
	public static final int CONTENTS_CHANGED = 0;
    /** Identifies the addition of children to a node. */    
	public static final int NODE_ADDED = 1;
    /** Identifies the removal of children to a node. */   
	public static final int NODE_REMOVED = 2;

	private final TreeModel _model;
	private final int _type;
	private final int[] _indexes;
	private final Object _parent;

	/** Contructor.
	 *
	 * @param type one of {@link #CONTENTS_CHANGED},
	 * {@link #NODE_ADDED}, or {@link #NODE_REMOVED}.
	 * @param parent - the parent node that its children being modified .
	 * @param indexes - the indexes of children being modified.
	 */
	public TreeDataEvent(TreeModel model, int type, Object parent, int[] indexes) {
		if (model == null)
			throw new NullPointerException();
		_model = model;
		_type = type;
		_parent = parent;
		_indexes = indexes;
	}
	
	/** Returns the tree model that fires this event.
	 */
	public TreeModel getModel() {
		return _model;
	}
	
	/** Returns the event type. One of {@link #CONTENTS_CHANGED},
	 * {@link #NODE_ADDED}, or {@link #NODE_REMOVED}.
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
	 * Returns the indexes of children being modified.
	 * @return the indexes of children being modified.
	 */
	public int[] getIndexes(){
		return _indexes;
	}

}
