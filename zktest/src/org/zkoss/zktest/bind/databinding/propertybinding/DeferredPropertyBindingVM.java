/* DeferredPropertyBindingVM.java
	Purpose:

	Description:

	History:
		Fri May 07 16:30:21 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.databinding.propertybinding;

import org.zkoss.bind.annotation.Command;

/**
 * @author jameschu
 */
public class DeferredPropertyBindingVM {
	private String text1;
	private String text2;

	@Command
	public void doCmd() {

	}

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
	}
}
