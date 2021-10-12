/* F86_ZK_4028Item.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 17 14:57:46 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.io.Serializable;

/**
 * @author rudyhuang
 */
public class F86_ZK_4028Item implements Serializable {
	private String text;
	private String iconClass;
	private String url;

	public F86_ZK_4028Item(String text, String iconClass, String url) {
		this.text = text;
		this.iconClass = iconClass;
		this.url = url;
	}

	public String getText() {
		return text;
	}

	public String getIconClass() {
		return iconClass;
	}

	public String getUrl() {
		return url;
	}
}
