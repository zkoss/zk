/* LabelElement.java

{{IS_NOTE
	$Id: LabelElement.java,v 1.2 2006/02/27 03:55:17 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Jun 17 09:45:54     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html.impl;

import com.potix.lang.Objects;

/**
 * A HTML element with a label.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:55:17 $
 */
abstract public class LabelElement extends XulElement {
	/** The label. */
	private String _label = "";

	/** Returns the label (never null).
	 * <p>Default: "".
	 */
	public String getLabel() {
		return _label;
	}
	/** Sets the label.
	 * <p>If label is changed, the whole component is invalidate.
	 * Thus, you want to smart-update, you have to override this method.
	 */
	public void setLabel(String label) {
		if (label == null) label = "";
		if (!Objects.equals(_label, label)) {
			_label = label;
			invalidate(INNER);
		}
	}
}
