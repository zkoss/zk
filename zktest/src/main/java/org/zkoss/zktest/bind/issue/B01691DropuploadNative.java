package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B01691DropuploadNative {
	boolean nativeUpload;

	public boolean isNativeUpload() {
		return nativeUpload;
	}

	public void setNativeUpload(boolean nativeUpload) {
		this.nativeUpload = nativeUpload;
	}
	
	@Command
	@NotifyChange("nativeUpload")
	public void toggle(){
		nativeUpload = !nativeUpload;
	}

}
