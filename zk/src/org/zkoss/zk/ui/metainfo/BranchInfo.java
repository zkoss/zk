/* BranchInfo.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul  7 09:14:47 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.metainfo;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.EvaluatorRef;

/**
 * Used to implement a branch node that allows children.
 * @author tomyeh
 * @since 6.0.0
 */
/*package*/ abstract class BranchInfo extends LeafInfo implements Condition {
	/** A list of a children ({@link NodeInfo}). */
	private List<NodeInfo> _children = new LinkedList<NodeInfo>();
	private ConditionImpl _cond;

	/*package*/ BranchInfo() {
	}
	/*package*/ BranchInfo(NodeInfo parent, ConditionImpl cond) {
		super(parent);
		_cond = cond;
	}
	/** Used only by {@link ComponentInfo#duplicate} to make a virtual copy.
	 */
	/*package*/ BranchInfo(BranchInfo from) {
		_children = from._children; //direct copy since it is 'virtual'
		_cond = from._cond;
	}

	/** Returns the effectiveness condition.
	 */
	public ConditionImpl getCondition() {
		return _cond;
	}
	/** Sets the effectiveness condition.
	 */
	public void setCondition(ConditionImpl cond) {
		_cond = cond;
	}
	/** Tests if the condition is set
	 */
	public boolean withCondition() {
		return _cond != null;
	}

	//NodeInfo//
	//@Override
	public void appendChild(NodeInfo child) {
		NodeInfo oldp = child.getParent();
		if (oldp != null)
			oldp.removeChild(child);

		_children.add(child);
		((LeafInfo)child).setParentDirectly(this); //except root, all are LeafInfo
		fixEvaluatorRefDown(child, _evalr);
	}
	/*package*/ static final
	void fixEvaluatorRefDown(NodeInfo child, EvaluatorRef evalr) {
		if (child instanceof LeafInfo)
			((LeafInfo)child)._evalr = evalr;

		final List<NodeInfo> children = child.getChildren();
		if (children != null) //it is null if this method is called in constructor
			for (NodeInfo c: children)
				fixEvaluatorRefDown(c, evalr);
	}
	//@Override
	public boolean removeChild(NodeInfo child) {
		if (child != null && _children.remove(child)) {
			((LeafInfo)child).setParentDirectly(null); //except root, all are LeafInfo
			fixEvaluatorRefDown(child, null);
			return true;
		}
		return false;
	}
	//@Override
	public List<NodeInfo> getChildren() {
		return _children;
	}

	//Condition//
	//@Override
	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(_evalr, comp);
	}
	//@Override
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(_evalr, page);
	}

	//Serializable//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		//fix parent
		for (Iterator it = _children.iterator(); it.hasNext();)
			((LeafInfo)it.next()).setParentDirectly(this);
	}
}
