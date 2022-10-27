/* ActionData.java

	Purpose:

	Description:

	History:
		4:45 PM 2021/10/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import java.io.Serializable;

/**
 * Represents a data type that is caused by an action.
 * @author jumperchen
 */
public interface ActionData extends Serializable {
	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	String ARGUMENTS = "@args";

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	String METHOD = "@mtd";
}
