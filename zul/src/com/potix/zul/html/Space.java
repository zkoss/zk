/* Space.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Jan 14 00:40:57     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zk.ui.WrongValueException;

/**
 * Space is a {@link Separator} with the orient default to "horizontal".
 *
 * <p>In other words, &lt;space&gt; is equivalent to &lt;separator orient="horizontal"&gt;
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/05/29 04:28:27 $
 */
public class Space extends Separator {
	public Space() {
		try {
			setOrient("vertical");
		} catch (WrongValueException ex) {
			throw new InternalError(); //impossible
		}
	}
}
