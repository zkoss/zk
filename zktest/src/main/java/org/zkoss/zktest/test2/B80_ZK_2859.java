/* B80_ZK_2859.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 27 16:40:31 CST 2015, Created by wenning

Copyright (C) 2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.io.Serializable;

/**
 * 
 * @author wenning
 */
public class B80_ZK_2859 implements Serializable {

	private String id;

	private String label;

	public B80_ZK_2859(String id, String label) {
		this.id = id;
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

}
