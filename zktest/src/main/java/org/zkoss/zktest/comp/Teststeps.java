/* Teststeps.java
	Purpose:

	Description:

	History:
		Thu Mar 15 18:38:20 CST 2018, Created by jameschu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.comp;

import java.util.regex.Pattern;

/**
 * @author jameschu
 */
public class Teststeps extends org.zkoss.zul.Label {
	private String _name = "";
	private boolean _autoIncrementLineNumber = true;

	@Override
	public boolean isMultiline() {
		return true;
	}

	@Override
	public String getValue() {
		return coerceToDisplayLabel(super.getValue());
	}

	private String coerceToDisplayLabel(String label) {
		String[] contentArr = label.split("\n");
		StringBuilder result = new StringBuilder();
		int no = 0;
		Pattern noPattern = Pattern.compile("^([0-9]+\\.).*");
		for (String lineStr : contentArr) {
			lineStr = lineStr.trim();
			if (lineStr.length() == 0 || lineStr.toLowerCase().contains("@type"))
				continue;
			//remove jq("...")
			lineStr = lineStr.replaceAll("jq\\([^\\)]*\\)", "");
			//remove ( and )
			lineStr = lineStr.replaceAll("[\\(\\)]", "");
			//remove `xxx`
			lineStr = lineStr.replaceAll("`.*`", "");

			if (_autoIncrementLineNumber && !noPattern.matcher(lineStr).matches()) {
				no++;
				result.append(no).append(". ");
			}
			result.append(lineStr);
			result.append("\n");
		}
		return result.toString();
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public boolean isAutoIncrementLineNumber() {
		return _autoIncrementLineNumber;
	}

	public void setAutoIncrementLineNumber(boolean autoIncrementLineNumber) {
		_autoIncrementLineNumber = autoIncrementLineNumber;
	}

}
