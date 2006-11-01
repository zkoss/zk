/* MultiBranch.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 17 14:02:55     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.render;

import org.zkoss.zk.ui.Component;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}
 * to denote a component might have several branches of elements at the client.
 * Then, ZK will call {@link #inDifferentBranch} to see whether a child
 * is NOT on the main branch.
 * 
 * @author tomyeh
 */
public interface MultiBranch {
	/** Whether the specified child is placed in different branch
	 * of the DOM tree (other than the main one).
	 * For example, you might put caption at a branch
	 * and the rest at another branch. Then, you shall return false
	 * for the caption. Thus, ZK knows how to render them correctly.
	 *
	 * <p>Note: for components, say caption, in different branch,
	 * you have to invalidate the parent when they are added or removed.
	 * Only children causing this method to return true are handled
	 * by ZK Update Engine.
	 */
	public boolean inDifferentBranch(Component child);
}
