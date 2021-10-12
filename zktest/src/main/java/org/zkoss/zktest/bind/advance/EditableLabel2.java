/* EditableLabel2.java

		Purpose:
		
		Description:
		
		History:
				Fri May 07 16:25:28 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.zk.ui.annotation.ComponentAnnotation;

/**
 * The custom Component for testing ComponentAnnotation apply on getter.
 * @author leon
 */
public class EditableLabel2 extends EditableLabel {

	@ComponentAnnotation("@ZKBIND(ACCESS=both, SAVE_EVENT=onEdited)")
	public String getValue() {
		return super.getValue();
	}
}
