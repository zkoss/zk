/* ProcessingInstruction.java


	Purpose: 
	Description: 
	History:
	2001/10/22 20:53:28, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Collections;
import java.io.IOException;

import org.zkoss.mesg.MCommon;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import static org.zkoss.lang.Generics.cast;
import org.zkoss.lang.SystemException;
import org.zkoss.util.Maps;
import org.zkoss.idom.impl.*;

/**
 * The iDOM processing instruction.
 *
 * @author tomyeh
 */
public class ProcessingInstruction extends AbstractItem
implements org.w3c.dom.ProcessingInstruction {
	/** The target. */
	protected String _target;
	/** The raw data. */
	protected String _rawData;

	/** Constructor.
	 */
	public ProcessingInstruction(String target, String data) {
		setTarget(target);
		setData(data);
	}
	/** Constructor.
	 */
	public ProcessingInstruction(String target, Map<String, String> data) {
		setTarget(target);
		setData(data);
	}
	/** Constructor.
	 */
	protected ProcessingInstruction() {
	}

	//-- ProcessingInstruction extras --//
	public final String getTarget() {
		return _target;
	}
	public final void setTarget(String target) {
		if (!Objects.equals(_target, target)) {
			Verifier.checkPITarget(target, getLocator());
			_target = target;
		}
	}

	public final String getData() {
		return _rawData;
	}
	public final void setData(String data) {
		if (data == null)
			data = "";

		_rawData = data;
	}
	/** Returns the parsed data in the form of Map (never null).
	 */
	public final Map<String, String> parseData() {
		return parseToMap(new LinkedHashMap<String, String>(), getData());
	}
	/**
	 * Sets the raw data with a data map.
	 * Each entry in the data map is a (name, value) pair.
	 *
	 * @exception org.zkoss.util.IllegalSyntaxException if name contains
	 * an invalid character: '=', ' ', '\'', '"'
	 */
	public final void setData(Map<String, String> data) {
		final String notAllowed = "= '\"";
		for (final Iterator it = data.keySet().iterator(); it.hasNext();) {
			final String key = (String)it.next();
			final int j = Strings.anyOf(key, notAllowed, 0);
			if (j < key.length()) { //found
				final char cc = key.charAt(j);
				throw new SystemException(MCommon.ILLEGAL_CHAR,
						cc + " (0x" + Integer.toHexString(cc) + ')');
			}
		}

		setData(Maps.toString(data, '"', ' '));
	}

	/**
	 * Parses the raw data into a map.
	 * Each entry in the data map is a (name, value) pair.
	 * This method will convert a value to a number, either Integer
	 * or Double, if appropriate.
	 *
	 * <p>Most of characters are considered as ordinary (like 'a'),
	 * exception '"', '='
	 *
	 * <p>Example, the string wil cause ("a12", Intger(12)),
	 * ("b+3", null), ("345", null), ("c6", "abc=125&3?5"):<br>
	 * a12 =12 b+3 345 c6=\t'abc=125&3?5'
	 *
	 * @return the map (never null)
	 * @exception org.zkoss.util.IllegalSyntaxException if syntax erros
	 */
	public static final Map<String, String> parseToMap(Map<String, String> map, String rawData) {
		if (rawData == null || rawData.trim().length() == 0) {
			if (map != null)
				return map;
			return Collections.emptyMap();
		}

		map = cast(Maps.parse(map, rawData, ' ', (char)1)); //both ' and "

		//&quot; and other are not processed by SAXHandler,
		//so we have to handle them here
		for (Map.Entry<String, String> me: map.entrySet()) {
			final String val = me.getValue();
			StringBuffer sb = null;
			for (int i = 0, j = 0, len = val.length();;) {
				int k = val.indexOf('&', j);
				if (k < 0) {
					if (sb != null)
						me.setValue(sb.append(val.substring(i)).toString());
					break;
				}

				int l = val.indexOf(';', k);
				if (l >= 0) {
					final char cc;
					final String code = val.substring(k + 1, l);
					if ("quot".equals(code)) {
						cc = '"';
					} else if ("amp".equals(code)) {
						cc = '&';
					} else if ("lt".equals(code)) {
						cc = '<';
					} else if ("gt".equals(code)) {
						cc = '>';
					} else {
						//TODO: handle &#nnn; and more
						j = l + 1;
						continue; //ignore it
					}

					if (sb == null) sb = new StringBuffer(len);
					sb.append(val.substring(i, k)).append(cc);
					i = j = l + 1;
				} else {
					j = k + 1;
				}
			}
		}

		return map;
	}

	//-- Item --//
	public final String getName() {
		return getTarget();
	}
	public final void setName(String name) {
		setTarget(name);
	}
	public final String getText() {
		return getData();
	}
	public final void setText(String text) {
		setData(text);
	}

	//-- Node --//
	public final short getNodeType() {
		return PROCESSING_INSTRUCTION_NODE;
	}

	//-- org.w3c.dom.ProcessingInstruction --//

	//-- Object --//
	public String toString() {
		StringBuffer sb = new StringBuffer(64)
			.append("[PI: ").append(_target);
		if (_rawData.length() > 0)
			sb.append(' ').append(_rawData);
		return sb.append(']').toString();
	}
}
