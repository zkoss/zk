/* Marshaller.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov 12 10:57:59     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.device.marshal;

/**
 * A marshaller used to marshall data between the client and the server.
 * A different device ({@link org.zkoss.zk.device.Device}) might have
 * a difference marshaller.
 *
 * @author tomyeh
 * @since 5.0.0
 * @see org.zkoss.zk.device.Device#getMarshaller
 */
public interface Marshaller {
	/** Marshall an Object to an intermediate format that
	 * can be transfer to the client.
	 *
	 * <p>To marshal a primitive, such as boolean, you can use
	 * {@link #marshal(boolean)} or wrap it with {@link $boolean}
	 * and then invoke {@link #marshal(Object)}.
	 * The later is convenient if you have an array of daa that
	 * could be objects and primitives.
	 *
	 * @param o the object to marshal. What kid of objects can be
	 * passed depends on the device's capability.
	 * However, the following objects are supporteed by all kind of
	 * devices: null, a String instance, a Date instance,
	 * a BigDecimal instance, a BigInteger instance, the wrapper
	 * class of all primitive objects, an array of these kind of
	 * objects, and an array of primitives (boolean, int...).
	 */
	public String marshal(Object o);
	/** Marshall a boolean to an intermediate format that
	 * can be transfer to the client.
	 */
	public String marshal(boolean v);
	/** Marshall an integer to an intermediate format that
	 * can be transfer to the client.
	 */
	public String marshal(int v);
	/** Marshall a long to an intermediate format that
	 * can be transfer to the client.
	 */
	public String marshal(long v);
	/** Marshall a byte to an intermediate format that
	 * can be transfer to the client.
	 */
	public String marshal(byte v);
	/** Marshall a short to an intermediate format that
	 * can be transfer to the client.
	 */
	public String marshal(short v);
	/** Marshall a character to an intermediate format that
	 * can be transfer to the client.
	 */
	public String marshal(char v);
	/** Marshall a float to an intermediate format that
	 * can be transfer to the client.
	 */
	public String marshal(float v);
	/** Marshall a double to an intermediate format that
	 * can be transfer to the client.
	 */
	public String marshal(double v);
}
