/* MyBinder.java
	Purpose:

	Description:

	History:
		Tue May 04 11:10:38 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.databinding.binder;

import org.zkoss.bind.AnnotateBinder;

/**
 * @author jameschu
 */
public class MyBinder extends AnnotateBinder {
	public MyBinder(String qname, String qscope) {
		super(qname, qscope);
	}

	public String getName() {
		return "Outer XYZ";
	}
}
