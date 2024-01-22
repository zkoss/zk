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

	/**
	 * States of the tri-state selection designed to assist users in defining states effectively.
	 * This enum is meant to be used in conjunction with an interface that requires the definition
	 * of tri-state selection states.
	 * <p>This enum is designed to facilitate the implementation of interfaces where a tri-state selection
	 * is required. Utilizing these predefined states enables users to clearly define the selection status
	 * of nodes consistently and meaningfully.
	 */
	enum State {
		/**
		 * Represents the unselected state.
		 * Use this state when the node is explicitly not selected.
		 */
		UNSELECTED,
		/**
		 * Represents the selected state.
		 * Use this state when the node is explicitly selected.
		 */
		SELECTED,
		/**
		 * Represents the partial selection state.
		 * Use this state when the node is partially selected, meaning
		 * that it is in an intermediate state between fully selected and unselected.
		 */
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
