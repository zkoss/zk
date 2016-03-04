/** FormFieldCleaner.java.

	Purpose:
		
	Description:
		
	History:
		12:42:36 PM Jan 14, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.proxy;

/**
 * A helper interface for form filed data to clean up.
 * @author jumperchen
 * @since 8.0.0
 */
public interface FormFieldCleaner {
	/**
	 * Returns the empty string.
	 * @return String empty
	 */
	public String getResetEmptyStringValue();

	/**
	 * Returns the null value
	 * @return Object null
	 */
	public Object getResetNullValue();

	/**
	 * Returns the default byte value. i.e. 0
	 * @return byte value
	 */
	public byte getResetByteValue();

	/**
	 * Returns the default short value. i.e. 0
	 * @return short value
	 */
	public short getResetShortValue();

	/**
	 * Returns the default int value. i.e. 0
	 * @return int value
	 */
	public int getResetIntValue();

	/**
	 * Returns the default long value. i.e. 0L
	 * @return long value
	 */
	public long getResetLongValue();

	/**
	 * Returns the default float value. i.e. 0.0f
	 * @return float value
	 */
	public float getResetFloatValue();

	/**
	 * Returns the default double value. i.e. 0.0d
	 * @return double value
	 */
	public double getResetDoubleValue();

	/**
	 * Returns the default boolean value. i.e. false
	 * @return boolean value
	 */
	public boolean getResetBooleanValue();

	/**
	 * Returns the default char value. i.e. '\u0000'
	 * @return char value
	 */
	public char getResetCharValue();
}
