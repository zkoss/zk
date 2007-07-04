/* FormFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 15 14:58:13     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.factory;

import javax.microedition.lcdui.Form;

import org.xml.sax.Attributes;
import org.zkoss.zkmob.ZkComponent;
import org.zkoss.zkmob.ui.ZkDesktop;
import org.zkoss.zkmob.ui.ZkForm;

/**
 * A UiFactory that create a Form Ui component.
 *
 * @author henrichen
 */
public class FormFactory extends AbstractUiFactory {
	public FormFactory(String name) {
		super(name);
	}
	
	public ZkComponent create(ZkComponent parent, String tag, Attributes attrs, String hostURL) {
		final String id = attrs.getValue("id"); //id
		final String title = attrs.getValue("tt"); //title

		ZkForm form = new ZkForm(((ZkComponent)parent).getZkDesktop(), id, title);

		form.setItemStateListener(((ZkDesktop) parent).getItemStateListener());
		
		return form;
	}
}
