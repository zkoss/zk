/* ForEachBranchInfo.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul  7 17:22:35 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.impl.Utils;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.ui.util.ForEach;
import org.zkoss.zk.ui.util.ForEachImpl;

/**
 * Used to implement a branch node that supports forEach.
 * @author tomyeh
 * @since 5.5.0
 */
/*package*/ abstract class ForEachBranchInfo extends BranchInfo {
	/** The forEach content, i.e., what to iterate. */
	private ExValue[] _forEach;
	/** The forEach info: [forEachBegin, forEachEnd]. */
	private ExValue[] _forEachInfo;

	/*package*/ ForEachBranchInfo() {
	}
	/*package*/ ForEachBranchInfo(NodeInfo parent, ConditionImpl cond) {
		super(parent, cond);
	}
	/** Used only by {@link ComponentInfo#duplicate} to make a virtual copy.
	 */
	/*package*/ ForEachBranchInfo(ForEachBranchInfo from) {
		_forEach = from._forEach;
		_forEachInfo = from._forEachInfo;
	}

	/** Returns the forEach object if the forEach attribute is defined
	 * (or {@link #setForEach} is called).
	 *
	 * <p>If comp is not null, both pagedef and page are ignored.
	 * If comp is null, page must be specified.
	 *
	 * @param page the page. It is used only if comp is null.
	 * @param comp the component.
	 * @return the forEach object to iterate this info multiple times,
	 * or null if this info shall be interpreted only once.
	 */
	public ForEach resolveForEach(Page page, Component comp) {
		return _forEach == null ? null:
			comp != null ?
				ForEachImpl.getInstance(
					_evalr, comp, _forEach, _forEachInfo[0], _forEachInfo[1]):
				ForEachImpl.getInstance(
					_evalr, page, _forEach, _forEachInfo[0], _forEachInfo[1]);
	}
	/** Sets the forEach attribute, which is usually an expression.
	 * @param expr the expression to return a collection of objects, or
	 * null/empty to denote no iteration.
	 */
	public void setForEach(String expr, String begin, String end) {
		_forEach = Utils.parseList(expr, Object.class, false);
			//forEach="" means to iterate a single-element array and the value
			//is empty
		_forEachInfo = _forEach == null ? null:
			new ExValue[] {
				begin != null && begin.length() > 0 ?
					new ExValue(begin, Integer.class): null,
				end != null && end.length() > 0 ?
					new ExValue(end, Integer.class): null};
	}
	/** Returns whether the forEach condition is defined.
	 */
	public boolean withForEach() {
		return _forEach != null;
	}

}
