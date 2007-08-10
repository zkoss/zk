package org.zkoss.zul.event;

import org.zkoss.zul.TreeModel;

/**
 * @author Jeff
 *
 */
public class TreeDataEvent {
	/** Identifies one or more changes in the lists contents. */
	public static final int CONTENTS_CHANGED = 0;
    /** Identifies the addition of one or more contiguous items to the tree. */    
	public static final int NODE_ADDED = 1;
    /** Identifies the removal of one or more contiguous items from the tree. */   
	public static final int NODE_REMOVED = 2;

	private final TreeModel _model;
	private final int _type;
	private final int _index;
	private final Object _node;

	/** Contructor.
	 *
	 * @param type one of {@link #CONTENTS_CHANGED},
	 * {@link #INTERVAL_ADDED}, or {@link #INTERVAL_REMOVED}.
	 * @param index0 the lower index of the change range.
	 * For simple element, index0 is the same as index1.
	 * -1 means the first element (the same as 0).
	 * @param index1 the upper index of the change range.
	 * -1 means the last element.
	 */
	public TreeDataEvent(TreeModel model, int type, Object node, int index) {
		if (model == null)
			throw new NullPointerException();
		_model = model;
		_type = type;
		_node = node;
		_index = index;
	}
	/** Returns the list model that fires this event.
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
	
	public Object getNode(){
		return _node;
	}
	
	public int getIndex(){
		return _index;
	}

}
