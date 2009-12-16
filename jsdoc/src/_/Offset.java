/* Offset.java

	Purpose:
		
	Description:
		
	History:
		Wed Dec  9 12:40:11 TST 2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package _;

/**
 * An offset is a two-element array, where the first element is the X coordinate, and the second the Y coordinate.
 * <p>For example, zDom.cmOffset(el); returns an offset.
 *
 * <pre><code>var ofs = zDom.cmOffset(el);
zk.debug("x,y = " + ofs[0] + "," + ofs[1]);</code></pre>
 *
 * @author tomyeh
 * @see Array
 */
public class Offset extends Array {
	private Offset() {}
}
