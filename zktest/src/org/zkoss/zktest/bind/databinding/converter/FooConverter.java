/* FooConverter.java
	Purpose:

	Description:

	History:
		Mon May 10 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.databinding.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

/**
 * @author jameschu
 */
public class FooConverter implements Converter {
	private final String preStr = "Foo - ";

	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		final String valStr = (String) val;
		return preStr + valStr;
	}

	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
		final String valStr = (String) val;
		return valStr.startsWith(preStr) ? valStr.substring(6) : valStr;
	}
}
