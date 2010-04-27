/* ResponseHeaderInfo.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 16:19:54 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.metainfo;

import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.impl.EvaluatorRef;

/**
 * Represents a setting of a response header.
 * The setting specified here will become the response's header.
 * <p>It is a bit confusing that {@link ResponseHeaderInfo} represents a setting of the response's header.
 * {@link HeaderInfo} represents a tag located in the header of the generated content of a page.
 * For example, if the client is a HTTP browser, then {@link ResponseHeaderInfo} is equivalent
 * to invoke {@link Execution#setResponseHeader} and 
 * {@link org.zkoss.zk.ui.sys.ExecutionCtrl#addHeader}.
 * And, {@link HeaderInfo} represents the &lt;link&gt;, &lt;meta&gt; and &lt;script&gt; HTML tags
 * 
 * <p>It is not serializable.
 *
 * @author tomyeh
 * @since 5.0.2
 */
public class ResponseHeaderInfo extends EvalRefStub
implements Condition {
	private final String _name;
	private final ExValue _value, _add;
	private final ConditionImpl _cond;

	/** Constructor.
	 *
	 * @param name the header's name, such as Refresh.
	 * @param value the header's value. It could contain EL expressions.
	 * It could be evaluated to a string, or a date ({@link Date}).
	 * @param add whether to add the header, or to set the header. It could contain EL expressions.
	 */
	public ResponseHeaderInfo(EvaluatorRef evalr, String name, String value, String add, ConditionImpl cond) {
		if (name == null || name.length() == 0 || value == null)
			throw new IllegalArgumentException();

		_name = name;
		_evalr = evalr;
		_value = new ExValue(value, Object.class);
		_add = add != null ? new ExValue(add, Boolean.class): null;
		_cond = cond;
	}
	/** Returns the response header's name.
	 */
	public String getName() {
		return _name;
	}
	/** Returns the value of the response header.
	 * <p>Notice that it does NOT invoke {@link #isEffective}, so the caller
	 * has to call it first.
	 * @return the value which is an instance of {@link Date} or {@link String}
	 * (and never null).
	 */
	public Object getValue(Page page) {
		final Object val = _value.getValue(_evalr, page);
		return val != null ? val instanceof Date ? val: val.toString(): "";
	}
	/** Returns whether to add the response header, rather than replace.
	 * <p>Notice that it does NOT invoke {@link #isEffective}, so the caller
	 * has to call it first.
	 */
	public boolean shallAdd(Page page) {
		final Boolean bAdd = _add != null ? (Boolean)_add.getValue(_evalr, page): null;
		return bAdd != null && bAdd.booleanValue();
	}

	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(_evalr, comp);
	}
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(_evalr, page);
	}
}
