package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * a mix example of mvp to mvvm.
 * 
 * 
 * @author dennis
 * 
 */
public class MVP2MVVMViewModel {

	private Boolean mWriteProtected;

	private String mTextA;

	public MVP2MVVMViewModel() {
		mWriteProtected = true;
		mTextA = "Start text";
	}

	public Boolean getWriteProtected() {
		return mWriteProtected;
	}

	@Command @NotifyChange("writeProtected")
	public void toggleWriteProtected() {
		mWriteProtected = !mWriteProtected;
	}

	public String getTextA() {
		return mTextA;
	}

	@NotifyChange
	public void setTextA(String textA) {
		mTextA = textA;
	}

}
