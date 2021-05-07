/* EditableLabel3.java

		Purpose:
		
		Description:
		
		History:
				Fri May 07 16:25:36 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.zk.ui.annotation.ComponentAnnotation;

/**
 * The custom Component for testing ComponentAnnotation apply on setter.
 * @author leon
 */
public class EditableLabel3 extends EditableLabel {

	@ComponentAnnotation("@ZKBIND(ACCESS=both, SAVE_EVENT=onEdited)")
	public void setValue(String value) {
		super.setValue(value);
	}
}