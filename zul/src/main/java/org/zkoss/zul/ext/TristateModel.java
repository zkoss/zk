/* TristateModel.java

        Purpose:
                
        Description:
                
        History:
                2024/1/20 AM 09:29, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul.ext;

import java.util.Set;

/**
 * Indicate a model that supports tristate checkbox.
 * @author jumperchen
 * @since 10.0.0
 */
public interface TristateModel<E> {


	/** States of the tri-state selection. */
	enum State {
		UNSELECTED,
		SELECTED,
		PARTIAL
	}

	/**
	 * Returns the current partial selection.
	 */
	Set<E> getPartials();

	/**
	 * Returns whether the item is partially selected.
	 * @param item a data item
	 */
	boolean isPartial(E item);
}
