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
	private boolean _contScan = false;
	private double _scanRate = 1;
	private boolean _open = true;
	private String _height = "700px";
	
	public String getType() {
		return _type;
	}
	
	public void setType(String type) {
		_type = type;
	}
	
	public void setContScan(boolean contScan) {
		_contScan = contScan;
	}
	
	public boolean getContScan() {
		return _contScan;
	}
	
	public double getScanRate() {
		return _scanRate;
		
	}
	
	public void setScanRate(double scanRate) {
		_scanRate = scanRate;
	}

	public boolean isOpen() {
		return _open;
	}
	
	public void setOpen(boolean open) {
		_open = open;
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
	@NotifyChange({"contScan"})
	public void changeContScan() {
		if (_contScan == false) {
			setContScan(true);
		} else {
			setContScan(false);
		}
	}
	
	@Command
	@NotifyChange({"scanRate"})
	public void changeScanRate() {
		if (_scanRate == 1) {
			setScanRate(0.2);
		} else {
			setScanRate(1);
		}
	}
	
	@Command
	@NotifyChange({"open"})
	public void changeOpen() {
		if (_open) {
			setOpen(false);
		} else {
			setOpen(true);
		}
	}
	
	@Command
	@NotifyChange({"height"})
	public void changeHeight() {
		if (_height.equals("700px")) {
			setHeight("600px");
		} else {
			setHeight("700px");
		}
	}
}
