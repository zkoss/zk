/* TreeDataEvent.java

	Purpose:
		
	Description:
		
	History:
		Aug 10 2007, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import org.zkoss.zul.TreeModel;

/**
 * Defines an event that encapsulates changes to a tree. 
 *
 * @author Jeff Liu
 * @since 3.0.0
 */
public class TreeDataEvent {
	/** Identifies changing contents of nodes. */
	public static final int CONTENTS_CHANGED = 0;
	/** Identifies the addition of children to a node. */    
	public static final int INTERVAL_ADDED = 1;
	/** Identifies the removal of children to a node. */   
	public static final int INTERVAL_REMOVED = 2;
	/** Identifies the structure of the tree has changed. @since 5.0.6  */
	public static final int STRUCTURE_CHANGED = 3;
	/** Identifies the selection of the tree has changed. @since 6.0.0  */
	public static final int SELECTION_CHANGED = 4;
	/** Identifies the open status of the tree has changed. @since 6.0.0  */
	public static final int OPEN_CHANGED = 5;
	/** Identified the state of {@link org.zkoss.zul.ext.Selectable#isMultiple} is changed.
	 * @since 6.0.0
	 */
	public static final int MULTIPLE_CHANGED = 6;

	private final TreeModel _model;
	private final int _type;
	private final int _indexFrom;
	private final int _indexTo;
	private final int[] _nodePath;

	/** Contructor.
	 *
	 * @param type one of {@link #CONTENTS_CHANGED},
	 * {@link #INTERVAL_ADDED}, {@link #INTERVAL_REMOVED}, {@link #SELECTION_CHANGED},
	 * {@link #OPEN_CHANGED}, {@link #STRUCTURE_CHANGED} or {@link #MULTIPLE_CHANGED}.
	 * @param nodePath the path of the affected node.
	 * If {@link #CONTENTS_CHANGED}, {@link #INTERVAL_ADDED} or {@link #INTERVAL_REMOVED},
	 * it is the parent node. If {@link #SELECTION_CHANGED} or {@link #OPEN_CHANGED},
	 * it is the node being selected or opened.
	 * If {@link #STRUCTURE_CHANGED} or {@link #MULTIPLE_CHANGED}, it is null.
	 * @param indexFrom the lower index of the change range
	 * @param indexTo the upper index of the change range
	 */
	public TreeDataEvent(TreeModel model, int type,
	int[] nodePath, int indexFrom, int indexTo) {
		_model = model;
		_type = type;
		_nodePath = nodePath;
		_indexFrom = indexFrom;
		_indexTo = indexTo;
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
	 * Returns the path of the affected node.
	 * If {@link #CONTENTS_CHANGED}, {@link #INTERVAL_ADDED} or {@link #INTERVAL_REMOVED},
	 * it is the parent node. If {@link #SELECTION_CHANGED} or {@link #OPEN_CHANGED},
	 * it is the node being selected or opened.
	 * If {@link #STRUCTURE_CHANGED} or {@link #MULTIPLE_CHANGED}, it is null.
	 * @return the parent node that one of its children being modified 
	 */
	public int[] getPath(){
		return _nodePath;
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
