/* F90_ZK_WC_TextArea.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 14 12:46:35 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.HtmlWebComponent;
import org.zkoss.zk.ui.annotation.ClientEvent;
import org.zkoss.zk.ui.annotation.ClientPropertySync;
import org.zkoss.zk.ui.annotation.ClientTag;
import org.zkoss.zk.ui.event.Events;

/**
 * @author rudyhuang
 */
@ClientTag("textarea")
@ClientEvent(value = "onPaste", flags = AbstractComponent.CE_REPEAT_IGNORE, event = F90_ZK_WC_PasteEvent.class)
@ClientEvent("onChange")
@ClientEvent("onKeyUp")
@ClientEvent("onKeyDown")
@ClientEvent("onKeyPress")
@ClientEvent("onClick")
@ClientEvent("onDoubleClick")
@ClientEvent("onRightClick")
@ClientEvent("onMouseDown")
@ClientEvent("onMouseUp")
@ClientEvent("onMouseMove")
@ClientEvent("onMouseOut")
@ClientEvent("onMouseOver")
@ClientPropertySync(prop = "value", event = Events.ON_CHANGE)
public class F90_ZK_WC_TextArea extends HtmlWebComponent {
	public void setLabel(String label) {
		setDynamicProperty("title", label);
	}

	public String getLabel() {
		return (String) getDynamicProperty("title");
	}

	public void setStyle(String style) {
		setDynamicProperty("style", style);
	}

	public String getStyle() {
		return (String) getDynamicProperty("style");
	}

	public void setReadonly(boolean readonly) {
		setDynamicProperty("readonly", readonly ? "readonly" : null);
	}

	public boolean getReadonly() {
		return "readonly".equals(getDynamicProperty("readonly"));
	}

	public void setValue(String value) {
		setDynamicProperty("value", value);
	}

	public String getValue() {
		return (String) getDynamicProperty("value"); // FIXME: textarea value is not mapped right
	}

	public void selectAll() {
		invoke("select");
	}
}
