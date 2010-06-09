/* Array.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov  3 16:57:14     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package _global_;

/**
 * An array is a special variable, whcih can hold more than one value.
 *
 * <p>In additions to <a href="http://www.w3schools.com/jsref/jsref_obj_array.asp">JavaScript Reference</a>
 * that JavaScript provide, ZK extends with the following methods.
 *
 * <p>To test if an object is an array, use <code>jq.isArray(obj)</code>
 * where obj can be null.
 *
 * @see Map
 * @see String
 * @see Dimension
 * @see Offset
 */
public class Array {
	/** Clones this array.
	 * <p>For example,
	 * <pre><code>var anotherArray = ary.$clone();</code></pre>
	 */
	public Array $clone();
	/** Returns the index in this list of the first occurrence of the
	 * specified element, or -1 if this list does not contain this element.
	 */
	public int $indexOf(Object o);
	/** Returns true if this list contains the specified element.
	 */
	public boolean $contains(Object o);
	/** Removes the specified object from this array. 
	 * @return whether the object is removed successfully.
	 */
	public boolean $remove(Object o);

	/** Returns whether obj is an array, the length is the same, and
	 * each element in obj is the same as each element of this array.
	 * It will compare recursively.
	 * <p>For example,
	 * <pre><code>[0, ["s"]].$equals([0, ["s"]]) //is true</code></pre>
	 * <p>Notice that == returns false in the above case. 
	 */
	public boolean $equals(Object o);

	private Array() {}
}
