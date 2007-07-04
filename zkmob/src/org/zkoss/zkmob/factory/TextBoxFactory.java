/* TextBoxFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 15, 2007 3:15:54 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.factory;

import javax.microedition.lcdui.TextBox;

import org.xml.sax.Attributes;
import org.zkoss.zkmob.ZkComponent;
import org.zkoss.zkmob.ui.ZkTextBox;

/**
 * An UiFactory that create a TextBox Ui component.
 * 
 * @author henrichen
 *
 */
public class TextBoxFactory extends AbstractUiFactory {
	public TextBoxFactory(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see org.zkoss.zmobi.UiFactory#create(java.lang.Object, java.lang.String, org.xml.sax.Attributes)
	 */
	public ZkComponent create(ZkComponent parent, String tag, Attributes attrs, String hostURL) {
		final String id = attrs.getValue("id"); //id
		final String title = attrs.getValue("tt"); //title
		final String constraintsStr = attrs.getValue("cs"); //constraints
		final String maxSizeStr = attrs.getValue("xs"); //maxSize
		final int maxSize = Integer.parseInt(maxSizeStr);
		String text = attrs.getValue("tx"); //text
		if (text.length() > maxSize) text = text.substring(0, maxSize);
		final int constraints = Integer.parseInt(constraintsStr);
		
		final ZkTextBox tbx = new ZkTextBox(((ZkComponent)parent).getZkDesktop(), id, title, text, maxSize, constraints);
		return tbx;
	}

}
