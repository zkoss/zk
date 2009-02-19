/* ScalableTimerInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Dec  5 14:22:11     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.Timer;

/**
 * Represents each timer created by {@link ScalableTimer}.
 *
 * @author tomyeh
 */
/*package*/ class ScalableTimerInfo {
	/*package*/ Timer timer;
	/*package*/ int count;
}
