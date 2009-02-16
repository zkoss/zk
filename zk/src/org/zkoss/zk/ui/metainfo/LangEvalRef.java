/* LangEvalRef.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Sep  5 10:58:14     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.xel.Evaluator;

/**
 * The evaluator reference based on language definition.
 * Used by {@link LangaugeDefinition} only.
 *
 * @author tomyeh
 * @since 3.0.0
 */
/*package*/ class LangEvalRef extends AbstractEvalRef
implements java.io.Serializable {
	private transient LanguageDefinition _langdef;
	/*package*/ LangEvalRef(LanguageDefinition langdef) {
		_langdef = langdef;
	}

	//EvaluatorRef//
	public Evaluator getEvaluator() {
		return _langdef.getEvaluator();
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		s.writeObject(_langdef != null ? _langdef.getName(): null);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		final String langnm = (String)s.readObject();
		if (langnm != null)
			_langdef = LanguageDefinition.lookup(langnm);
	}
};
