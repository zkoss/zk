/* B80_ZK_2542VM.java

	Purpose:
		
	Description:
		
	History:
		10:00 AM 9/8/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

/**
 * @author jumperchen
 */
public class B80_ZK_2542VM {
	List s;
	public B80_ZK_2542VM() {
		int size = 100;
		s = new ArrayList(size);
		for (int i = 0; i < size; i++)
			s.add(i);
	}
	public void setChildren(List children) {}
	public List getChildren() {
		return s;
	}
	public void setConcatConverter(Converter c) {}
	public Converter getConcatConverter() { return new ConcatConverter();}
	class ConcatConverter implements Converter {
		public Object coerceToUi(Object val, Component comp, BindContext ctx) {
			return "";
		}
		public Object coerceToBean(Object val, Component comp, BindContext ctx) {
			return null;
		}
	}
}