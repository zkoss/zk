/* String.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov  3 16:16:58     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package _;

/**
 * A string.
 * Notice that, in JavaScript, there is no character.
 * <p>Refer to <a href="http://www.w3schools.com/jsref/jsref_obj_string.asp">JavaScript Reference</a>.
 */
public class String {
	/** Returns a copy of this string by converting dashes into a camel-case equivalent.
	 *
	 * <p>Example: 
	 * <pre><code>"foo-bar".$camel(); //returns "fooBar"</code></pre>
	 *
	 * @return the cameled copy of this string. 
	 */
	public String $camel();
	/** Returns a copy of the first character of a string by increasing its value.
	 * <p>For example, 'a'.$inc(2) is 'c' 
	 * (same as 'a' + 2 in Java) and 'z'.$inc(-1) is 'y'.
	 * @param diff number to increase 
	 * @return the increased coy of this string. 
	 * @see #$sub 
	 */
	public String $inc(int diff);
	/** Returns the difference between the first character of this string and
	 * the first character of the specified string.
	 * <p>For example, 'd'.$sub('c') is 1 (same as 'd' - 'c' in Java)
	 * @param cc a string to compare with 
	 * @return the difference between this string and the specified one. 
	 * @see #$inc
	 */
	public String $sub(String cc);
	/** Returns whether this string starts with the specified prefix. 
	 * @param prefix the prefix to test
	 * @see #endsWith
	 */
	public boolean startsWith(String prefix);
	/** Returns whether this string ends with the specified postfix.
	 * @param postfix the postfix to test 
	 * @see #startsWith
	 */
	public boolean endsWith(String postfix);
	/** Returns a copy of the string, with leading and trailing whitespace omitted. 
	 */
	public String trim();
}
