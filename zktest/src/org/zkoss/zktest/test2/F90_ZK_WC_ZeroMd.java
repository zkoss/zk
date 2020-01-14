/* F90_ZK_WC_TextArea.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 14 12:46:35 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.HtmlWebComponent;
import org.zkoss.zk.ui.annotation.ClientEvent;
import org.zkoss.zk.ui.annotation.ClientTag;

@ClientTag("zero-md")
@ClientEvent("onZero-md-ready")
@ClientEvent("onZero-md-rendered")
@ClientEvent("onClick")
public class F90_ZK_WC_ZeroMd extends HtmlWebComponent {
	public void setSrc(String src) {
		setDynamicProperty("src", src);
	}

	public String getSrc() {
		return (String) getDynamicProperty("src");
	}

	public void setManualRender(boolean manualRender) {
		setDynamicProperty("manual-render", manualRender);
	}

	public boolean getManualRender() {
		return (boolean) getDynamicProperty("manual-render");
	}

	public void setCssUrls(String cssUrls) {
		setDynamicProperty("css-urls", cssUrls);
	}

	public String getCssUrls() {
		return (String) getDynamicProperty("css-urls");
	}

	public void setNoShadow(boolean noShadow) {
		setDynamicProperty("no-shadow", noShadow);
	}

	public boolean getNoShadow() {
		return (boolean) getDynamicProperty("no-shadow");
	}

	public void render() {
		invoke("render");
	}
}
