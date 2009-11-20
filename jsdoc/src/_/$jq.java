/* $jq.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov 20 12:09:19     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package _;

/**
 * Represents the object returned by the <code>jq</code> function.
 * Notice that <code>jq</code> is the global variable representing
 * jQuery (aka., <code>$</code>).
 * Refer to <a href="package-summary.html">the package summary</a>
 * for more information.
 * It inherits all functionality provided by <a href="http://docs.jquery.com/Main_Page" target="jq">jQuery</a>.
 *
 * <p>Here is a list of the ZK extension to the object returned
 * by the <code>jq</code>function.
 *
 * <p>Other extension can be found as follows
 * <ul>
 * <li>{@link jq} - DOM utilities (such as, {@link jq#innerX}</li>
 * <li>{@link $zk} - ZK extension to {@link $jq}.</li>
 * <li>{@link _.jq.Event} - the event object passed to the event listener</li>
 * <li>{@link _.jq.event} - a collection of functions used to manipulate events.</li>
 * </ul>
 *
 * @author tomyeh
 */
public interface $jq {
}
