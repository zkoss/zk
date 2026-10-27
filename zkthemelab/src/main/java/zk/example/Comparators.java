/* Comparators.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 29, 2010 9:34:23 AM , Created by Sam
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package zk.example;

/**
 * @author Sam
 *
 */
public class Comparators {
	public static RowDetailComparator ascRowDetailComparator = new RowDetailComparator(true);
	public static RowDetailComparator dscRowDetailComparator = new RowDetailComparator(false);
}
