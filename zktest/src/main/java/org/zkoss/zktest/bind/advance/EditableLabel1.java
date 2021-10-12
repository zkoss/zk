/* EditableLabel1.java

		Purpose:
		
		Description:
		
		History:
				Fri May 07 16:18:02 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.zk.ui.annotation.ComponentAnnotation;

/**
 * The custom Component for testing ComponentAnnotation apply on class.
 * @author leon
 */
@ComponentAnnotation("value:@ZKBIND(ACCESS=both, SAVE_EVENT=onEdited)")
public class EditableLabel1 extends EditableLabel {
}
