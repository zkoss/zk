/* Selectable.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 24, 2009 12:15:21 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.ext;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;

/**
 * @deprecated As of release 6.0.0, replaced with {@link ListSelectionModel}.
 * Indicate a data model that supports selection.
 * It is typically used with {@link ListModel}
 * and {@link Listbox}.
 * @author henrichen
 */
public interface Selectable extends ListSelectionModel {
}
