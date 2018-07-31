/* F85_ZK_3923VM.java

        Purpose:
                
        Description:
                
        History:
                Wed May 02 9:34 AM:37 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class F86_ZK_3923VM {

	private String _type = "code128,pdf417,ean,qr";

	private boolean _continuous = false;

	private int _interval = 1000;

	private boolean _enable = true;

	private String _height = "700px";

	public String getType() {
		return _type;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setContinuous(boolean continuous) {
		_continuous = continuous;
	}

	public boolean getContinuous() {
		return _continuous;
	}

	public int getInterval() {
		return _interval;
	}

	public void setInterval(int interval) {
		_interval = interval;
	}

	public boolean isEnable() {
		return _enable;
	}

	public void setEnable(boolean enable) {
		_enable = enable;
	}

	public String getHeight() {
		return _height;
	}

	public void setHeight(String height) {
		_height = height;
	}

	@Command
	@NotifyChange({"type"})
	public void changeType() {
		if (_type.equals("code128")) {
			setType("code128,pdf417,ean,qr");
		} else if (_type.equals("code128,pdf417,ean,qr")) {
			setType("qr");
		} else if (_type.equals("qr")) {
			setType("code128");
		}
	}

	@Command
	@NotifyChange({"continuous"})
	public void changeContinuous() {
		if (_continuous == false) {
			setContinuous(true);
		} else {
			setContinuous(false);
		}
	}

	@Command
	@NotifyChange({"interval"})
	public void changeInterval() {
		if (_interval == 1000) {
			setInterval(200);
		} else {
			setInterval(1000);
		}
	}

	@Command
	@NotifyChange({"enable"})
	public void changeEnable() {
		if (_enable) {
			setEnable(false);
		} else {
			setEnable(true);
		}
	}

	@Command
	@NotifyChange({"height"})
	public void changeHeight() {
		if (_height.equals("700px")) {
			setHeight("350px");
		} else {
			setHeight("700px");
		}
	}
}
