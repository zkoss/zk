/* Frame.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 22, 2007 6:06:11 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil;

import org.zkoss.zk.ui.IdSpace;

/**
 * A generic Frame.
 * 
 * <p>A Frame is an independent ID space (by implementing {@link IdSpace}).
 * It means a window and all its descendants forms a ID space and
 * the ID of each of them is unique in this space.
 * You could retrieve any of them in this space by calling {@link #getFellow}.</p>
 * 
 * @author henrichen
 */
public class Frame extends Displayable implements Screen, IdSpace {
	private static final long serialVersionUID = 200705221824L;
}
