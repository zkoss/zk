/* ChildrenBindingConverterVM.java
	Purpose:

	Description:

	History:
		Mon May 10 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.databinding.converter;

import java.util.Arrays;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;

/**
 * @author jameschu
 */
public class ChildrenBindingConverterVM {
	private String data = "1,2,3,4,5,6,7";
	private Converter itemConverter = new Converter() {
		@Override
		public Object coerceToUi(Object val, Component component, BindContext ctx) {
			String dataStr = (String) val;
			return Arrays.asList(dataStr.split(","));
		}

		@Override
		public Object coerceToBean(Object val, Component component, BindContext ctx) {
			return val;
		}
	};

	public Converter getItemConverter() {
		return itemConverter;
	}

	public String getData() {
		return data;
	}

	@Command
	@NotifyChange("data")
	public void add() {
		data += ",New";
	}
}
