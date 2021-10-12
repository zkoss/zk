/* B86_ZK_4146VM.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov 28 09:55:53 CST 2018, Created by jameschu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Init;

/**
 * @author jameschu
 */
public class B86_ZK_4146VM {
	private B86_ZK_4146Form formBean = null;
	@Init
	public void init() {
		formBean = new B86_ZK_4146Form();
	}

	public B86_ZK_4146Form getFormBean() {
		return formBean;
	}

	public void setForm(B86_ZK_4146Form form) {
		this.formBean = form;
	}
}
