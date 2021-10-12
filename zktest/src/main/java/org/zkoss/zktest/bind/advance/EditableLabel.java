/* EditableLabel.java

		Purpose:
		
		Description:
		
		History:
				Fri May 07 15:01:25 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 * The custom Component for testing ComponentAnnotation.
 * @author leon
 */
public class EditableLabel extends HtmlMacroComponent {

	@Wire
	Textbox textbox;

	@Wire
	Label label;

	public EditableLabel() {
		setMacroURI("/bind/advance/editablelabel.zul");
		// force the template to be applied, and to wire members automatically
		compose();
	}

	public String getValue() {
		return textbox.getValue();
	}

	public void setValue(String value) {
		textbox.setValue(value);
		label.setValue(value);
	}

	@Listen("onClick=#label")
	public void doEditing() {
		textbox.setVisible(true);
		label.setVisible(false);
		textbox.focus();
		textbox.setValue("");
	}

	@Listen("onBlur=#textbox")
	public void doEdited() {
		label.setValue(textbox.getValue());
		textbox.setVisible(false);
		label.setVisible(true);
		Events.postEvent("onEdited", this, null);
	}
}
