/* SmartUpdater.java

	Purpose:

	Description:

	History:
		3:32 PM 2022/1/11, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui;

import java.util.Map;

/**
 * An interface to indicate the implementation can be serialized by a smart updater.
 * i.e. {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
 * @author jumperchen
 */
public interface SmartUpdater {
	/**
	 * Serializes the keys and values of the updater.
	 * @return A keys and values mapping for {@link UiAgent#smartUpdate}
	 */
	Map<String, Object> marshal();
}
