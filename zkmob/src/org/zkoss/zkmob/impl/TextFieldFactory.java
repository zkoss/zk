/* TextFieldFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 15, 2007 3:28:33 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.impl;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import org.xml.sax.Attributes;
import org.zkoss.zkmob.AbstractUiFactory;
import org.zkoss.zkmob.ZkComponent;
import org.zkoss.zkmob.ZkComponents;

/**
 * @author henrichen
 *
 */
public class TextFieldFactory extends AbstractUiFactory {
	public TextFieldFactory(String name) {
		super(name);
	}
	
	public ZkComponent create(ZkComponent parent, String tag, Attributes attrs, String hostURL) {
		final String id = attrs.getValue("id"); //label
		final String label = attrs.getValue("lb"); //label
		String text = attrs.getValue("tx"); //text
		if (text == null) text = "";
		final String constraintsStr = attrs.getValue("cs"); //constraints
		final String maxSizeStr = attrs.getValue("xs"); //maxSize
		final int maxSize = Integer.parseInt(maxSizeStr);
		if (text.length() > maxSize) text = text.substring(0, maxSize);
		final int constraints = Integer.parseInt(constraintsStr);
		final String onChange = attrs.getValue("on");
		final String onChanging = attrs.getValue("og");
		final Zk zk = ((ZkComponent)parent).getZk();
		
		final ZkTextField component = new ZkTextField(zk, id, label, text, maxSize, constraints
				, onChange == null ? null : new Boolean("t".equals(onChange))
				, onChanging == null ? null : new Boolean("t".equals(onChanging)));

		ZkComponents.applyItemProperties(parent, component, attrs);
		
		return component;
	}

}
