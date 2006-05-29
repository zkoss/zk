/* LabelLoaderImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep 21 10:56:09     2004, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.resource.impl;

import java.util.Map;

import com.potix.util.logging.Log;
import com.potix.util.resource.LabelLoader;
import com.potix.util.resource.LabelLocator;

/**
 * The simple implementation of the label manager.
 * It doesn't consult any i3-label.properties but simply returns
 * the class and field names.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.6 $ $Date: 2006/05/29 04:27:25 $
 */
public class LabelLoaderImpl implements LabelLoader {
	//-- LabelLoader --//
	public String getFieldLabel(Class klass, String field) {
		return field;
	}
	public String getClassLabel(Class klass) {
		return klass.getName();
	}
	public String getCompoundLabel(Class klass, String field) {
		return getClassLabel(klass) + '[' + field + ']';
	}
	public String getObjectLabel(Object o) {
		if (o == null)
			return "null";
		return getClassLabel(o.getClass()) + ' ' + o;
	}
	/** Default: always returns null. */
	public String getProperty(String ky) {
		return null;
	}
	/** Default: throw UnsupportedOperationException. */
	public void register(LabelLocator loader) {
		throw new UnsupportedOperationException("i3m2 only");
	}

	/** Default: throw UnsupportedOperationException. */
	public void reset() {
		throw new UnsupportedOperationException("i3m2 only");
	}
}
