/* PageEvalRef.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep  5 10:59:39     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.xel.FunctionMapper;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.impl.SimpleEvaluator;

/**
 * The evaluator reference based on page definition.
 * Used by {@link PageDefinition} only.
 *
 * @author tomyeh
 * @since 3.0.0
 */
/*package*/ class PageEvalRef extends AbstractEvalRef
implements java.io.Serializable {
	private transient PageDefinition _pagedef;
	/** Used only if _pagedef == null. */
	private transient Evaluator _eval;
	/** The implementation of the expression factory.
	 * Used oly if _pagedef == null.
	 */
	private Class _expfcls;
	/** The function mapper for the evaluator.
	 * Used oly if _pagedef == null.
	 */
	private FunctionMapper _mapper;

	/*package*/ PageEvalRef(PageDefinition pagedef) {
		_pagedef = pagedef;
	}

	//EvaluatorRef//
	public Evaluator getEvaluator() {
		if (_pagedef != null)
			return _pagedef.getEvaluator();
		if (_eval == null)
			_eval = new SimpleEvaluator(_mapper, _expfcls);
		return _eval;
	}
	public PageDefinition getPageDefinition() {
		return _pagedef;
	}

	//Serializable//
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();
		s.writeObject(_pagedef != null ? _pagedef.getExpressionFactoryClass(): _expfcls);
		s.writeObject(_pagedef != null ? _pagedef.getTaglibMapper(): _mapper);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		_expfcls = (Class)s.readObject();
		_mapper = (FunctionMapper)s.readObject();
	}
}
