/* F86_ZK_3913VM.java

        Purpose:
                
        Description:
                
        History:
                Wed Apr 11 3:55 PM:29 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class F86_ZK_3913VM {
	private static final List<String> TYPES = Collections.unmodifiableList(Arrays.asList("CODE39", "CODE128", "CODE128A", "CODE128B",
			"CODE128C", "EAN13", "EAN8", "EAN5", "EAN2", "UPC", "ITF14", "ITF", "MSI", "MSI10", "MSI11", "MSI1010",
			"MSI1010", "MSI1110", "PHARMACODE", "CODABAR", "QR", "pdf417"));
	private String _value = "1234";
	private String _type = "CODE128";
	private String _height = "100px";
	private boolean _displayValue = false;
	private int _fontSize = 10;
	private int _barWidth = 2;

	public void init() {
	}

	public List<String> getTypes() {
		return TYPES;
	}

	public String getValue () {
		return _value;
	}

	public void setValue(String v) {
		_value = v;
	}

	public String getType() {
		return _type;
	}

	public void setType(String type) {
		_type = type;
	}

	public String getHeight() {
		return _height;
	}

	public void setHeight(String height) {
		_height = height;
	}

	public boolean getDisplayValue() {
		return _displayValue;
	}

	public void setDisplayValue(boolean displayValue) {
		_displayValue = displayValue;
	}

	public int getFontSize() {
		return _fontSize;
	}

	public void setFontSize(int fontSize) {
		_fontSize = fontSize;
	}

	public int getBarWidth() {
		return _barWidth;
	}

	public void setBarWidth(int barWidth) {
		_barWidth = barWidth;
	}

	@NotifyChange({"value"})
	@Command
	public void setValue123456789012() {
		setValue("123456789012");
	}

	@NotifyChange({"value"})
	@Command
	public void setValue1234() {
		setValue("1234");
	}

	@NotifyChange({"value"})
	@Command
	public void setValueZKWeb() {
		setValue("https://www.zkoss.org");
	}

	@NotifyChange({"height"})
	@Command
	public void changeHeight() {
		if (_height.equals("50px")) {
			setHeight("120px");
		} else if (_height.equals("120px")) {
			setHeight("80%");
		} else if (_height.equals("80%")) {
			setHeight("50%");
		} else {
			setHeight("50px");
		}
	}

	@NotifyChange({"displayValue"})
	@Command
	public void changeDisplayValue() {
		if (_displayValue) {
			setDisplayValue(false);
		} else {
			setDisplayValue(true);
		}
	}

	@NotifyChange({"fontSize"})
	@Command
	public void changeFontSize() {
		if (_fontSize == 10) {
			setFontSize(20);
		} else {
			setFontSize(10);
		}
	}

	@NotifyChange({"barWidth"})
	@Command
	public void changeBarWidth() {
		if (_barWidth == 2) {
			setBarWidth(1);
		} else {
			setBarWidth(2);
		}
	}
}
